package com.laputa.xrouter.api;

import com.laputa.xrouter.annotations.Constant;

/**
 * Author by xpl, Date on 2021/4/19.
 */
public class Utils {

    /**
     * 寻找生成的RouterField规则的完整类名
     *
     * @param clz    宿主Class 以后可能为 Object
     * @return
     */
    public static String createRouterField(Class<?> clz) {
        return BuildConfig.PACKAGE_NAME_FOR_APT + "." + clz.getSimpleName() +Constant.PREFIX_ROUTER_FILED;
    }

    public static String createRouterGroup(String group) {
        return  BuildConfig.PACKAGE_NAME_FOR_APT+ "." + Constant.PREFIX_ROUTER_GROUP + group;
    }
}
