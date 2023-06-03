package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssignRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        //1、查询所有角色，返回list集合
        List<SysRole> allRolesList = baseMapper.selectList(null);
        //2、根据用户id查询角色用户关系表。查询用户id对应的所有的角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existRoleList = sysUserRoleService.list(wrapper);
        /**
         * 此时的existRoleList是userId对应的所有的角色对象，而需要的是所有的角色id，则遍历
         */
        List<Long> existRoleIDList =
                existRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //对角色进行分类
        //3、根据查询出的所有的角色id，找到相对应的角色对象
        //根据角色id到所有橘色的list集合里面对比 assignRoleList allRolesList
        List<SysRole> assignRoleList = new ArrayList();
        for (SysRole sysRole:allRolesList) {
            if (existRoleList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }

        //4、得到的两部分数据封装到map集合返回
        Map<String,Object> map = new HashMap<>();
        map.put("assignRoleList",assignRoleList);
        map.put("allRolesList",allRolesList);
        return map;
    }

    @Override
    public void doAssign(AssignRoleVo assginRoleVo) {
        //把用户之前分配角色的数据删除 用户角色关系表里面，根据id删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        //重新分配数据,即往SysUserRole表里添加数据
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId:roleIdList) {
            if (StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }

    }
}
