package com.iiichz.devboot.init.hbase;

import mockit.Mock;
import mockit.MockUp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class FileNotFoundMock {
    public static void init() {
        new MockUp<HBaseConfiguration>() {
            @Mock
            private Configuration addHbaseResources(Configuration conf) {
                conf.addResource("hbase-default.xml");
                conf.addResource("hbase-site.xml");
                return conf;
            }
        };
    }
}
