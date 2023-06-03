package com.atguigu.auth.service.impl;

import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysUser;
import com.atguigu.auth.mapper.SysUserMapper;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-02
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatus(Long id, Integer status) {
        //1、根据id查询出对应的对象
        SysUser sysUser = baseMapper.selectById(id);
        //1、修改状态码的值
        sysUser.setStatus(status);
        //1、调用修改方法进行修改
        baseMapper.updateById(sysUser);
    }


}
