package com.laputa.xrouter.api;

import android.app.Activity;
import android.content.Intent;
import android.util.LruCache;

import com.laputa.xrouter.annotations.RouterBean;

import java.util.Map;

/**
 * Author by xpl, Date on 2021/4/19.
 */
public class RouterManager {

    private LruCache<String, RouterPath> mPathCache;
    private LruCache<String, RouterGroup> mGroupCache;

    static RouterManager getInstance() {
        return RouterManager.RouterManagerHolder.INSTANCE;
    }

    private RouterManager() {
        mPathCache = new LruCache<>(100);
        mGroupCache = new LruCache<>(100);
    }

    private static final class RouterManagerHolder {
        private final static RouterManager INSTANCE = new RouterManager();
    }

    BundleBox build(String path) {
        if (check(path)) {
            String group = path.substring(1, path.indexOf("/", 1));
            return new BundleBox(path, group);
        }
        throw new RuntimeException("不支持的path:" + path);
    }

    private boolean check(String path) {
        // todo 校验
        return true;
    }

    Object navigation(Object obj, BundleBox bundleBox) {
        String path = bundleBox.getPath();
        String group = bundleBox.getGroup();
        // 添加mGroupCache
        RouterGroup routerGroup = getRouterGroup(group);
        Class<? extends RouterPath> routerPathClass = routerGroup.getGroupMap().get(group);
        RouterPath routerPath = getRouterPath(path, routerPathClass);
        RouterBean routerBean = routerPath.getPathMap().get(path);
        if (routerBean == null) {
            throw new RuntimeException("RouterBean is null");
        }
        switch (routerBean.getType()) {
            case ACTIVITY:
                Activity activity = (Activity) obj;
                Intent intent = new Intent(activity, routerBean.getAnnotationClass());
                intent.putExtras(bundleBox.getData());
                activity.startActivity(intent);
                break;
            case FRAGMENT:
                // Fragment
            case SERVICE:
                // Service
                break;
            default:
                break;
        }
        return null;
    }

    private RouterGroup getRouterGroup(String group) {
        RouterGroup routerGroup = mGroupCache.get(group);
        String routerGroupClassName = Utils.createRouterGroup(group);
        if (routerGroup == null) {
            try {
                Class<?> aClass = Class.forName(routerGroupClassName);
                routerGroup = (RouterGroup) aClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (routerGroup == null || routerGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("没有生成RouterGroup");
            }
            mGroupCache.put(group, routerGroup);
        }

        return routerGroup;
    }

    private RouterPath getRouterPath(String path, Class<?> routerPathClass) {
        RouterPath routerPath = mPathCache.get(path);
        if (routerPath == null) {
            try {
                routerPath = (RouterPath) routerPathClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (routerPath == null || routerPath.getPathMap().isEmpty()) {
                throw new RuntimeException("没有生成RouterPath");
            }
            mPathCache.put(path, routerPath);
        }
        return routerPath;
    }


}
