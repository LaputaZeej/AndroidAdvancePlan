package com.laputa.xrouter.api;

/**
 * Author by xpl, Date on 2021/4/19.
 */
public class XRouter {

    public static void load(Object target) {
        FieldManager.getInstance().load(target);
    }

    public static BundleBox build(String path) {
        return RouterManager.getInstance().build(path);
    }
}
