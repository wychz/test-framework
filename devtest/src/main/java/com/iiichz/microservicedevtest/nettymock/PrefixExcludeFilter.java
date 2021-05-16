package com.iiichz.microservicedevtest.nettymock;

import java.io.File;
import java.io.FilenameFilter;

class PrefixExcludeFilter implements FilenameFilter {
    private String prefix;

    public PrefixExcludeFilter(String prefix) {
        this.prefix = prefix.toLowerCase();
    }

    @Override
    public boolean accept(File dir, String name) {
        return !name.toLowerCase().startsWith(prefix);
    }
}
