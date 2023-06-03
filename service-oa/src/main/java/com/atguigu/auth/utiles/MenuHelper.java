package com.atguigu.auth.utiles;

import com.atguigu.model.system.SysMenu;
import com.sun.org.apache.xerces.internal.xs.LSInputList;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {

    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {

        List<SysMenu> trees = new ArrayList<>();//trees是最终的树形集合
        for (SysMenu sysmenu:sysMenuList) {
            if (sysmenu.getParentId().longValue() == 0){
                //递归入口
                trees.add(getChildren(sysmenu,sysMenuList));
            }
        }
        return trees;
    }

    public static SysMenu getChildren(SysMenu sysMenu,List<SysMenu> sysMenuList){
        sysMenu.setChildren(new ArrayList<>());

        for (SysMenu item:sysMenuList) {
            if (sysMenu.getId() == item.getParentId()){
                //找打了下一层
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }

                //SysMenu children = getChildren(item, sysMenuList);
                //sysMenu.getChildren().add(children);

                sysMenu.getChildren().add(getChildren(item, sysMenuList));
            }
        }
        return sysMenu;
    }
}
