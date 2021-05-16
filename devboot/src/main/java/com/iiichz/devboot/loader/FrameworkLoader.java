package com.iiichz.devboot.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FrameworkLoader implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkLoader.class);

    private static String appBaseHome;
    private ApplicationContext springContext;
    private Set<Pattern> includeRules = new HashSet();
    private Set<Pattern> excludeRules = new HashSet();

    public void contextDestroyed(ServletContextEvent sce) {
    }

    public void contextInitialized(ServletContextEvent sce) {

        ServletContext sc = sce.getServletContext();
        LOGGER.warn("Current init servlet context is " + sc.getContextPath());
        appBaseHome = sc.getRealPath("");
        System.setProperty("APP_BASE_HOME", appBaseHome);
        this.includeRules.add(this.pathString2Pattern("spring/*.xml"));
        String contextRuleStr = sce.getServletContext().getInitParameter("com.huawei.cloudsop.springpath");
        if (contextRuleStr != null && !contextRuleStr.isEmpty()) {
            String[] rules = contextRuleStr.split(",");
            String[] var5 = rules;
            int var6 = rules.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String rule = var5[var7];
                Pattern patterns;
                if (rule.startsWith("!")) {
                    patterns = this.pathString2Pattern(rule.substring(1));
                    if (patterns != null) {
                        this.excludeRules.add(patterns);
                    }
                } else {
                    patterns = this.pathString2Pattern(rule);
                    if (patterns != null) {
                        this.includeRules.add(this.pathString2Pattern(rule));
                    }
                }
            }
        }

        this.init();
    }

    private Pattern pathString2Pattern(String path) {
        Pattern patterns = null;

        try {
            String regex = path.replaceAll("/\\*/", "/.+/");
            regex = regex.replaceAll("\\*", ".*");
            patterns = Pattern.compile(regex);
        } catch (PatternSyntaxException var4) {
            LOGGER.error("path is not right: {}", var4.getMessage());
        }

        return patterns;
    }

    private void loadSpring() {
        LOGGER.warn("Start init spring");

        try {
            this.springContext = new ClassPathXmlApplicationContext("classpath*:/spring/localContext.xml");
        } catch (BeansException var7) {
            LOGGER.error("Init spring application context failed, system is started in abnormal state!", var7);
            return;
        }

        LOGGER.warn("Init spring finish!");
    }

    private void init() {
        LOGGER.warn("****** BSP System is  starting ...... ******");
        long start = System.currentTimeMillis();
        this.loadSpring();
        long timeCost = System.currentTimeMillis() - start;
        LOGGER.warn("BSPSystem starting cost time: " + timeCost);
        LOGGER.warn("****** BSP System is Ok ...... ******");
    }

}

