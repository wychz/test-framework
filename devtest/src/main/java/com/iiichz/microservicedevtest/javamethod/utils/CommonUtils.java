package com.iiichz.microservicedevtest.javamethod.utils;

import com.alibaba.fastjson.JSON;

import javafx.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
    private static Pattern numPattern = Pattern.compile("^-?[0-9]+\\.?[0-9]*$");
    private static Pattern objectPattern = Pattern.compile("^[a-zA-Z0-9.]+\\s\\[.+\\]$");
    private static Pattern listPattern = Pattern.compile("^\\[.*\\]$");
    private static Pattern mapPattern = Pattern.compile("^\\{.*\\}$");
    private static final String NULL = "null";
    private static List<Character> TOKEN_LEFT = Arrays.asList('(', '{', '[');
    private static List<Character> TOKEN_RIGHT = Arrays.asList(')', '}', ']');

    public static String LogToJsonString(String toString) throws ParseException {
        Map<String, Object> map = LogToJsonMap(toString);
        return JSON.toJSONString(map);
    }

    private static Map<String, Object> LogToJsonMap(String toString) throws ParseException {
        if (StringUtils.isEmpty(toString = StringUtils.trim(toString))) {
            return toString == null ? null : new HashMap<>();
        }
        toString = StringUtils.substringAfter(toString, "[").trim();
        toString = StringUtils.substringBeforeLast(toString, "]").trim();
        String token;
        Map<String, Object> map = new HashMap<>();
        while (StringUtils.isNotEmpty(toString) && StringUtils.isNotEmpty(token = getCurrentParseString(toString))) {
            toString = StringUtils.removeStart(StringUtils.removeStart(toString, token).trim(), ",").trim();
            Pair<String, String> keyValue = parseString(token);
            map.put(keyValue.getKey(), PatternMatch(keyValue.getValue()));
        }
        return map;
    }

    private static Object PatternMatch(String value) throws ParseException {
        if (StringUtils.isEmpty(value)) {
            return null;
        } else if (value.equals(NULL)) {
            return null;
        }
        if (numPattern.matcher(value).matches()) {
            return value;
        }
        if (listPattern.matcher(value).matches()) {
            return parseList(value);
        }
        if (mapPattern.matcher(value).matches()) {
            return parseMap(value);
        }
        if (objectPattern.matcher(value).matches()) {
            return LogToJsonMap(value);
        }
        return value;
    }

    private static String getCurrentParseString(String toString) {
        if (StringUtils.isBlank(toString)) {
            return toString;
        }
        int index = getSeparateIndex(toString, ',');
        return toString.substring(0, index);
    }

    public static Pair<String, String> parseString(String token) {
        int index = getSeparateIndex(token, '=');
        String key = token.substring(0, index);
        String value = token.substring(index + 1);
        return new Pair<>(key, value);
    }

    private static int getSeparateIndex(String token, char split) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (TOKEN_LEFT.contains(c)) {
                stack.push(c);
            } else if (TOKEN_RIGHT.contains(c)) {
                if (TOKEN_LEFT.indexOf(stack.peek()) != TOKEN_RIGHT.indexOf(c)) {
                    throw new RuntimeException("parse error!");
                }
                stack.pop();
            } else if (c == split && stack.isEmpty()) {
                return i;
            }
        }
        if (stack.isEmpty()) {
            return token.length();
        }
        throw new RuntimeException("parse error!");
    }

    private static Object parseList(String value) throws ParseException {
        List<Object> result = new ArrayList<>();
        value = value.substring(1, value.length() - 1).trim();
        if (StringUtils.isEmpty(value)) {
            return result;
        }
        String token = null;
        while (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(token = getCurrentParseString(value))) {
            result.add(PatternMatch(token));
            value = StringUtils.removeStart(StringUtils.removeStart(value, token).trim(), ",").trim();
        }
        return result;
    }

    private static Map<Object, Object> parseMap(String value) throws ParseException {
        Map<Object, Object> result = new HashMap<>();
        value = value.substring(1, value.length() - 1).trim();
        if (StringUtils.isEmpty(value)) {
            return result;
        }
        String token = null;
        while (StringUtils.isNotEmpty(token = getCurrentParseString(value))) {
            Pair<String, String> keyValue = parseString(token);
            result.put(PatternMatch(keyValue.getKey()), PatternMatch(keyValue.getValue()));
            value = StringUtils.removeStart(StringUtils.removeStart(value, token).trim(), ",").trim();
        }
        return result;
    }

    public static String jsonFormat(String notFormatString) {
        String notFormatJsonString = null;
        try {
            notFormatJsonString = LogToJsonString(notFormatString);
        } catch (ParseException e) {
            LOGGER.error("Exception: ", e);
        }
        if (notFormatJsonString != null) {
            notFormatJsonString = notFormatJsonString.replace("=", ":");
        }
        StringBuffer jsonFormatStr = new StringBuffer();
        int level = 0;
        if (notFormatJsonString != null) {
            for (int index = 0; index < notFormatJsonString.length(); index++) {
                char c = notFormatJsonString.charAt(index);
                if (level > 0 && '\n' == jsonFormatStr.charAt(jsonFormatStr.length() - 1)) {
                    jsonFormatStr.append(getLevel(level));
                }
                switch (c) {
                    case '{':
                    case '[':
                        jsonFormatStr.append(c).append("\n");
                        level++;
                        break;
                    case ',':
                        jsonFormatStr.append(c).append("\n");
                        break;
                    case '}':
                    case ']':
                        if (jsonFormatStr.charAt(jsonFormatStr.length() - 1) == '\t'
                                && jsonFormatStr.charAt(jsonFormatStr.length() - 1) == jsonFormatStr.charAt(
                                jsonFormatStr.length() - 2)) {
                            jsonFormatStr = new StringBuffer(
                                    StringUtils.substringBeforeLast(jsonFormatStr.toString(), "\n").trim());
                            level--;
                            jsonFormatStr.append(c);
                        } else {
                            jsonFormatStr.append("\n");
                            level--;
                            jsonFormatStr.append(getLevel(level));
                            jsonFormatStr.append(c);
                        }
                        break;
                    default:
                        jsonFormatStr.append(c);
                        break;
                }
            }
        }
        return jsonFormatStr.toString();
    }

    private static String getLevel(int level) {
        StringBuilder levelStr = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
}
