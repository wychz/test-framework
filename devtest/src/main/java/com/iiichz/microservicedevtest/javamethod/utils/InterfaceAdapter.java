package com.iiichz.microservicedevtest.javamethod.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class InterfaceAdapter implements JsonDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceAdapter.class);
    private static String CLASSNAME = null;

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        Class<?> klass = getObjectClass(CLASSNAME);
        return jsonDeserializationContext.deserialize(jsonElement, klass);
    }

    public Class<?> getObjectClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Exception: ", e);
            throw new JsonParseException(e.getMessage());
        }
    }

    public void setClassName(String className) {
        CLASSNAME = className;
    }
}