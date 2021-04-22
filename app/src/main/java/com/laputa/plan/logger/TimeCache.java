package com.laputa.plan.logger;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by xpl, Date on 2021/4/16.
 */
public class TimeCache {

    private static Map<String, Long> caches = new HashMap<>();

    public static void setStartTime(String tag, long time) {
        Log.i("TimeCache", "setStartTime");
        caches.put(tag, time);
    }

    public static void setEndTime(String tag, long time) {
        Log.i("TimeCache", "setEndTime");
        if (caches.containsKey(tag)) {
            long start = caches.get(tag);
            caches.put(tag, time - start);
        }
    }

    public static String getCostTime(String tag) {
        String msg;
        if (caches.containsKey(tag)) {
            msg = "消耗时间：" + caches.get(tag) + "ms";
            caches.clear();
        } else {
            msg = "没有消耗记录";
        }
        Log.i("TimeCache", msg);
        return msg;
    }


}
