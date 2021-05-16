package com.iiichz.devboot.init.hbase;

import com.iiichz.embeddedhbase.*;
import org.apache.hadoop.hbase.client.Connection;

public class HBaseInit {
    private volatile static EmbeddedHBase fakeHBase;

    private static Connection Connection;

    public static void init(){
        String hBase = System.getProperty("HBase");
        if (hBase == null) {
            return;
        }

        FileNotFoundMock.init();

        if(null == fakeHBase){
            synchronized (HBaseInit.class){
                if(null == fakeHBase){
                    fakeHBase = new EmbeddedHBase();
                }
            }
        }

        Connection = fakeHBase.getHConnection();
    }

    protected static Connection getConnection(){
        return Connection;
    }

    protected static EmbeddedHBase getInstance() {
        return fakeHBase;
    }
}