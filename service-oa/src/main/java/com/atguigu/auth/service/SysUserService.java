package com.atguigu.auth.service;

import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-02
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);


}
