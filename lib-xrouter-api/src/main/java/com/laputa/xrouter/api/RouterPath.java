package com.laputa.xrouter.api;

import com.laputa.xrouter.annotations.RouterBean;

import java.util.Map;

/**
 * Author by xpl, Date on 2021/4/17.
 */
public interface RouterPath {
    Map<String, RouterBean> getPathMap();
}

//class RouterPath$$ implements RouterPath{
//    @Override
//    public Map<String, RouterBean> getPathMap() {
//        Map<String,RouterBean> pathMap = new HashMap<>();
//        return null;
//    }
//}
