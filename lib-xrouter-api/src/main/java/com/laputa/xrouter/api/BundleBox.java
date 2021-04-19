package com.laputa.xrouter.api;

import android.os.Bundle;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * todo Builder模式
 * Author by xpl, Date on 2021/4/9.
 */
public class BundleBox {
    private Bundle data = new Bundle();
    private final String path;
    private final String group;
    private WeakReference<Object> target;

     BundleBox(String path, String group) {
        this.path = path;
        this.group = group;
    }

     BundleBox(String path, String group, Object target) {
        this.path = path;
        this.group = group;
        this.target = new WeakReference<>(target);
    }

    String getPath() {
        return path;
    }

    String getGroup() {
        return group;
    }

    public WeakReference<Object> getTarget() {
        return target;
    }

    public Bundle getData() {
        return data;
    }

    public BundleBox with(String key, String value) {
        data.putString(key, value);
        return this;
    }

    public BundleBox with(String key, int value) {
        data.putInt(key, value);
        return this;
    }

    public BundleBox with(String key, long value) {
        data.putLong(key, value);
        return this;
    }

    public BundleBox with(String key, boolean value) {
        data.putBoolean(key, value);
        return this;
    }

    public BundleBox with(Bundle bundle) {
        this.data = bundle;
        return this;
    }

    public BundleBox with(String key,Parcelable parcelable) {
        data.putParcelable(key, parcelable);
        return this;
    }

    public void navigation(Object target) {
        RouterManager.getInstance().navigation(target,this);
    }

}
