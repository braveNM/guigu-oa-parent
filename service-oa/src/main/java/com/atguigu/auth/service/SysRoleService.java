package com.atguigu.auth.service;

import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {

    //1、查询所有用户和当前用户所属的角色
    Map<String, Object> findRoleByUserId(Long userId);

    //2、为用户分配角色
    void doAssign(AssignRoleVo assginRoleVo);
}
