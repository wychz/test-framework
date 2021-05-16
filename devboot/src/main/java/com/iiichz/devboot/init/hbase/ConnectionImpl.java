package com.iiichz.devboot.init.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ConnectionImpl implements Connection {

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public Table getTable(TableName tableName) throws IOException {
        Connection conn = null;
        try {
            conn = HBaseUtil.getInstance().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Table table = null;
        try {
            table = conn.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

    @Override
    public BufferedMutator getBufferedMutator(TableName tableName) throws IOException {
        return null;
    }

    @Override
    public BufferedMutator getBufferedMutator(BufferedMutatorParams bufferedMutatorParams) throws IOException {
        return null;
    }

    @Override
    public RegionLocator getRegionLocator(TableName tableName) throws IOException {
        return null;
    }

    @Override
    public void clearRegionLocationCache() {

    }

    @Override
    public Admin getAdmin() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public TableBuilder getTableBuilder(TableName tableName, ExecutorService executorService) {
        return null;
    }

    @Override
    public void abort(String s, Throwable throwable) {

    }

    @Override
    public boolean isAborted() {
        return false;
    }
}