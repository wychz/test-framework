package com.iiichz.microservicedevtest.jettymock;

import com.intuit.karate.netty.FeatureServer;

import java.io.File;
import java.net.URL;

public class JettyMockServer {


    private FeatureServer mockServer;

    public void startMockServer(int port) {
        URL url1 = this.getClass().getClassLoader().getResource("./");
        URL url = this.getClass().getClassLoader().getResource("acceptancetest/mock/server.feature");
        File file = new File(url.getPath());
        mockServer = FeatureServer.start(file, port, false, null);
        System.setProperty("karate.server.port", port + "");
        // needed to ensure we undo what the other test does to the jvm else ci fails
        System.setProperty("karate.server.ssl", "");
        System.setProperty("karate.server.proxy", "");
    }

    public void stopServer(){
        mockServer.stop();
    }
}