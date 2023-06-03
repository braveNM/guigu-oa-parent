package com.atguigu.auth.service.impl;


import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.mapper.SysRoleMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysRoleMenuService;
import com.atguigu.auth.utiles.MenuHelper;
import com.atguigu.common.execption.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.system.AssignMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-07
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    //根据用户id查询出用户可以操作的菜单列表
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1、判断用户是否是管理员 userId=1是管理员
        if (userId.longValue() == 1){
            //1.1、若是管理员 返回所有菜单数据
            LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
            wrapperSysMenu.eq(SysMenu::getStatus,1);
            wrapperSysMenu.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapperSysMenu);
        }else {
            //1.2、若不是管理员，则查询用户可以操作的菜单列表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //多表关联查询  用户角色表  角色菜单表  菜单表

        //2、将查询出来的数据构建成为框架需要的路由数据结构
        //先构建成为树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建成框架要求的路由结构
        List<RouterVo> routerList = this.buileRouter(sysMenuTreeList);
        return routerList;
    }

    //将树形结构数据sysMenuTreeList构建成为框架需要的路由数据结构
    private List<RouterVo> buileRouter(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu:menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));

            //下一层级路由做判断  将隐藏路由显示出来
            List<SysMenu> children = menu.getChildren();
            if (menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item ->
                        !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu:hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else {
                if (!CollectionUtils.isEmpty(children)){
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buileRouter(children));//递归向下查找
                }
            }
            routers.add(router);
        }
        return routers;
    }
    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    //根据用户id查询出用户可以操作的按钮列表
    @Override
    public List<String> findUserPermsListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1、判断用户是否是管理员 userId=1是管理员
        if (userId.longValue() == 1){
            //1.1、若是管理员 返回所有菜单数据
            LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
            wrapperSysMenu.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapperSysMenu);
        }else {
            //1.2、若不是管理员，则查询用户可以操作的菜单列表
            sysMenuList= baseMapper.findMenuListByUserId(userId);
        }
        //多表关联查询  用户角色表  角色菜单表  菜单表
        List<String> permsList = sysMenuList.stream().filter(item -> item.getType() == 2)
                                                     .map(item -> item.getPerms())
                                                     .collect(Collectors.toList());

        //2、将查询出来的数据构建成为框架需要的数据
        return permsList;
    }

    //根据角色获取菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1、获取所有的菜单集合 （stasus=1）
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus,1);
        List<SysMenu> allMenuList = baseMapper.selectList(wrapperSysMenu);

        //2、根据角色id查询 角色菜单关系表，查出角色对应的所有菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        //3、根据当前角色菜单对象，获取菜单id
        List<Long> menuIdList = roleMenuList.stream()
                .map(c -> c.getMenuId()).collect(Collectors.toList());

        //4、拿着菜单id和所有菜单集合对比，如果相同则封装
        allMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())){
                item.setSelect(true);
            }else {
                item.setSelect(false);
            }
        });
        //5、返回树形集合
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allMenuList);

        return sysMenuList;
    }

    //给角色分配权限
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //根据角色id，如果角色之前有菜单数据。先删除之前的菜单数据
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapperSysRoleMenu);

        //从assignMenuVo中获取当前角色对应的菜单id列表，遍历添加
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId:menuIdList) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuService.save(sysRoleMenu);
        }

    }

    /**
     * 菜单列表接口
     */
    @Override
    public List<SysMenu> findNodes() {
        //查询所有的菜单数据，此时的菜单数据不是树形的，需要转成树形的
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    //删除菜单
    @Override
    public void removeMenuById(Long id) {
        //当前菜单是是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0){
            //存在子菜单
            throw new GuiguException(201,"当前菜单不可删除！");
        }
        baseMapper.deleteById(id);

    }


}
