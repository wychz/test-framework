package com.iiichz.devboot.db;

import com.iiichz.devboot.DevBootProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;

import java.util.Map;

public class H2Console {
    private static final Log LOGGER = LogFactory.getLog(H2Console.class);

    private Flyway flyway;

    public H2Console(Flyway flyway) {
        this.flyway = flyway;
    }

    public void executeMigrate(){
        Map<String, String> flywayMap = DevBootProfile.getInstance().getFlyway();
        if (flywayMap.get("enabled") == null || flywayMap.get("enabled").equals("false")) {
            return;
        }
        flyway.migrate();
    }
}