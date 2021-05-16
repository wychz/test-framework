package com.iiichz.microservicedevtest.javamethod.utils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GsonUtils {
    public static <T> List<T> parseString2List(String json, Class<?> clazz) {
        Type type = new ParameterizedListTypeImpl(clazz);
        return new Gson().fromJson(json, type);
    }

    private static class ParameterizedListTypeImpl implements ParameterizedType {
        Class<?> clazz;

        public ParameterizedListTypeImpl(Class<?> clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static Map<Object, Object> parseString2Map(String json, Class<?> clazz1, Class<?> clazz2) {
        Type type = new ParameterizedMapTypeImpl(clazz1, clazz2);
        return new Gson().fromJson(json, type);
    }

    private static class ParameterizedMapTypeImpl implements ParameterizedType {
        Class<?> clazz1;

        Class<?> clazz2;

        public ParameterizedMapTypeImpl(Class<?> clz1, Class<?> clz2) {
            clazz1 = clz1;
            clazz2 = clz2;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {clazz1, clazz2};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static Set parseString2Set(String json, Class<?> clazz) {
        Type type = new ParameterizedSetTypeImpl(clazz);
        return new Gson().fromJson(json, type);
    }

    private static class ParameterizedSetTypeImpl implements ParameterizedType {
        Class<?> clazz;

        public ParameterizedSetTypeImpl(Class<?> clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {clazz};
        }

        @Override
        public Type getRawType() {
            return Set.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static String removeBegEndBrace(String str) {
        StringBuilder sb = new StringBuilder(str);
        int leftIndex = 0;
        while (sb.charAt(leftIndex) == '[') {
            leftIndex++;
        }
        sb.substring(leftIndex);
        int rightIndex = sb.length() - 1;
        int count = 0;
        while (sb.charAt(rightIndex) == ']' && count < leftIndex) {
            rightIndex--;
            count++;
        }
        sb.substring(0, rightIndex + 1);
        return sb.toString();
    }
}

