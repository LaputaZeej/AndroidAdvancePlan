//package com.laputa.xrouter.api;
//
//import android.content.Context;
//import android.os.Bundle;
//
///**
// * Author by xpl, Date on 2021/4/9.
// */
//public class BundleBox {
//    private Bundle data = new Bundle();
//    private String path;
//    private String group;
//
//    public BundleBox(String path, String group) {
//        this.path = path;
//        this.group = group;
//    }
//
//     String getPath() {
//        return path;
//    }
//
//     String getGroup() {
//        return group;
//    }
//
//    public Bundle getData() {
//        return data;
//    }
//
//    public BundleBox with(String key, String value) {
//        data.putString(key, value);
//        return this;
//    }
//
//    public BundleBox with(String key, int value) {
//        data.putInt(key, value);
//        return this;
//    }
//
//    public BundleBox with(String key, long value) {
//        data.putLong(key, value);
//        return this;
//    }
//
//    public BundleBox with(String key, boolean value) {
//        data.putBoolean(key, value);
//        return this;
//    }
//
//    public BundleBox with(Bundle bundle) {
//        this.data = bundle;
//        return this;
//    }
//
//    public void navigation(Context context) {
//        RouterManager.getInstance().navigation(context, this);
//    }
//
//}
