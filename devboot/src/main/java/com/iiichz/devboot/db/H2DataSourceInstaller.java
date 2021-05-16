package com.iiichz.devboot.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class H2DataSourceInstaller {
    private static final Log LOGGER = LogFactory.getLog(H2DataSourceInstaller.class);

    private ApplicationContext context;

    public H2DataSourceInstaller(String dbname, DataSource dataSource) {
        if (dataSource != null) {
            Map<String, DataSource> h2bean = new ConcurrentHashMap<>();
            h2bean.put(dbname, dataSource);
//            DataSourceCenter.getInstance().registerDBDataSources(h2bean);
        }

    }

}