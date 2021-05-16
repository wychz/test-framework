package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@XStreamAlias("servlet")
public class ServletSettingNode {
    @XStreamAlias("servlet-name")
    public String servletName;

    @XStreamAlias("servlet-class")
    public String servletClass;

    @XStreamImplicit
    public List<InitParamSettingNode> initParam;

    @XStreamAlias("load-on-startup")
    public Integer loadOnStartup;

    @XStreamAlias("async-supported")
    public Boolean asyncSupported;
}