package com.iiichz.microservicedevtest.javamethod.utils;

import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.addMethodParamList;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.getGenericSoloParamClass;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.jsonToList;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.jsonToPojoProcess;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.jsonToSet;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.judgeMethodParamList;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.processDiffClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HandlerRpc {

    private static final Gson MAPPER = new GsonBuilder().disableHtmlEscaping().create();

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCUtils.class);

    protected static <T> T jsonToPojoHandlerProcess(Class<T> beanType, Map map, T t, List<Object> handlers)
            throws Exception {
        Set keys = map.keySet();
        for (Object key : keys) {
            String keyTemp = (String) key;
            String subKeyTemp = keyTemp.substring(0, 1).toUpperCase() + keyTemp.substring(1);
            String methodKeySet = "set" + subKeyTemp;
            Object value = map.get(key);
            String valueString = MAPPER.toJson(value);
            Field[] declaredFields = beanType.getDeclaredFields();
            for (Field field : declaredFields) {
                String keyNameField = field.getName();
                for (Object handler : handlers) {
                    String contains = handler.getClass()
                            .getDeclaredMethod("contains", String.class)
                            .invoke(handler, field.getName())
                            .toString();
                    if (keyTemp.equals(keyNameField)) {
                        Class<?> type = field.getType();
                        Object o = null;
                        try {
                            if (contains.equals("true")) {
                                o = processDiffClassHandler(type, field, valueString, handler);
                            } else {
                                o = processDiffClass(type, field, valueString);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Method setMethod = beanType.getDeclaredMethod(methodKeySet, type);
                        setMethod.invoke(t, o);
                    }
                }
            }
        }
        return t;
    }

    protected static void processHandler(Method method, List<Object> methodParamList, List<String> paramMessage,
                                         List<Object> handlers) {
        Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramClass = parameterTypes[i];
            addMethodParamList(paramClass, paramMessage, methodParamList, method, i, handlers);
        }
    }

    protected static Object processDiffClassHandler(Class<?> type, Field field, String valueString, Object handler)
            throws Exception {
        Object o = null;
        String actualClassString = handler.getClass().getDeclaredMethod("getType").invoke(handler).toString();
        Class<?> actualClass = Class.forName(actualClassString);
        if (ParamType.isListParameter(type)) {
            if (getGenericSoloParamClass(field).isInterface()) {
                o = processListParam(valueString, actualClass);
            } else {
                o = jsonToList(valueString, getGenericSoloParamClass(field));
            }
        } else if (ParamType.isSetParameter(type)) {
            if (getGenericSoloParamClass(field).isInterface()) {
                o = processSetParam(valueString, actualClass);
            } else {
                o = jsonToSet(valueString, getGenericSoloParamClass(field));
            }
        } else if (ParamType.isMapParameter(type)) {
            // for(Class cls : getGenericMultiParamClass(field)){
            // not completed
        } else if (type.isArray()) {
            if (type.getComponentType().isInterface()) {
                o = processArrayParam(valueString, actualClass);
            } else {
                o = MAPPER.fromJson(valueString, type);
            }
        } else if (type.isInterface()) {
            o = processInterfaceParam(field, valueString, actualClass);
        } else {
            o = MAPPER.fromJson(valueString, type);
        }
        return o;
    }

    private static List<Object> processListParam(String valueString, Class<?> actualClass) {
        List<Object> list = GsonUtils.parseString2List(valueString, actualClass);
        return list;
    }

    private static Set<Object> processSetParam(String valueString, Class<?> actualClass) {
        Set<Object> set = GsonUtils.parseString2Set(valueString, actualClass);
        return set;
    }

    private static Object[] processArrayParam(String valueString, Class<?> actualClass) {
        return processListParam(valueString, actualClass).toArray();
    }

    private static Object processInterfaceParam(Field field, String valueString, Class<?> actualClass) {
        if (actualClass.getSimpleName().endsWith("Builder")) {
            Object actualParam = null;
            try {
                Object o = jsonToPojoProcess(valueString, actualClass, null);
                Method methodBuild = o.getClass().getDeclaredMethod("build");
                actualParam = methodBuild.invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return actualParam;
        }
        Class<?> type = field.getType();
        GsonBuilder builder = new GsonBuilder();
        InterfaceAdapter ia = new InterfaceAdapter();
        ia.setClassName(actualClass.getName());
        builder.registerTypeAdapter(type, ia);
        Gson gson = builder.create();
        return gson.fromJson(valueString, type);
    }

    protected static boolean isHandlerMethodActualSpecify(Method method, List<String> paramMessage, int index,
                                                          List<Object> handlers) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> paramClass = parameterTypes[index];
        return judgeMethodParamList(paramClass, paramMessage, method, index, handlers);
    }

    protected static boolean canCastHandlerMode(Method method, List<String> paramMessage, List<Object> handlers) {
        if (handlers == null) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramClass = parameterTypes[i];
            boolean judgeRes = judgeMethodParamList(paramClass, paramMessage, method, i, handlers);
            if (!judgeRes) {
                return false;
            }
        }
        return true;
    }
}