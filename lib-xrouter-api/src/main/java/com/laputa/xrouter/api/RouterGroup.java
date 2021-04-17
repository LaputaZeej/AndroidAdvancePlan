package com.laputa.xrouter.api;

import java.util.Map;

/**
 * Author by xpl, Date on 2021/4/17.
 */
public interface RouterGroup {
    Map<String, Class<? extends RouterPath>> getGroupMap();
}
