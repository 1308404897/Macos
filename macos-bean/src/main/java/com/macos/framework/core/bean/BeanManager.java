package com.macos.framework.core.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * @Desc bean class全局管理
 * @Author Zheng.LiMing
 * @Date 2019/9/4
 */
public class BeanManager {

    private static volatile Set<Class> classContainer=new HashSet<>();;

    public static void registerClassBySet(Set<Class> result){
            classContainer.addAll(result);
    }

    public static Set<Class> getClassContainer() {
        return classContainer;
    }

    public static boolean isLoad(){
        return classContainer.size()>0;
    }

}
