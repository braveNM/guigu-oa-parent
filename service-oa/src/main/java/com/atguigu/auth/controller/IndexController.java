package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.execption.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    //login
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        //{"code":20000,"data":{"token":"admin-token"}}
/*        Map<String,Object> map = new HashMap<>();
        map.put("token","admin-token");
        return Result.ok(map);*/
        //1、获取输入的用户名和密码
        String password = loginVo.getPassword();
        String username = loginVo.getUsername();
        //2、根据用户名查询数据库
        LambdaQueryWrapper<SysUser> wrapperSysUser = new LambdaQueryWrapper<>();
        wrapperSysUser.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapperSysUser);
        //3、用户信息是否存在
        if (sysUser == null) {
            throw new GuiguException(201,"用户不存在");
        }
        //4、判断密码
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());
        if (!password_db.equals(password_input)){
            throw new GuiguException(204,"密码错误!");
        }
        //5、判断用户是否被禁用
        if (sysUser.getStatus() == 0){
            throw new GuiguException(205,"该用户已被禁用");
        }
        //6、根据用户id和username 使用token生成字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        //7、返回
        Map<String,Object> map = new HashMap<>();
        System.out.println("token = " + token);
        System.out.println("git3 map = " + map);
        map.put("token",token);
        return Result.ok(map);
    }

    //info
    @GetMapping("info")
    public Result info(HttpServletRequest request){
        //1、从请求头中获取token
        String token = request.getHeader("token");
        //2、从token中获取用户id
        Long userId = JwtHelper.getUserId(token);
        //3、通过用户id查询用户信息
        SysUser sysUser = sysUserService.getById(userId);
        //4、根据用户id查询出用户可以操作的菜单列表
        //查询数据库动态构建路由结构
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        //5、根据用户id查询出用户可以操作的按钮列表
        List<String> permsList = sysMenuService.findUserPermsListByUserId(userId);
        System.out.println("permsList = " + permsList);
        System.out.println("routerList = " + routerList);
        //6、返回相应的数据

        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers",routerList);
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    //退出
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }

}
