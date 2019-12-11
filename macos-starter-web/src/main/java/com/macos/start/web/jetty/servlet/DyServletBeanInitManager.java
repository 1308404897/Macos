package com.macos.start.web.jetty.servlet;



import com.macos.common.util.StringUtils;
import com.macos.framework.annotation.WebServlet;
import com.macos.framework.context.ApplicationContextImpl;
import com.macos.framework.context.base.ApplicationContextApi;
import com.macos.framework.core.bean.factory.BeanFactory;
import com.macos.framework.core.bean.factory.ValueFactory;
import com.macos.framework.core.load.conf.ConfigurationLoader;

import javax.servlet.Servlet;
import java.util.HashSet;
import java.util.Set;

/**
 * @Desc DyServletBeanInitManager
 * @Author Zheng.LiMing
 * @Date 2019/9/11
 */
public class DyServletBeanInitManager {

    private Set<DyServletBean> servletBeans=new HashSet<>();

    private ApplicationContextApi applicationContext= ApplicationContextImpl.Builder.getDySpringApplicationContext();

    public void init(Set<Class> classes){
        for (Class c:classes){
            if (c.isAnnotationPresent(WebServlet.class)){
                WebServlet webServlet=(WebServlet) c.getAnnotation(WebServlet.class);
                String url=webServlet.url();
                registerServlet(c,url);
            }
        }
    }

    public void registerServlet(Class servlet,String url){

        if (hasServlet(servlet)){
            return;
        }

        try {
            Servlet servlet1=(Servlet) BeanFactory.createNewBean(servlet);
            ValueFactory.doFields(servlet1, ConfigurationLoader.getEvn());
            DyServletBean dyServletBean=new DyServletBean(servlet1,url);
            servletBeans.add(dyServletBean);
            //交给applicationContext管理，因为Servlet有可能需要注入
            applicationContext.registerBean(StringUtils.toLowerCaseFirstName(servlet.getSimpleName()),servlet1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public boolean hasServlet(Class servlet){
        for (DyServletBean servletBean:servletBeans){
            if (servletBean.getServlet().getClass()==servlet){
                return true;
            }
        }
        return false;
    }

    public Set<DyServletBean> getServletBeans() {
        return servletBeans;
    }

    private DyServletBeanInitManager(){

    }

    public static class Builder{
        private final static DyServletBeanInitManager dyServletBeanInitManager=new DyServletBeanInitManager();
        public static DyServletBeanInitManager getDyServletBeanInitManager(){
            return dyServletBeanInitManager;
        }
    }
}
