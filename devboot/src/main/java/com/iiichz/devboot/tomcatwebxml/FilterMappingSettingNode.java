package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@XStreamAlias("filter-mapping")
public class FilterMappingSettingNode {
    @XStreamAlias("filter-name")
    public String filterName;

    @XStreamAlias("url-pattern")
    public String urlPattern;
}
