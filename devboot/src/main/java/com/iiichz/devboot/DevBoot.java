package com.iiichz.devboot;

import com.iiichz.devboot.init.hbase.HBaseInit;
import com.iiichz.devboot.tomcatwebxml.TomcatWebXmlInitUtil;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;


public class DevBoot {
    private static final Log LOGGER = LogFactory.getLog(DevBoot.class);

    private static final String NFW = "NFW";
    private static Tomcat tomcat = new Tomcat();

    public Tomcat run() throws Exception {
        System.out.println("Current path:" + new File("./").getAbsolutePath());

        prepareContext();

        String webappDirLocation = DevBootProfile.getInstance().getAppPath();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));
        tomcat.getConnector();

        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        Context ctx = tomcat.addContext("", new File(webappDirLocation).getAbsolutePath());

        ctx.addApplicationListener("com.iiichz.devboot.loader.FrameworkLoader");

        TomcatWebXmlInitUtil.init(ctx);

        initCommon();

        initDb();

        tomcat.start();

        new Thread(() -> tomcat.getServer().await()).start();

        return tomcat;
    }

    private void initDb() {
        HBaseInit.init();
    }

    private void initCommon() {}

    private void prepareContext() throws Exception {

        setEnv(DevBootProfile.getInstance().getEnv());

        setProperty(DevBootProfile.getInstance().getProp());
    }

    private void setProperty(Map<String, String> prop) {
        prop.entrySet().stream().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }

    protected void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }
}