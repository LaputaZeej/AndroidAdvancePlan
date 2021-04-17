//package com.laputa.xrouter.api;
//
//import android.app.Activity;
//import android.util.LruCache;
//
///**
// * Author by xpl, Date on 2021/4/9.
// */
//class FieldManager {
//
//    private LruCache<String, FieldGet> cache;
//
//    private static final String RouterFiled_NAME = "$$Field"; // 对应生成规则
//
//    public void load(Activity activity) {
//        String key = activity.getClass().getName();
//        String targetClassName = Utils.findActivityFieldClassName(activity.getClass(), RouterFiled_NAME);
//        FieldGet fieldGet = cache.get(key);
//        if (fieldGet == null) {
//            try {
//                Class<?> aClass = Class.forName(targetClassName);
//                fieldGet = (FieldGet) aClass.newInstance();
//                cache.put(key, fieldGet);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (fieldGet != null) {
//            fieldGet.loadField(activity);
//        }
//    }
//
//    public static com.laputa.rounter.api.FieldManager getInstance() {
//        return FieldManagerHolder.INSTANCE;
//    }
//
//    private FieldManager() {
//        cache = new LruCache<>(100);
//    }
//
//    private static final class FieldManagerHolder {
//        private final static com.laputa.rounter.api.FieldManager INSTANCE = new com.laputa.rounter.api.FieldManager();
//    }
//
//
//}
