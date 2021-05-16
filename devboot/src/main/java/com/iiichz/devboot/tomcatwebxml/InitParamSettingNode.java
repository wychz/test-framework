package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@XStreamAlias("init-param")
public class InitParamSettingNode {
    @XStreamAlias("param-name")
    public String paramName;

    @XStreamAlias("param-value")
    public String paramValue;
}

