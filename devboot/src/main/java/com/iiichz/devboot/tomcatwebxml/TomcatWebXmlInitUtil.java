package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

public final class TomcatWebXmlInitUtil {
    public static final String CUSTOMER_WEB_XML_FILE = "WEB_XML_FILE";

    private TomcatWebXmlInitUtil() {
    }

    public static void init(Context ctx) {
        TomcatWebXml bean = getWebXmlBean();
        if (bean == null) {
            return;
        }

        initContextParam(ctx, bean.getContextParams());

        initListener(ctx, bean.getListeners());

        initFilter(ctx, bean.getFilters());

        initFilterMapping(ctx, bean.getFilterMappings());

        initServlet(ctx, bean.getServlets());

        initServletMapping(ctx, bean.getServletMappings());

    }

    private static void initContextParam(Context ctx, List<ContextParamSettingNode> contextParams) {
        if (CollectionUtils.isEmpty(contextParams)) {
            System.out.println("ContextParams is empty!");
            return;
        }
        contextParams.stream().forEach(node -> ctx.addParameter(node.getParamName(), node.getParamValue()));
    }

    private static void initListener(Context ctx, List<ListenerSettingNode> listeners) {
        if (CollectionUtils.isEmpty(listeners)) {
            System.out.println("Listeners is empty!");
            return;
        }

        listeners.stream().forEach(node -> ctx.addApplicationListener(node.getListenerClass()));
    }

    private static void initFilter(Context ctx, List<FilterSettingNode> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            System.out.println("Filters is empty!");
            return;
        }
        for(FilterSettingNode node : filters){
            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(node.getFilterName());
            filterDef.setFilterClass(node.getFilterClass());
            filterDef.setAsyncSupported(String.valueOf(node.getAsyncSupported()));
            ctx.addFilterDef(filterDef);
        }
    }


    private static void initFilterMapping(Context ctx, List<FilterMappingSettingNode> filterMappings) {
        if (CollectionUtils.isEmpty(filterMappings)) {
            System.out.println("FilterMappings is empty!");
            return;
        }
        for(FilterMappingSettingNode node : filterMappings){
            FilterMap fm = new FilterMap();
            fm.setFilterName(node.getFilterName());
            fm.addURLPattern(node.getUrlPattern());
            ctx.addFilterMap(fm);
        }
    }

    private static void initServletMapping(Context ctx, List<ServletMappingSettingNode> mapping) {
        if (CollectionUtils.isEmpty(mapping)) {
            System.out.println("Servlet mapping is empty!");
            return;
        }

        mapping.stream().forEach(node -> {
            ctx.addServletMappingDecoded(node.getUrlPattern(), node.getServletName());
        });
    }

    private static void initServlet(Context ctx, List<ServletSettingNode> servlets) {
        if (CollectionUtils.isEmpty(servlets)) {
            System.out.println("Servlet is empty!");
            return;
        }

        servlets.stream().forEach(servlet -> {
            Wrapper wrapper = Tomcat.addServlet(ctx, servlet.getServletName(), servlet.getServletClass());

            if (!CollectionUtils.isEmpty(servlet.getInitParam())) {
                servlet.getInitParam().stream().forEach(param -> {
                    wrapper.addInitParameter(param.getParamName(), param.getParamValue());
                });
            }

            if (servlet.getLoadOnStartup() != null) {
                wrapper.setLoadOnStartup(servlet.getLoadOnStartup());
            }

            if (servlet.getAsyncSupported() != null) {
                wrapper.setAsyncSupported(true);
            }
        });
    }

    public static TomcatWebXml getWebXmlBean() {
        System.out.println("current path:" + new File("./").getAbsolutePath());
        String path = System.getProperty(CUSTOMER_WEB_XML_FILE);
        if (path == null) {
            System.out.println("HAVE NO Customer WEB XML");
            return null;
        }

        File webXml = new File(path);
        if (!webXml.isFile()) {
            System.out.println("HAVE NO Customer WEB XML");
            return null;
        }

        XStream xstream = new XStream() {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        if (definedIn == Object.class) {
                            return false;
                        }
                        return super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };
        xstream.processAnnotations(TomcatWebXml.class);
        return  (TomcatWebXml)xstream.fromXML(webXml);
    }
}