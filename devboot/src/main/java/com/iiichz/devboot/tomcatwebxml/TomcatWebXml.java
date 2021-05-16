package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@XStreamAlias("web-app")
public class TomcatWebXml {
    @XStreamImplicit
    public List<ServletSettingNode> servlets;

    @XStreamImplicit
    public List<ServletMappingSettingNode> servletMappings;

    @XStreamImplicit
    public List<ListenerSettingNode> listeners;

    @XStreamImplicit
    public List<ContextParamSettingNode> contextParams;

    @XStreamImplicit
    public List<FilterSettingNode> filters;

    @XStreamImplicit
    public List<FilterMappingSettingNode> filterMappings;
}
