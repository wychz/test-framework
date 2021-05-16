package com.iiichz.microservicedevtest.javamethod.utils;

import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.addMethodParamListFromBuilderList;
import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.canCastBuilderMode;
import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.isBuilderMethodActualSpecify;
import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.jsonToPojoBuilderProcess;
import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.ofBuilderProcess;
import static com.iiichz.microservicedevtest.javamethod.utils.BuilderRpc.processBuilder;
import static com.iiichz.microservicedevtest.javamethod.utils.HandlerRpc.canCastHandlerMode;
import static com.iiichz.microservicedevtest.javamethod.utils.HandlerRpc.isHandlerMethodActualSpecify;
import static com.iiichz.microservicedevtest.javamethod.utils.HandlerRpc.jsonToPojoHandlerProcess;
import static com.iiichz.microservicedevtest.javamethod.utils.HandlerRpc.processHandler;
import static com.iiichz.microservicedevtest.javamethod.utils.NormalRpc.canCastNormalMode;
import static com.iiichz.microservicedevtest.javamethod.utils.NormalRpc.isNormalMethodActualSpecify;
import static com.iiichz.microservicedevtest.javamethod.utils.NormalRpc.processNormal;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isBasicParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isListParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isMapParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isPojoParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isSetParameter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RPCUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCUtils.class);

    private static final Gson MAPPER = new GsonBuilder().disableHtmlEscaping().create();

    public static String getMethodMessage(String message) {
        LOGGER.warn("start to parse request json to get the method!");
        String methodMessage = null;
        JSONObject jsonObject = JSON.parseObject(message);
        Object objMethod = jsonObject.get("method");
        if (objMethod != null) {
            methodMessage = objMethod.toString();
        }
        return methodMessage;
    }

    public static List<String> getParamMessage(String message) {
        LOGGER.warn("start to parse request json to get the param!");
        JSONObject jsonObject = JSON.parseObject(message);
        List<Object> paramList = new ArrayList<>();
        Object objTemp;
        objTemp = jsonObject.get("param");
        if (objTemp == null) {
            if (jsonObject.get("param1") == null) {
                return null;
            }
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                objTemp = jsonObject.get("param" + i);
                if (objTemp == null) {
                    break;
                }
                paramList.add(objTemp);
            }
        } else {
            paramList.add(objTemp);
        }

        List<String> paramMessage = new ArrayList<>();
        for (Object o : paramList) {
            String paramMessageTemp = o.toString();
            paramMessage.add(paramMessageTemp);
        }
        return paramMessage;
    }

    public static String getParamType(String message) {
        LOGGER.warn("start to parse request json to get the param type!");
        String paramType = null;
        JSONObject jsonObject = JSON.parseObject(message);
        Object objMethod = jsonObject.get("paramType");
        if (objMethod != null) {
            paramType = objMethod.toString();
        }
        return paramType;
    }

    public static String getParamMode(String message) {
        LOGGER.warn("start to parse request json to get the param Mode!");
        String paramMode = null;
        JSONObject jsonObject = JSON.parseObject(message);
        Object objMode = jsonObject.get("paramMode");
        if (objMode != null) {
            paramMode = objMode.toString();
        }
        return paramMode;
    }

    public static String getBeanMode(String message) {
        LOGGER.warn("start to parse request json to get the bean Mode!");
        String beanMode = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Object objBeanMode = jsonObject.get("beanMode");
        if (objBeanMode != null) {
            beanMode = objBeanMode.toString();
        }
        return beanMode;
    }

    public static List<Integer> getBuilderIndex(String message) {
        LOGGER.warn("start to parse request json to get the param builder_index!");
        List<Integer> builderIndex = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(message);
        JSONArray objIndex = (JSONArray) jsonObject.get("builderIndex");
        if (objIndex != null) {
            List<Integer> objIndexTemp = objIndex.toJavaList(Integer.class);
            builderIndex.addAll(objIndexTemp);
        }
        return builderIndex;
    }

    public static String getHandlerName(String message) {
        LOGGER.warn("start to parse request json to get the HandlerName!");
        String handlerName = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Object objHandlerName = jsonObject.get("handler");
        if (objHandlerName != null) {
            handlerName = objHandlerName.toString();
        }
        return handlerName;
    }

    public static List<Object> getHandlers(String message) {
        List<Object> handlers = null;
        String handlerNamesRes = getHandlerName(message);
        if (handlerNamesRes != null) {
            handlers = new ArrayList<>();
            String[] handlerNames = handlerNamesRes.split(",");
            for (String handlerName : handlerNames) {
                Object handler = null;
                if (handlerName != null) {
                    try {
                        handler = Class.forName(handlerName).newInstance();
                    } catch (Exception e) {
                        LOGGER.error("Exception: ", e);
                    }
                }
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public static List<Object> getMethodParam(String className, String methodNameMessage, List<String> paramMessage,
                                              String paramMode, List<Integer> builderIndex, List<Object> handlers) {
        LOGGER.warn("start to cast param json to actual method param!");
        List<Object> methodParamList = new ArrayList<>();
        List<Object> methodParamBuilderList = new ArrayList<>();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Can't find the class, Please check your method string!", e);
        }
        assert clazz != null;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            String methodName = method.getName();
            if (canFindActualMethod(methodName, methodNameMessage, method, paramMessage)) {     //能够找到名称和参数类型的对应重载方法
                if (canCastBuilderMode(paramMode, method, paramMessage, builderIndex, handlers)) {        //使用Builder模式
                    processBuilder(method, methodParamBuilderList, paramMessage, builderIndex, handlers);
                    addMethodParamListFromBuilderList(methodParamBuilderList, methodParamList, builderIndex);
                } else if (canCastHandlerMode(method, paramMessage, handlers)) {            //使用Handler模式
                    processHandler(method, methodParamList, paramMessage, handlers);
                } else if (canCastNormalMode(method, paramMessage)) {             //使用普通模式
                    processNormal(method, methodParamList, paramMessage);
                }
            }
        }
        return methodParamList;
    }

    protected static <T> T jsonToPojo(String jsonData, Class<T> beanType, List<Object> handlers) {
        try {
            return jsonToPojoProcess(jsonData, beanType, handlers);
        } catch (Exception e) {
            LOGGER.error("Can't cast json request to method param, Please check your param!", e);
        }
        return null;
    }

    protected static <T> boolean canJsonToPojo(String jsonData, Class<T> beanType, List<Object> handlers) {
        try {
            jsonToPojoProcess(jsonData, beanType, handlers);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected static <T> List<T> jsonToList(String jsonData, Class<T> beanType) throws Exception {
        return GsonUtils.parseString2List(jsonData, beanType);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     LOGGER.error("Can't cast json request to method param, Please check your param!");
        // }
        // return null;
    }

    protected static <T> boolean canJsonToList(String jsonData, Class<T> beanType) {
        try {
            GsonUtils.parseString2List(jsonData, beanType);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected static <T> Set jsonToSet(String jsonData, Class<T> beanType) throws Exception {
        // try {
        //     return GsonUtils.parseString2Set(jsonData, beanType);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     LOGGER.error("Can't cast json request to method param, Please check your param!");
        // }
        // return null;
        return GsonUtils.parseString2Set(jsonData, beanType);
    }

    protected static <T> boolean canJsonToSet(String jsonData, Class<T> beanType) {
        try {
            GsonUtils.parseString2Set(jsonData, beanType);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected static <K, V> Map jsonToMap(String jsonData, Class<K> beanTypeKey, Class<V> beanTypeValue)
            throws Exception {
        // JSON.parseObject(jsonData, new TypeReference<Map<K, V>>() { });
        // try {
        //     return GsonUtils.parseString2Map(jsonData, beanTypeKey, beanTypeValue);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     LOGGER.error("Can't cast json request to method param, Please check your param!");
        // }
        // return null;
        return GsonUtils.parseString2Map(jsonData, beanTypeKey, beanTypeValue);
    }

    protected static <K, V> boolean canJsonToMap(String jsonData, Class<K> beanTypeKey, Class<V> beanTypeValue) {
        try {
            GsonUtils.parseString2Map(jsonData, beanTypeKey, beanTypeValue);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected static <T> T jsonToPojoProcess(String jsonData, Class<T> beanType) throws Exception {
        return jsonToPojoProcess(jsonData, beanType, null);
    }

    protected static <T> T jsonToPojoProcess(String jsonData, Class<T> beanType, List<Object> handlers)
            throws Exception {
        String name = beanType.getName();
        T t = null;
        if (ofBuilderProcess(name)) {
            HashMap map = MAPPER.fromJson(jsonData, HashMap.class);
            T temp = beanType.newInstance();
            t = jsonToPojoBuilderProcess(beanType, map, temp, handlers);
        } else if (handlers != null && handlers.size() > 0) {
            HashMap map = MAPPER.fromJson(jsonData, HashMap.class);
            Constructor<T> declaredConstructor = beanType.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            T temp = declaredConstructor.newInstance();
            t = jsonToPojoHandlerProcess(beanType, map, temp, handlers);
        } else {
            try {
                t = MAPPER.fromJson(jsonData, beanType);
            } catch (Exception e) {
                HashMap map = MAPPER.fromJson(jsonData, HashMap.class);
                T temp = beanType.newInstance();
                t = jsonToPojoBuilderProcess(beanType, map, temp, handlers);
            }
        }
        return t;
    }

    protected static void addMethodParamList(Class<?> cls, List<String> paramMessage, List<Object> methodParamList,
                                             Method method, int i, List<Object> handlers) {
        try {
            if (isBasicParameter(cls)) {
                Object methodParam = ParamUtils.getBasicMethodParam(paramMessage.get(i), cls);
                methodParamList.add(methodParam);
            }
            if (isListParameter(cls)) {
                Class<?> paramClass = ParamUtils.getListParameterType(method, i);
                Object methodParam = jsonToList(paramMessage.get(i), paramClass);
                methodParamList.add(methodParam);
            }
            if (isPojoParameter(cls)) {
                Class<?> paramClass = ParamUtils.getPojoParameterType(cls);
                Object methodParam = jsonToPojo(paramMessage.get(i), paramClass, handlers);
                methodParamList.add(methodParam);
            }
            if (isMapParameter(cls)) {
                List<Class<?>> paramClassList = ParamUtils.getMapParameterType(method, i);
                Object methodParam = jsonToMap(paramMessage.get(i), paramClassList.get(0), paramClassList.get(1));
                methodParamList.add(methodParam);
            }
            if (isSetParameter(cls)) {
                Class<?> paramClass = ParamUtils.getSetParameterType(method, i);
                Object methodParam = jsonToSet(paramMessage.get(i), paramClass);
                methodParamList.add(methodParam);
            }
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
    }

    protected static Object processDiffClass(Class<?> type, Field field, String valueString) throws Exception {
        Object o = null;
        if (ParamType.isListParameter(type)) {
            o = jsonToList(valueString, getGenericSoloParamClass(field));
        } else if (ParamType.isSetParameter(type)) {
            o = jsonToSet(valueString, getGenericSoloParamClass(field));
        } else if (ParamType.isMapParameter(type)) {
            // for(Class cls : getGenericMultiParamClass(field)){
            // not completed
        } else if (type.isArray()) {
            o = MAPPER.fromJson(valueString, type);
        } else {
            o = MAPPER.fromJson(valueString, type);
        }
        return o;
    }

    protected static boolean isAllMethodActual(Method method, List<String> paramMessage, List<Integer> builderIndex) {
        return isAllMethodActual(method, paramMessage, builderIndex, null);
    }

    protected static boolean isAllMethodActual(Method method, List<String> paramMessage, List<Integer> builderIndex,
                                               List<Object> handlers) {
        Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
        boolean[] actualJudge = new boolean[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (builderIndex.contains(i)) {
                actualJudge[i] = isBuilderMethodActualSpecify(method, paramMessage, builderIndex, i, handlers);
            } else if (handlers != null && handlers.size() > 0) {
                actualJudge[i] = isHandlerMethodActualSpecify(method, paramMessage, i, handlers);
            } else {
                actualJudge[i] = isNormalMethodActualSpecify(method, paramMessage, i);
            }
        }
        for (boolean b : actualJudge) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    protected static boolean judgeMethodParamList(Class<?> cls, List<String> paramMessage, Method method, int i,
                                                  List<Object> handlers) {
        if (isBasicParameter(cls)) {
            return ParamUtils.canGetBasicMethodParam(paramMessage.get(i), cls);
        }
        if (isListParameter(cls)) {
            Class<?> paramClass = ParamUtils.getListParameterType(method, i);
            return canJsonToList(paramMessage.get(i), paramClass);
        }
        if (isPojoParameter(cls)) {
            Class<?> paramClass = ParamUtils.getPojoParameterType(cls);
            return canJsonToPojo(paramMessage.get(i), paramClass, handlers);
        }
        if (isMapParameter(cls)) {
            List<Class<?>> paramClassList = ParamUtils.getMapParameterType(method, i);
            return canJsonToMap(paramMessage.get(i), paramClassList.get(0), paramClassList.get(1));
        }
        if (isSetParameter(cls)) {
            Class<?> paramClass = ParamUtils.getSetParameterType(method, i);
            return canJsonToSet(paramMessage.get(i), paramClass);
        }
        return true;
    }

    /**
     * 得到field的泛型参数
     *
     * @param field 某个field
     * @return 得到对应的泛型参数
     */
    protected static Class<?> getGenericSoloParamClass(Field field) {
        Type genericType = field.getGenericType();
        Class<?> genericClass = null;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            genericClass = (Class<?>) pt.getActualTypeArguments()[0];
        }
        return genericClass;
    }

    private static boolean canFindActualMethod(String methodName, String methodNameMessage, Method method,
                                               List<String> paramMessage) {
        return methodName.equals(methodNameMessage) && method.getParameterTypes().length == paramMessage.size();
    }
}