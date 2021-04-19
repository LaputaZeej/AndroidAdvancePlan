package com.laputa.xrouter.api;

import android.util.LruCache;

import com.laputa.xrouter.annotations.Constant;

/**
 * Author by xpl, Date on 2021/4/9.
 */
class FieldManager {
    // Field缓存
    // ClassName to RouterField
    private final LruCache<String, RouterField> mCache;

    public static FieldManager getInstance() {
        return FieldManagerHolder.INSTANCE;
    }

    private FieldManager() {
        mCache = new LruCache<>(100);
    }

    private static final class FieldManagerHolder {
        private final static FieldManager INSTANCE = new FieldManager();
    }

    public void load(Object target) {
        // 放入缓存
        String key = target.getClass().getName();
        String routerFieldClassName = Utils.createRouterField(target.getClass());
        RouterField routerField = mCache.get(key);
        if (routerField == null) {
            try {
                Class<?> aClass = Class.forName(routerFieldClassName);
                routerField = (RouterField) aClass.newInstance();
                mCache.put(key, routerField);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 注入属性
        if (routerField != null) {
            routerField.loadField(target);
        }
    }




}
