package com.iiichz.microservicedevtest.devboot;

import com.iiichz.devboot.DevBootProfile;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataRecover {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecover.class);

    boolean hasRecovered = false;
    String featureScenarioName;

    public DataRecover(String featureScenarioName){
        this.featureScenarioName = featureScenarioName;
    }

    public void recoverData() throws Exception {
        File dirFile = new File("src/test/resources/dbTemp");
        if(!dirFile.exists()){
            return;
        }
        List<File> files = searchFile(dirFile);
        for(File file: files){
            if(featureScenarioName.equals(file.getName())){
                recoverDataProcess(featureScenarioName);
            }
        }
        if(!hasRecovered) {
            LOGGER.error("cannot find backup files!");
        }
        if(dirFile.exists()){
            deleteDir(dirFile);
        }
    }

    private boolean deleteDir(File dir) {
        System.gc();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private List<File> searchFile(File dirFile){
        List<File> fileList = new ArrayList<>();
        File[] files = dirFile.listFiles();
        if(files != null && files.length > 0){
            for (File file : files) {
                if(!fileList.contains(file)){
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private void recoverDataProcess(String featureScenarioName) throws Exception{
        LOGGER.info("Begin to recover data from backup files!");
        hasRecovered = true;
    }
}