package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@XStreamAlias("servlet-mapping")
public class ServletMappingSettingNode {
    @XStreamAlias("servlet-name")
    public String servletName;

    @XStreamAlias("url-pattern")
    public String urlPattern;
}