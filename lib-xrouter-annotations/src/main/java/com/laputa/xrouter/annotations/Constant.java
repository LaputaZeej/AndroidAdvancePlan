package com.laputa.xrouter.annotations;

/**
 * Author by xpl, Date on 2021/4/7.
 */
public class Constant {

    // Android
    public static final String ANDROID_ACTIVITY = "android.app.Activity";
    public static final String ANDROID_BUNDLE = "android.os.Bundle";
    public static final String ANDROID_PARCELABLE = "android.os.Parcelable";
    // 接口包名、方法名等
    private static final String INTERFACE_PACKAGE_NAME = "com.laputa.xrouter.api";
    public static final String INTERFACE_ROUTER_GROUP_PACKAGE_NAME = INTERFACE_PACKAGE_NAME + ".RouterGroup";
    public static final String INTERFACE_ROUTER_PATH_PACKAGE_NAME = INTERFACE_PACKAGE_NAME + ".RouterPath";
    public static final String INTERFACE_ROUTER_FIELD_PACKAGE_NAME = INTERFACE_PACKAGE_NAME + ".RouterField";
    public static final String INTERFACE_METHOD_ROUTER_PATH = "getPathMap";
    public static final String INTERFACE_METHOD_ROUTER_GROUP = "getGroupMap";
    public static final String INTERFACE_METHOD_ROUTER_FIELD = "loadField";


    public static final String MODULE_NAME_FOR_APT = "modelName";
    // 获取生成类的包名KEY
    public static final String PACKAGE_NAME_FOR_APT = "aptPackageName";
    // 生成接口实现类的类名规则
    public static final String PREFIX_ROUTER_GROUP = "XRouter$$Group$$";
    public static final String PREFIX_ROUTER_PATH = "XRouter$$Path$$";
    public static final String PREFIX_ROUTER_FILED = "$$XRouter$$Field";


}
