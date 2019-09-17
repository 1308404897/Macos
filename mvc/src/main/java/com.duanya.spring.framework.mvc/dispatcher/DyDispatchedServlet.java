package com.duanya.spring.framework.mvc.dispatcher;

import com.duanya.spring.common.http.HttpResponsePrintln;
import com.duanya.spring.common.http.result.ResultData;
import com.duanya.spring.common.properties.DyLoadPropeties;
import com.duanya.spring.common.scanner.api.IDyScanner;
import com.duanya.spring.common.scanner.impl.DyScannerImpl;
import com.duanya.spring.common.util.StringUtils;
import com.duanya.spring.common.util.TypeUtil;
import com.duanya.spring.framework.core.bean.factory.bean.manager.DyBeanManager;
import com.duanya.spring.framework.core.load.DyConfigurationLoader;
import com.duanya.spring.framework.mvc.context.DyServletContext;
import com.duanya.spring.framework.mvc.enums.DyMethod;
import com.duanya.spring.framework.mvc.handler.bean.RequestUrlBean;
import com.duanya.spring.framework.mvc.handler.impl.DyHandlerExecution;
import com.duanya.spring.framework.mvc.handler.mapping.HandlerMapping;
import com.duanya.spring.framework.mvc.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Set;

/**
 * @author zheng.liming
 * @date 2019/8/4
 * @description 请求分发器
 */
public class DyDispatchedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final static String DEFAULT_SCANNER_KEY="dy.mvc.scan";

    private static String config;

    private static Set<Class> cl;


    public DyDispatchedServlet() {
        cl=DyBeanManager.getClassContainer();
        try {

            Properties evn=DyConfigurationLoader.getEvn();

            if (null == evn) {
                evn = DyLoadPropeties.doLoadProperties(null, config, this.getClass());
            }

            if (cl == null && DyBeanManager.isLoad()) {
                cl = DyBeanManager.getClassContainer();
            }

            if (cl == null) {
                if (null != evn) {
                    String path = evn.getProperty(DEFAULT_SCANNER_KEY);
                    if (StringUtils.isNotEmptyPlus(path)) {
                        IDyScanner scanner = new DyScannerImpl();
                        cl = scanner.doScanner(path);
                    } else {
                        throw new Exception("请在dy-properties配置dy.mvc.scan（mvc根路径）的值");
                    }
                }
            }

            DyServletContext.load(cl);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.PUT);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.DELETE);

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatched(req, resp, DyMethod.TRACE);
    }

    private void doDispatched(HttpServletRequest req, HttpServletResponse resp, DyMethod method) throws IOException, ServletException {

        setDefaultEncoding(req,resp);

        try {

            HandlerMapping handlerMapping = new HandlerMapping();

            RequestUrlBean bean = handlerMapping.requestMethod(req.getRequestURI(), method);


            if (null == bean) {
                String result = JsonUtil.objectToJson(new ResultData<Object>(404, null, " NOT FOUND "));
                HttpResponsePrintln.writer(resp,result);
                return;
            }

            DyHandlerExecution exec = new DyHandlerExecution();

            Object data = exec.handle(req, resp, bean);

            if (TypeUtil.isBaseType(data.getClass(), true)) {
                resp.setContentType("text/html;charset=UTF-8");
                HttpResponsePrintln.writer(resp,data);
            } else {
                resp.setContentType("application/json;charset=UTF-8");
                HttpResponsePrintln.writer(resp,JsonUtil.objectToJson(data));
            }

        } catch (Exception e) {
            e.printStackTrace();
            String result = JsonUtil.objectToJson(new ResultData<Object>(502, e.getMessage(), " SERVER ERROR "));
            HttpResponsePrintln.writer(resp,result);
        }

    }

    private  void  setDefaultEncoding(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        resp.setHeader("content-type", "text/html;charset=utf-8");
    }


    public static Set<Class> getCl() {
        return cl;
    }

    public static void setCl(Set<Class> cl) {
        DyDispatchedServlet.cl = cl;
    }
}
