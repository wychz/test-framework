package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@XStreamAlias("filter")
public class FilterSettingNode {
    @XStreamAlias("filter-name")
    public String filterName;

    @XStreamAlias("filter-class")
    public String filterClass;

    @XStreamAlias("async-supported")
    public Boolean asyncSupported;
}
