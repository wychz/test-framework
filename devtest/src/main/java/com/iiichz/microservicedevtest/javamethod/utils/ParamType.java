package com.iiichz.microservicedevtest.javamethod.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParamType {

    private static final Class<?> STRING_TYPE = String.class;

    private static final Class<?> INTEGER_TYPE = Integer.class;

    private static final Class<?> INTEGER_BASIC_TYPE = int.class;

    private static final Class<?> BYTE_TYPE = Byte.class;

    private static final Class<?> BYTE_BASIC_TYPE = byte.class;

    private static final Class<?> SHORT_TYPE = Short.class;

    private static final Class<?> SHORT_BASIC_TYPE = short.class;

    private static final Class<?> LONG_TYPE = Long.class;

    private static final Class<?> LONG_BASIC_TYPE = long.class;

    private static final Class<?> FLOAT_TYPE = Float.class;

    private static final Class<?> FLOAT_BASIC_TYPE = float.class;

    private static final Class<?> DOUBLE_TYPE = Double.class;

    private static final Class<?> DOUBLE_BASIC_TYPE = double.class;

    private static final Class<?> CHAR_TYPE = Character.class;

    private static final Class<?> CHAR_BASIC_TYPE = char.class;

    public static Boolean isBasicParameter(Class<?> cls) {
        return isByteParameter(cls) || isCharParameter(cls) || isDoubleParameter(cls) || isFloatParameter(cls)
                || isIntegerParameter(cls) || isLongParameter(cls) || isShortParameter(cls) || isStringParameter(cls);

    }

    public static Boolean isStringParameter(Class<?> cls) {
        return cls.equals(STRING_TYPE);
    }

    public static Boolean isIntegerParameter(Class<?> cls) {
        return cls.equals(INTEGER_TYPE) || cls.equals(INTEGER_BASIC_TYPE);
    }

    public static Boolean isByteParameter(Class<?> cls) {
        return cls.equals(BYTE_TYPE) || cls.equals(BYTE_BASIC_TYPE);
    }

    public static Boolean isShortParameter(Class<?> cls) {
        return cls.equals(SHORT_TYPE) || cls.equals(SHORT_BASIC_TYPE);
    }

    public static Boolean isLongParameter(Class<?> cls) {
        return cls.equals(LONG_TYPE) || cls.equals(LONG_BASIC_TYPE);
    }

    public static Boolean isFloatParameter(Class<?> cls) {
        return cls.equals(FLOAT_TYPE) || cls.equals(FLOAT_BASIC_TYPE);
    }

    public static Boolean isDoubleParameter(Class<?> cls) {
        return cls.equals(DOUBLE_TYPE) || cls.equals(DOUBLE_BASIC_TYPE);
    }

    public static Boolean isCharParameter(Class<?> cls) {
        return cls.equals(CHAR_TYPE) || cls.equals(CHAR_BASIC_TYPE);
    }

    public static Boolean isListParameter(Class<?> cls) {
        return List.class.isAssignableFrom(cls);
    }

    public static Boolean isPojoParameter(Class<?> cls) {
        return (!isBasicParameter(cls) && !isListParameter(cls) && !isMapParameter(cls) && !isSetParameter(cls));
    }

    public static Boolean isMapParameter(Class<?> cls) {
        return Map.class.isAssignableFrom(cls);
    }

    public static Boolean isSetParameter(Class<?> cls) {
        return Set.class.isAssignableFrom(cls);
    }

}
