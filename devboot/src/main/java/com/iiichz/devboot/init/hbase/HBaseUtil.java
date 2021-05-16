package com.iiichz.devboot.init.hbase;

import com.iiichz.embeddedhbase.EmbeddedHBase;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseUtil {
    private static volatile HBaseUtil instance;

    private EmbeddedHBase fakeHBase;
    private Connection fakeHConnection;

    private HBaseUtil() throws Exception {
        fakeHConnection = HBaseInit.getConnection();
        fakeHBase = HBaseInit.getInstance();
        if (fakeHConnection == null) {
            throw new Exception("HBaseUtil init error");
        }
    }

    public static synchronized HBaseUtil getInstance() throws Exception {
        if (null == instance) {
            synchronized (HBaseUtil.class){
                if(null == instance){
                    instance = new HBaseUtil();
                }
            }
        }
        return instance;
    }

    public EmbeddedHBase getFakeHBase(){
        return fakeHBase;
    }

    public Connection getConnection(){
        return fakeHConnection;
    }

    public void createTable(String tableName) throws IOException {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        fakeHBase.Admin().createTable(desc);
    }

    public void createTable(String tableName, String columnFamily) throws IOException {
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(columnFamily));
        hTableDescriptor.addFamily(hColumnDescriptor);
        fakeHBase.Admin().createTable(hTableDescriptor);
    }

    public void deleteTable(String tableName){
        fakeHBase.Admin().deleteTable(tableName);
    }
}