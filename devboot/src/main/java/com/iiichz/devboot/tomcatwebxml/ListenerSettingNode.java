package com.iiichz.devboot.tomcatwebxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@XStreamAlias("listener")
public class ListenerSettingNode {
    @XStreamAlias("listener-class")
    public String listenerClass;
}
