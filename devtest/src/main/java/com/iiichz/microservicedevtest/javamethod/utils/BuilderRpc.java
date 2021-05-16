package com.iiichz.microservicedevtest.javamethod.utils;

import static com.iiichz.microservicedevtest.javamethod.utils.HandlerRpc.processDiffClassHandler;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamUtils.getMapParameterTypeField;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.getGenericSoloParamClass;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.isAllMethodActual;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.jsonToList;
import static com.iiichz.microservicedevtest.javamethod.utils.RPCUtils.jsonToPojo;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuilderRpc {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderRpc.class);
    private static final Gson MAPPER = new GsonBuilder().disableHtmlEscaping().create();

    protected static boolean isBuilderMethodActualSpecify(Method method, List<String> paramMessage,
                                                          List<Integer> builderIndex, int index, List<Object> handlers) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?>[] paramClassBuilders = new Class[parameterTypes.length];
        if (builderIndex.contains(index)) {
            String className = parameterTypes[index].getName();
            try {
                paramClassBuilders[index] = Class.forName(className + "Builder");
            } catch (ClassNotFoundException e) {
                return false;
            }
            return judgeMethodParamList(paramClassBuilders[index], paramMessage, method, index, handlers);
        }
        return true;
    }

    protected static void processBuilder(Method method, List<Object> methodParamBuilderList, List<String> paramMessage,
                                         List<Integer> builderIndex, List<Object> handlers) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (builderIndex.contains(i)) {
                Class<?> paramClass = parameterTypes[i];
                String cls = paramClass.getName();
                Class<?> paramClassBuilder = null;
                try {
                    paramClassBuilder = Class.forName(cls + "Builder");
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Exception: ", e);
                }
                addMethodParamBuilderList(paramClassBuilder, paramMessage, methodParamBuilderList, i, handlers);
            } else {
                Class<?> paramClass = parameterTypes[i];
                addMethodParamBuilderList(paramClass, paramMessage, methodParamBuilderList, i, handlers);
            }
        }
    }

    private static void addMethodParamBuilderList(Class<?> cls, List<String> paramMessage,
                                                  List<Object> methodParamBuilderList, int i, List<Object> handles) {
        Class<?> paramClass = ParamUtils.getPojoParameterType(cls);
        Object methodParam = jsonToPojo(paramMessage.get(i), paramClass, handles);
        methodParamBuilderList.add(methodParam);
    }

    protected static void addMethodParamListFromBuilderList(List<Object> methodParamBuilderList,
                                                            List<Object> methodParamList, List<Integer> builderIndex) {
        for (int i = 0; i < methodParamBuilderList.size(); i++) {
            try {
                if (builderIndex.contains(i)) {
                    Method methodBuild = methodParamBuilderList.get(i).getClass().getDeclaredMethod("build");
                    Object actualParam = methodBuild.invoke(methodParamBuilderList.get(i));
                    methodParamList.add(actualParam);
                } else {
                    methodParamList.add(methodParamBuilderList.get(i));
                }
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
    }

    protected static <T> T jsonToPojoBuilderProcess(Class<T> beanType, Map map, T t, List<Object> handlers)
            throws Exception {
        Set keys = map.keySet();
        for (Object key : keys) {
            String keyTemp = (String) key;
            String methodKeySet;
            if (keyTemp.contains("augmentation")) {
                String subKeyTemp = keyTemp.substring(0, 1).toUpperCase() + keyTemp.substring(1);
                methodKeySet = "add" + subKeyTemp;
            } else if (keyTemp.startsWith("_")) {
                String subKeyTemp = keyTemp.substring(1, 2).toUpperCase() + keyTemp.substring(2);
                methodKeySet = "set" + subKeyTemp;
            } else {
                String subKeyTemp = keyTemp.substring(0, 1).toUpperCase() + keyTemp.substring(1);
                methodKeySet = "set" + subKeyTemp;
            }
            Object value = map.get(key);
            String valueString = MAPPER.toJson(value);
            Field[] declaredFields = beanType.getDeclaredFields();
            for (Field field : declaredFields) {
                String keyNameField = field.getName();
                if (keyTemp.equals(keyNameField)) {
                    Class<?> type = field.getType();
                    Object o = null;
                    if (isRecursive(type, field, valueString, handlers)) {
                        o = processRecursive(type, field, valueString, handlers);
                    } else {
                        try {
                            o = processDiffClassBuilder(type, field, valueString,
                                    handlers); //外层Builder进行build的结果o  处理内层参数，进行builder和普通参数的转换
                        } catch (Exception e) {
                            LOGGER.error("Exception: ", e);
                        }
                    }
                    Method setMethod;
                    if (keyNameField.equals("augmentation")) {                  //内层augmentation尚未处理实现
                        Class<?> augClass = Class.forName("org.opendaylight.yangtools.yang.binding.Augmentation");
                        setMethod = beanType.getDeclaredMethod(methodKeySet, Class.class, augClass);
                        setMethod.invoke(t, null, null);
                    } else {
                        try {
                            setMethod = beanType.getDeclaredMethod(methodKeySet, type);
                            setMethod.invoke(t, o);             //对外层Builder进行set进t的结果
                        } catch (NoSuchMethodException e) {
                            field.setAccessible(true);
                            field.set(t, o);
                        }
                    }
                }
            }
        }
        return t;
    }

    private static void judgeDiffClassBuilderRecursive(Class<?> type, Field field, String valueString,
                                                       List<Object> handlers) throws Exception {
        if (handlers == null) {
            BuilderRpc.processDiffClassBuilder(type, field, valueString, null);
            return;
        }
        BuilderRpc.processDiffClassBuilderHandler(handlers, type, field, valueString);
    }

    /**
     * 处理不同类型的参数，包括不同类型的Builder和普通java类型
     *
     * @param type field的class类型
     * @param field 参数对应Builder的某个field
     * @param valueString 参数对应json串的部分String
     * @return json转换成的field对象
     * @throws Exception 异常
     */
    private static Object processDiffClassBuilder(Class<?> type, Field field, String valueString, List<Object> handlers)
            throws Exception {
        List<Object> res = new ArrayList<>();
        Set<Object> set = new HashSet<>();
        Object o = null;
        if (ParamType.isListParameter(type)) {
            if (getGenericSoloParamClass(field).isInterface()) {
                if (handlers != null && handlers.size() > 0) {
                    o = processDiffClassBuilderHandler(handlers, type, field, valueString);
                } else {
                    o = processListParamBuilder(field, res, valueString);
                }
            } else {
                o = jsonToList(valueString, getGenericSoloParamClass(field));
            }
        } else if (ParamType.isSetParameter(type)) {
            if (getGenericSoloParamClass(field).isInterface()) {
                if (handlers != null && handlers.size() > 0) {
                    o = processDiffClassBuilderHandler(handlers, type, field, valueString);
                } else {
                    o = processSetParamBuilder(field, set, valueString);
                }
            } else {
                o = jsonToSet(valueString, getGenericSoloParamClass(field));
            }
        } else if (ParamType.isMapParameter(type)) {
            o = processRecursiveMapWrap(field, valueString, handlers);
        } else if (type.isArray()) {
            if (type.getComponentType().isInterface()) {
                if (handlers != null && handlers.size() > 0) {
                    o = processDiffClassBuilderHandler(handlers, type, field, valueString);
                } else {
                    o = processArrayParamBuilder(field, res, valueString);
                }
            } else {
                o = MAPPER.fromJson(valueString, type);
            }
        } else if (type.isInterface()) {
            if (handlers != null && handlers.size() > 0) {
                o = processDiffClassBuilderHandler(handlers, type, field, valueString);
            } else {
                o = processInterfaceParamBuilder(field, valueString);
            }
        } else {
            o = MAPPER.fromJson(valueString, type);
        }
        return o;
    }

    private static Object processDiffClassBuilderHandler(List<Object> handlers, Class<?> type, Field field,
                                                         String valueString) throws Exception {
        Object o = null;
        for (Object handler : handlers) {
            String contains = handler.getClass()
                    .getDeclaredMethod("contains", String.class)
                    .invoke(handler, field.getName())
                    .toString();
            if (contains.equals("true")) {
                o = processDiffClassHandler(type, field, valueString, handler);
            } else {
                o = processDiffClass(type, field, valueString);
            }
        }
        return o;
    }

    /**
     * 对于数据类型为List的Builder进行处理
     *
     * @param field 参数对应Builder的某个field
     * @param res 将处理后的结果转换为List
     * @param valueString 参数对应json串的部分String
     * @return 将builder完成build之后的结果封装成List返回
     * @throws Exception 异常
     */
    private static List<Object> processListParamBuilder(Field field, List<Object> res, String valueString)
            throws Exception {
        Class<?> genericClass = getGenericSoloParamClass(field);
        Class<?> classBuilder = Class.forName(genericClass.getName() + "Builder");
        Method build = classBuilder.getDeclaredMethod("build");
        List<Object> list = GsonUtils.parseString2List(valueString, classBuilder);
        for (Object listItem : list) {
            Object buildRes = build.invoke(listItem);
            res.add(buildRes);
        }
        return res;
    }

    /**
     * 对于数据类型为Set的Builder进行处理
     *
     * @param field 参数对应Builder的某个field
     * @param res 将处理后的结果转换为Set
     * @param valueString 参数对应json串的部分String
     * @return 将builder完成build之后的结果封装成Set返回
     * @throws Exception 异常
     */
    private static Set<Object> processSetParamBuilder(Field field, Set<Object> res, String valueString)
            throws Exception {
        Class<?> genericClass = getGenericSoloParamClass(field);
        Class<?> classBuilder = Class.forName(genericClass.getName() + "Builder");
        Method build = classBuilder.getDeclaredMethod("build");
        Set<Object> set = GsonUtils.parseString2Set(valueString, classBuilder);
        for (Object setItem : set) {
            Object buildRes = build.invoke(setItem);
            res.add(buildRes);
        }
        return res;
    }

    /**
     * 对于数据类型为Array的Builder进行处理
     *
     * @param field 参数对应Builder的某个field
     * @param res 将处理后的结果转换为List
     * @param valueString 参数对应json串的部分String
     * @return 将builder完成build之后的结果封装成Array返回
     * @throws Exception 异常
     */
    private static Object[] processArrayParamBuilder(Field field, List<Object> res, String valueString)
            throws Exception {
        return processListParamBuilder(field, res, valueString).toArray();
    }

    /**
     * 对于数据类型为接口的builder进行处理
     *
     * @param field 参数对应Builder的某个field
     * @param valueString 参数对应json串的部分String
     * @return 返回builder进行build之后的结果
     * @throws Exception 异常
     */
    private static Object processInterfaceParamBuilder(Field field, String valueString) throws Exception {
        Class<?> type = field.getType();
        Class<?> classBuilder = Class.forName(type.getName() + "Builder");
        Method build = classBuilder.getDeclaredMethod("build");
        Object res = null;
        Object o = MAPPER.fromJson(valueString, classBuilder);
        res = build.invoke(o);
        return res;
    }

    /**
     * 判断是否有多层嵌套的情况出现
     *
     * @param type field的class类型
     * @param field 参数对应Builder的某个field
     * @param valueString 参数对应json串的部分String
     * @return 是否出现了多层builder嵌套
     */
    private static boolean isRecursive(Class<?> type, Field field, String valueString, List<Object> handlers) {
        try {
            judgeDiffClassBuilderRecursive(type, field, valueString, handlers);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 处理递归
     *
     * @param type field的class类型
     * @param field 参数对应Builder的某个field
     * @param valueString 参数对应json串的部分String
     * @return 参数对应Builder进行build之后的结果
     */
    private static Object processRecursive(Class<?> type, Field field, String valueString, List<Object> handlers) {
        Object o = null;
        if (ParamType.isListParameter(type)) {          //只考虑List里有一个参数的情况
            if (getGenericSoloParamClass(field).isInterface()) {
                o = processRecursiveListWrap(field, valueString, handlers);
            }
        } else if (ParamType.isSetParameter(type)) {
            if (getGenericSoloParamClass(field).isInterface()) {
                o = processRecursiveSetWrap(field, valueString, handlers);
            }
        } else if (ParamType.isMapParameter(type)) {
            o = processRecursiveMapWrap(field, valueString, handlers);
        } else if (type.isArray()) {
            if (type.getComponentType().isInterface()) {
                o = processRecursiveArrayWrap(field, valueString, handlers);
            }
        } else if (type.isInterface()) {
            o = processRecursiveInterface(valueString, type, field, handlers);
        } else {
            o = processRecursiveNormal(valueString, type, field, handlers);
        }
        return o;
    }

    /**
     * 处理数据类型为接口的递归
     *
     * @param valueString 参数对应json串的部分String
     * @param type field的class类型
     * @return 参数对应Builder进行build之后的结果
     */
    private static Object processRecursiveInterface(String valueString, Class<?> type, Field field,
                                                    List<Object> handlers) {
        Object o = null;
        try {
            if (handlers != null && handlers.size() > 0) {
                for (Object handler : handlers) {
                    String contains = handler.getClass()
                            .getDeclaredMethod("contains", String.class)
                            .invoke(handler, field.getName())
                            .toString();
                    if (contains.equals("true")) {
                        String name = handler.getClass().getDeclaredMethod("getType").invoke(handler).toString();
                        if (name.endsWith("Builder")) {
                            Class<?> builderClass = Class.forName(name);
                            Method build = builderClass.getDeclaredMethod("build");
                            Object builderTemp = jsonToPojoProcess(valueString, builderClass, null);
                            o = build.invoke(builderTemp);
                        } else {
                            o = processDiffClassHandler(type, field, valueString, handler);
                        }
                        return o;
                    }
                }
            }
            Class<?> builderClass = Class.forName(type.getName() + "Builder");
            Method build = builderClass.getDeclaredMethod("build");
            Object builderTemp = jsonToPojoProcess(valueString, builderClass, handlers);
            o = build.invoke(builderTemp);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return o;
    }

    private static Object processRecursiveNormal(String valueString, Class<?> type, Field field,
                                                 List<Object> handlers) {
        Object o = null;
        try {
            o = jsonToPojoProcess(valueString, type, handlers);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return o;
    }

    /**
     * 处理数据类型为Array类型的递归
     *
     * @param field 参数对应Builder的某个builder field为Array类型
     * @param valueString 参数对应json串的部分String
     * @return 参数对应Builder进行build之后的结果
     */
    private static Object processRecursiveArray(Field field, String valueString, List<Object> handlers) {
        Class<?> cls = getGenericSoloParamClass(field);
        String builderName = cls.getName() + "Builder";
        valueString = GsonUtils.removeBegEndBrace(valueString);
        Object o = null;
        try {
            Class<?> builderClass = Class.forName(builderName);
            Object builderTemp = jsonToPojoProcess(valueString, builderClass, handlers);
            Method build = builderClass.getDeclaredMethod("build");
            Object tTemp = build.invoke(builderTemp);
            List<Object> listTemp = new ArrayList<>();
            listTemp.add(tTemp);
            o = listTemp.toArray();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return o;
    }

    private static Object processRecursiveArrayWrap(Field field, String valueString, List<Object> handlers) {
        List<Object> res = new ArrayList<>();
        List<HashMap> list = GsonUtils.parseString2List(valueString, HashMap.class);
        for (HashMap map : list) {
            String valueStringTemp = MAPPER.toJson(map);
            Class<?> cls = RPCUtils.getGenericSoloParamClass(field);
            Object oTemp = processRecursiveInterface(valueStringTemp, cls, field, handlers);
            res.add(oTemp);
        }
        return res.toArray();
    }

    /**
     * 处理数据类型为Set情况的递归
     *
     * @param field 参数对应Builder的某个builder field为Set类型
     * @param valueString 参数对应json串的部分String
     * @return 参数对应Builder进行build之后的结果
     */
    private static Object processRecursiveSet(Field field, String valueString, List<Object> handlers) {
        Class<?> cls = getGenericSoloParamClass(field);
        String builderName = cls.getName() + "Builder";
        valueString = GsonUtils.removeBegEndBrace(valueString);
        Object o = null;
        try {
            Class<?> builderClass = Class.forName(builderName);
            Object builderTemp = jsonToPojoProcess(valueString, builderClass, handlers);
            Method build = builderClass.getDeclaredMethod("build");
            Object tTemp = build.invoke(builderTemp);
            Set<Object> setTemp = new HashSet<>();
            setTemp.add(tTemp);
            o = setTemp;
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return o;
    }

    private static Object processRecursiveSetWrap(Field field, String valueString, List<Object> handlers) {
        Set<Object> res = new HashSet<>();
        Set<HashMap> set = GsonUtils.parseString2Set(valueString, HashMap.class);
        for (HashMap map : set) {
            String valueStringTemp = MAPPER.toJson(map);
            Class<?> cls = RPCUtils.getGenericSoloParamClass(field);
            Object oTemp = processRecursiveInterface(valueStringTemp, cls, field, handlers);
            res.add(oTemp);
        }
        return res;
    }

    /**
     * 处理递归的情况
     *
     * @param field 参数对应Builder的某个builder field为List类型
     * @param valueString 参数对应json串的部分String
     * @return 参数对应Builder进行build之后的结果
     */
    private static Object processRecursiveList(Field field, String valueString, List<Object> handlers) {
        Class<?> cls = getGenericSoloParamClass(field);
        String builderName = cls.getName() + "Builder";
        valueString = GsonUtils.removeBegEndBrace(valueString);
        Object o = null;
        try {
            Class<?> builderClass = Class.forName(builderName);
            Object builderTemp = jsonToPojoProcess(valueString, builderClass, handlers);
            Method build = builderClass.getDeclaredMethod("build");
            List<Object> listTemp = new ArrayList<>();
            Object tTemp = build.invoke(builderTemp);
            listTemp.add(tTemp);
            o = listTemp;
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return o;
    }

    private static Object processRecursiveListWrap(Field field, String valueString, List<Object> handlers) {
        List<Object> res = new ArrayList<>();
        List<HashMap> list = GsonUtils.parseString2List(valueString, HashMap.class);
        for (HashMap map : list) {
            String valueStringTemp = MAPPER.toJson(map);
            Class<?> cls = RPCUtils.getGenericSoloParamClass(field);
            Object oTemp = processRecursiveInterface(valueStringTemp, cls, field, handlers);
            res.add(oTemp);
        }
        return res;
    }

    private static Map<Object, Object> processRecursiveMapWrap(Field field, String valueString, List<Object> handlers) {
        Map<Object, Object> res = new HashMap<>();
        Map<Object, Object> map = GsonUtils.parseString2Map(valueString, Object.class, HashMap.class);
        Type[] types = getMapParameterTypeField(field);
        for (Map.Entry entry : map.entrySet()) {
            String valueStringTempKey = MAPPER.toJson(entry.getKey());
            String valueStringTempValue = MAPPER.toJson(entry.getValue());
            Object key = getMapParam((Class<?>) types[0], field, valueStringTempKey, handlers);
            Object value = getMapParam((Class<?>) types[1], field, valueStringTempValue, handlers);
            res.put(key, value);
        }
        return res;
    }

    private static Object getMapParam(Class<?> type, Field field, String valueString, List<Object> handlers) {
        Object param;
        if (isRecursive(type, field, valueString, handlers)) {
            param = processRecursiveInterface(valueString, type, field, handlers);
        } else {
            param = MAPPER.fromJson(valueString, type);
        }
        return param;
    }

    protected static boolean canCastBuilderMode(String paramMode, Method method, List<String> paramMessage,
                                                List<Integer> builderIndex, List<Object> handlers) {
        return paramMode != null && paramMode.equals("builder") && isAllMethodActual(method, paramMessage, builderIndex,
                handlers);
    }

    protected static boolean ofBuilderProcess(String name) {
        return name.endsWith("Builder") && name.startsWith("org.opendaylight.yang.gen");
    }
}