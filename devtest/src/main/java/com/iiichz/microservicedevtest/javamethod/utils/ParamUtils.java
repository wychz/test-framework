package com.iiichz.microservicedevtest.javamethod.utils;

import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isByteParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isCharParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isDoubleParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isFloatParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isIntegerParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isLongParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isShortParameter;
import static com.iiichz.microservicedevtest.javamethod.utils.ParamType.isStringParameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParamUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamUtils.class);

    public static Object getBasicMethodParam(String paramsMessage, Class<?> cls) {

        Object obj = null;
        if (isStringParameter(cls)) {
            obj = String.valueOf(paramsMessage);
        }
        if (isIntegerParameter(cls)) {
            obj = Integer.valueOf(paramsMessage);
        }
        if (isByteParameter(cls)) {
            obj = Byte.valueOf(paramsMessage);
        }
        if (isDoubleParameter(cls)) {
            obj = Double.valueOf(paramsMessage);
        }
        if (isLongParameter(cls)) {
            obj = Long.valueOf(paramsMessage);
        }
        if (isFloatParameter(cls)) {
            obj = Float.valueOf(paramsMessage);
        }
        if (isShortParameter(cls)) {
            obj = Short.valueOf(paramsMessage);
        }
        if (isCharParameter(cls)) {
            obj = paramsMessage.charAt(0);
        }
        return obj;
    }

    public static boolean canGetBasicMethodParam(String paramsMessage, Class<?> cls) {
        try {
            getBasicMethodParam(paramsMessage, cls);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Class<?> getListParameterType(Method method, int i) {
        Parameter[] parameters = method.getParameters();
        Class<?> genericNameClass = null;
        Parameter parameter = parameters[i];
        String typeName = parameter.getParameterizedType().getTypeName();
        String genericName = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"));
        try {
            genericNameClass = Class.forName(genericName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Can't find the class, Please check your method!", e);
        }
        return genericNameClass;
    }

    public static Class<?> getSetParameterType(Method method, int i) {
        return getListParameterType(method, i);
    }

    public static List<Class<?>> getMapParameterType(Method method, int i) {
        List<Class<?>> mapGenericNameClass = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        Class<?> genericNameKeyClass = null;
        Class<?> genericNameValueClass = null;
        Parameter parameter = parameters[i];
        String typeName = parameter.getParameterizedType().getTypeName();
        String genericNameKey = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(","));
        String genericNameValue = typeName.substring(typeName.indexOf(",") + 2, typeName.indexOf(">"));
        try {
            genericNameKeyClass = Class.forName(genericNameKey);
            mapGenericNameClass.add(genericNameKeyClass);
            genericNameValueClass = Class.forName(genericNameValue);
            mapGenericNameClass.add(genericNameValueClass);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Can't find the class, Please check your method!", e);
        }
        return mapGenericNameClass;
    }

    public static Type[] getMapParameterTypeField(Field mapField) {
        Type mapMainType = mapField.getGenericType();
        if (mapMainType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) mapMainType;
            Type[] types = parameterizedType.getActualTypeArguments();
            return types;
        } else {
            LOGGER.error("can not get the generic type!");
            return null;
        }
    }

    public static Class<?> getPojoParameterType(Class<?> cls) {
        String parameterName = cls.getName();
        Class<?> paramClass = null;
        try {
            paramClass = Class.forName(parameterName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Can't find the class, Please check your method!", e);
        }
        return paramClass;
    }

    public static int getMethodParamsNumber(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes.length;
    }

    public static Class<?>[] getMethodParamTypes(Class<?> cls, String methodName, List paramMessage,
                                                 List<Integer> builderIndex, List<Object> handlers) {
        Method[] methods = cls.getMethods();
        Class<?>[] cArg = new Class[paramMessage.size()];
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == paramMessage.size()) {
                boolean allMethodActual = RPCUtils.isAllMethodActual(method, paramMessage, builderIndex, handlers);
                if (allMethodActual) {
                    Class<?>[] parameterTypes = method.getParameterTypes();     //得到参数类型
                    System.arraycopy(parameterTypes, 0, cArg, 0, parameterTypes.length);
                }
            }
        }
        return cArg;
    }

    public static Object[] getMethodParamObj(Method method, List methodParamList) {
        int paramNumber = ParamUtils.getMethodParamsNumber(method);
        Object[] paramObj = new Object[paramNumber];

        for (int i = 0; i < paramNumber; i++) {
            paramObj[i] = methodParamList.get(i);
        }
        return paramObj;
    }
}