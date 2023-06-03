package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDeom1 {

    //注入
    @Autowired
    private SysRoleMapper mapper;

    @Test
    public void getAll(){
        //查询所有记录
        List<SysRole> list = mapper.selectList(null);
        System.out.println("list = " + list);
    };

    @Test
    public void add(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setDescription("角色管理员");
        sysRole.setRoleCode("role");

        int rows = mapper.insert(sysRole);
        System.out.println("sysRole = " + sysRole);
        System.out.println("sysRole = " + sysRole.getId());
        System.out.println("rows = " + rows);
    }

    @Test
    public void update(){
        //先根据id查询
        SysRole sysRole = mapper.selectById(10);
        //设置修改
        sysRole.setRoleName("atguigu管理员");
        //调用修改方法
        int rows = mapper.updateById(sysRole);
        System.out.println("rows = " + rows);
        System.out.println("sysRole = " + sysRole.getRoleName());
    }

    @Test
    public void delete(){
        int rows = mapper.deleteById(10);
        System.out.println("rows = " + rows);

    }

    @Test
    public void deleteBatchIds(){
        int rows = mapper.deleteBatchIds(Arrays.asList(1, 2));
        System.out.println("rows = " + rows);
    }

    //条件查询
    @Test
    public void selectByQueryWrapper(){
        //创建queryWrapper对象，封装条件
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper();
        queryWrapper.eq("role_name","总经理");
        List selectList = mapper.selectList(queryWrapper);
        System.out.println("selectList = " + selectList);
    }
    //条件查询
    @Test
    public void selectByQueryWrapper2(){
        //创建queryWrapper对象，封装条件
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper();
        queryWrapper.like("role_name","理");
        List selectList = mapper.selectList(queryWrapper);
        System.out.println("selectList = " + selectList);
    }

    @Test
    public void selectByLambdaQueryWrapper(){
        //创建queryWrapper对象，封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"总经理");
        List selectList = mapper.selectList(wrapper);
        System.out.println("selectList = " + selectList);
    }
}
