package com.iiichz.microservicedevtest.javamethod.api;


import com.iiichz.devboot.init.SpringBeanUtil;
import com.iiichz.microservicedevtest.javamethod.utils.ParamUtils;
import com.iiichz.microservicedevtest.javamethod.utils.RPCUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service
public class Rpc {
    private static final Logger LOGGER = LoggerFactory.getLogger(Rpc.class);

    @PUT
    @Consumes( {"application/json"})
    @Produces( {"application/json"})
    public Object process(String message) throws Exception {
        String methodMessage = RPCUtils.getMethodMessage(message);
        List<String> paramMessage = RPCUtils.getParamMessage(message);
        String paramMode = RPCUtils.getParamMode(message);
        List<Integer> builderIndex = RPCUtils.getBuilderIndex(message);
        List<Object> handlers = RPCUtils.getHandlers(message);
        String beanMode = RPCUtils.getBeanMode(message);
        //获取全路径类名和方法名
        String className = StringUtils.substringBeforeLast(methodMessage, ".");
        String methodName = StringUtils.substringAfterLast(methodMessage, ".");
        Class<?> cls = Class.forName(className);

        Object res = null;
        if (paramMessage == null) {
            res = processEmptyParamMethod(cls, methodName);
        } else {
            res = processValueParamMethod(cls, className, paramMessage, paramMode, builderIndex, handlers, beanMode,
                    methodName);
        }
        return res;
    }

    private Object processEmptyParamMethod(Class<?> cls, String methodName) throws Exception {
        Method method = null;
        try {
            method = cls.getDeclaredMethod(methodName);
            method.setAccessible(true);
        } catch (Exception e) {
            LOGGER.error("Cannot find the method which has empty param, please input correct param!", e);
        }
        Constructor declaredConstructor = cls.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Object o = declaredConstructor.newInstance();
        return Optional.ofNullable(method).orElseThrow(() -> new Exception("can not get the target method")).invoke(o);
    }

    private Object processValueParamMethod(Class<?> cls, String className, List<String> paramMessage, String paramMode,
                                           List<Integer> builderIndex, List<Object> handlers, String beanMode, String methodName) throws Exception {
        //methodParamList是json转换成的方法的参数列表
        List<Object> methodParamList = RPCUtils.getMethodParam(className, methodName, paramMessage, paramMode,
                builderIndex, handlers);
        Class<?>[] cArg = ParamUtils.getMethodParamTypes(cls, methodName, paramMessage, builderIndex, handlers);
        Method method = null;
        try {
            method = cls.getDeclaredMethod(methodName, cArg);
            method.setAccessible(true);
        } catch (Exception e) {
            LOGGER.error("Can't find the method, Please check your method param!", e);
        }
        Object[] paramObj = ParamUtils.getMethodParamObj(method, methodParamList);
        Object bean;
        if (beanMode != null && beanMode.equals("springBean")) {
            bean = Optional.of(SpringBeanUtil.getBean(cls))
                    .orElseThrow(() -> new Exception("Can not get spring bean! Please inject the bean!"));
        } else {
            bean = cls.newInstance();
        }
        return Optional.ofNullable(method)
                .orElseThrow(() -> new Exception("can not find the method!"))
                .invoke(bean, paramObj);
    }
}