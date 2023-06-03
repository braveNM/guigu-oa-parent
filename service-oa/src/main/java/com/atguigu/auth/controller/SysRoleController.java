package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    //注入service
    @Autowired
    private SysRoleService sysRoleService;


    //1、查询所有用户和当前用户所属的角色
    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        Map<String,Object> map = sysRoleService.findRoleByUserId(userId);
        return Result.ok(map);
    }
    //2、为用户分配角色
    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assginRoleVo){
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();

    }


/*    @GetMapping("/findAll")
    public List<SysRole> findAll(){
        //http://localhost:8800/admin/system/sysRole/findAll
        //调用service中的方法
        List<SysRole> list = sysRoleService.list();
        return list;
    }*/

    /**
     * 统一返回数据结果
     * @return
     */
      @ApiOperation("查询所有的角色")
      @GetMapping("/findAll")
      public Result findAll(){
          //http://localhost:8800/admin/system/sysRole/findAll
          //调用service中的方法
          List<SysRole> list = sysRoleService.list();
          //模拟异常
          /*try {
              int i = 10/0;
          } catch (Exception e) {
              throw new GuiguException(20001,"执行了自定义异常处理");
          }*/
          return Result.ok(list);
      }

    /**
     * 条件分页查询
     * page:当前页
     * limit:每页显示记录数
     * SysRoleQueryVo:查询条件对象
     */
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){

        //调用service中的方法
        //1、创建一个page对象，传递分页相关参数
        Page<SysRole> pageParm = new Page<>(page,limit);
        //2、封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysRole::getCreateTime);
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)){
            //封装条件
            wrapper.like(SysRole::getRoleName,roleName);

        }
        //3、调用方法分页查询
        IPage<SysRole> pageModel = sysRoleService.page(pageParm, wrapper);
        System.out.println("pageModel = " + pageModel.getRecords());
        return Result.ok(pageModel);
    }

    /**
     * 添加角色
     */
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){//通过json格式传递数据
        boolean isSuccess = sysRoleService.save(role);
        //System.out.println("isSuccess = " + isSuccess);
        if (isSuccess) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 修改角色-根据id查询
     */
    @ApiOperation("根据id查询")
    @PostMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }
    /**
     * 修改角色-最终修改
     */
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){//通过json格式传递数据
        boolean isSuccess = sysRoleService.updateById(role);
        //System.out.println("isSuccess = " + isSuccess);
        if (isSuccess) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 根据id删除角色
     */
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        boolean isSuccess = sysRoleService.removeById(id);
        System.out.println("isSuccess = " + isSuccess);
        if (isSuccess) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }


    /**
     * 批量删除角色
     */
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        boolean isSuccess = sysRoleService.removeByIds(idList);
        //System.out.println("isSuccess = " + isSuccess);
        if (isSuccess) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }


}
