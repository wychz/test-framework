package com.iiichz.microservicedevtest.nettymock;

import java.util.ArrayList;
import java.util.List;

/**
 * JettyMockServer
 *
 * @author h00549738
 * @since [2020.9.26]
 * @since 2020.9.26
 */
public class Settings {

    private Integer port = null;

    private List<String> subFeatures = new ArrayList<>();

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getSubFeatures() {
        return subFeatures;
    }

    public void setSubFeatures(List<String> subFeatures) {
        this.subFeatures = subFeatures;
    }
}
