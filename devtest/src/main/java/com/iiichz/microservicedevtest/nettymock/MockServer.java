package com.iiichz.microservicedevtest.nettymock;

import com.intuit.karate.netty.FeatureServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * JettyMockServer
 *
 * @author h00549738
 * @since [2020.9.26]
 * @since 2020.9.26
 */
public class MockServer {

    public static final String SETTINGS = "Settings:";

    public static final String MOCK_PORT = "mockPort";

    public static final String ACCEPTANCETEST_MOCK_PATH = "acceptancetest/mock";

    public static final String MOCK_FILE_SUFFIX = ".feature";

    public static final String TMP_FILE_PREFIX = "___tmp___";

    public static final String SUB_FEATURES = "SubFeatures:";

    public static final String UTF_8 = "UTF-8";

    private List<FeatureServer> featureServers = new ArrayList<>();

    /**
     * startMockServer
     *
     * @author h00549738
     * @since [2020.9.26]
     * @since 2020.9.26
     */
    public void startMockServer() {
        URL url = this.getClass().getClassLoader().getResource(ACCEPTANCETEST_MOCK_PATH);
        File dir = new File(url.getPath());
        File[] files = dir.listFiles(new PrefixExcludeFilter(TMP_FILE_PREFIX));
        Set<Integer> portSet = new HashSet<>();

        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(MOCK_FILE_SUFFIX)) {
                Settings settings = parseSettings(f);
                Integer port = settings.getPort();
                if (Objects.isNull(port)) {
                    continue;
                }
                if (!portSet.contains(port)) {
                    f = mergeSubFeatures(f, settings.getSubFeatures());
                    featureServers.add(FeatureServer.start(f, port, false, null));
                    System.setProperty("karate.server.port", port + "");
                    portSet.add(port);
                } else {
                    throw new RuntimeException("The assigned mock port(" + port + ") is conflicted.");
                }
            }
        }

        // needed to ensure we undo what the other test does to the jvm else ci fails
        System.setProperty("karate.server.ssl", "");
        System.setProperty("karate.server.proxy", "");
    }

    private File mergeSubFeatures(File f, List<String> subFeatures) {

        if (subFeatures != null && subFeatures.size() > 0) {
            URL url = this.getClass().getClassLoader().getResource(ACCEPTANCETEST_MOCK_PATH);
            Path source = f.toPath();
            File mergeFile = new File(url.getPath() + "/" + TMP_FILE_PREFIX + f.getName());

            try {
                Files.copy(source, mergeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> lineList = new ArrayList<>();
            for (String featurePath : subFeatures) {
                lineList.addAll(parseScenario(featurePath));
            }

            if (lineList.size() > 0) {
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(mergeFile, true), UTF_8))) {
                    writer.newLine();
                    for (String s : lineList) {
                        writer.newLine();
                        writer.append(s);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mergeFile;
        }

        return f;
    }

    private List<String> parseScenario(String featurePath) {
        List<String> lineList = new ArrayList<>();
        URL url = this.getClass().getClassLoader().getResource(ACCEPTANCETEST_MOCK_PATH + "/" + featurePath);
        File file = new File(url.getPath());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("Scenario:")) {
                    lineList.add(line);
                    while ((line = bufferedReader.readLine()) != null) {
                        lineList.add(line);
                    }
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

    private Settings parseSettings(File f) {
        Settings settings = new Settings();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals(SETTINGS)) {
                    line = bufferedReader.readLine().trim();
                    if (line.startsWith(MOCK_PORT)) {
                        settings.setPort(Integer.valueOf(line.substring(line.indexOf("=") + 1).trim()));
                    }
                    continue;
                }
                if (line.trim().equals(SUB_FEATURES)) {
                    while ((line = bufferedReader.readLine()) != null && line.trim().length() > 0) {
                        if (line.trim().length() > 0) {
                            settings.getSubFeatures().add(line.trim());
                        }
                    }
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return settings;
    }
}