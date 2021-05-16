package com.iiichz.devboot;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class DevBootProfile {

    private static final DevBootProfile DEV_BOOT_PROFILE;
    public static final String MYBATIS = "mybatis";
    public static final String FRAMEWORK = "framework";
    public static final String TOPICS = "topics";

    static {
        Yaml yaml = new Yaml(new Constructor(DevBootProfile.class));
        InputStream inputStream = DevBootProfile.class.getClassLoader().getResourceAsStream("devboot.yml");
        DEV_BOOT_PROFILE = yaml.load(inputStream);
    }

    private String appPath;

    private Map<String, String> env;

    private Map<String, String> prop;
    private Map<String, String> datasource;
    private Map<String, String> flyway;
    private Map<String, String> embeddedkafka;
    private DevBootProfile() {
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public static DevBootProfile getInstance() {
        return DEV_BOOT_PROFILE;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public Map<String, String> getProp() {
        return prop;
    }

    public void setProp(Map<String, String> prop) {
        this.prop = prop;
    }

    public Map<String, String> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, String> datasource) {
        this.datasource = datasource;
    }

    public Map<String, String> getFlyway() {
        return flyway;
    }

    public void setFlyway(Map<String, String> flyway) {
        this.flyway = flyway;
    }
    public Map<String, String> getEmbeddedkafka() {
        return embeddedkafka;
    }

    public void setEmbeddedkafka(Map<String, String> embeddedkafka) {
        this.embeddedkafka = embeddedkafka;
    }

    public boolean enbaleMybatis() {
        String framework = datasource.getOrDefault(FRAMEWORK, MYBATIS);
        if (Objects.equals(framework, MYBATIS)) {
            return true;
        }
        return false;
    }
    public String getTopics() {
        return embeddedkafka.get(TOPICS);
    }
}
