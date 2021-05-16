package com.iiichz.microservicedevtest.javamethod.utils;

import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.addMethodParamList;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.judgeMethodParamList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
import java.util.List;

public class NormalRpc {

    private static final Gson MAPPER = new GsonBuilder().disableHtmlEscaping().create();

    protected static boolean canCastNormalMode(Method method, List<String> paramMessage) {
        Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramClass = parameterTypes[i];
            boolean judgeRes = judgeMethodParamList(paramClass, paramMessage, method, i, null);
            if (!judgeRes) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isNormalMethodActualSpecify(Method method, List<String> paramMessage, int index) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> paramClass = parameterTypes[index];
        return judgeMethodParamList(paramClass, paramMessage, method, index, null);
    }

    protected static void processNormal(Method method, List<Object> methodParamList, List<String> paramMessage) {
        Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramClass = parameterTypes[i];
            addMethodParamList(paramClass, paramMessage, methodParamList, method, i, null);
        }
    }
}