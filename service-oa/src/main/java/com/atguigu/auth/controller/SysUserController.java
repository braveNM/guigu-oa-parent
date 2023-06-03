package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-02
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    public SysUserService sysUserService;
    //更改用户状态
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,@PathVariable Integer status){
        sysUserService.updateStatus(id,status);
        return Result.ok();
    }

    //条件分页查询
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){
        //创建一个page对象，传递分页相关参数
        Page<SysUser> pageParam = new Page<>(page,limit);
        //封装条件，判断条件值不为空
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);
        //获取条件值
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        //判断条件是否为空
        if (!StringUtils.isEmpty(username)){
            wrapper.like(SysUser::getUsername,username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);
        }
        IPage<SysUser> pageModel = sysUserService.page(pageParam, wrapper);
        //调用mp的方法实现条件分页查询
        return Result.ok(pageModel);
    }

    /**
     * 查询所有用户
     */
    @ApiOperation("查询所有用户")
    @PostMapping("/findAllUser")
    public Result findAllUser(){
        //http://localhost:8800/admin/system/sysUser/findAllUser
        List<SysUser> allUserList = sysUserService.list();
        return Result.ok(allUserList);
    }

    /**
     * 添加用户
     */
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser sysUser){
        String passwordMD5 = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(passwordMD5);
        boolean isSuccess = sysUserService.save(sysUser);
        if (isSuccess){
            return Result.ok();
        }else {
            return  Result.fail();
        }

    }

    /**
     * 修改用户-根据id查询
     */
    @ApiOperation("根据id查询")
    @PostMapping("get/{id}")
    public Result getUserById(@PathVariable Long id){
        SysUser sysUser = sysUserService.getById(id);
        return Result.ok(sysUser);
    }
    /**
     * 修改用户-最终修改
     */
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody SysUser sysUser){
        boolean isSuccess = sysUserService.updateById(sysUser);
        if (isSuccess){
            return Result.ok();
        }else {
            return  Result.fail();
        }
    }

    /**
     * 根据id删除用户
     */
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean isSuccess = sysUserService.removeById(id);
        if (isSuccess){
            return Result.ok();
        }else {
            return  Result.fail();
        }
    }

    /**
     * 批量删除用户
     */
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result remove(@RequestBody List<Long> idList){
        boolean isSuccess = sysUserService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

}


