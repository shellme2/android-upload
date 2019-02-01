package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
    private final Editor mEditor;
    private final SharedPreferences mPreferences;

    public SharedPreferencesUtil(Context context, String fileName) {
        this.mPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.mEditor = this.mPreferences.edit();
    }

    public SharedPreferencesUtil(Context context, String fileName, int mode) {
        this.mPreferences = context.getSharedPreferences(fileName, mode);
        this.mEditor = this.mPreferences.edit();
    }

    public void putString(String name, String value) {
        mEditor.putString(name, value);
        mEditor.apply();
    }

    public void putLong(String name, Long value) {
        mEditor.putLong(name, value);
        mEditor.apply();
    }

    public void putInt(String name, int value) {
        mEditor.putInt(name, value);
        mEditor.apply();
    }

    public void putBoolean(String name, Boolean value) {
        mEditor.putBoolean(name, value);
        mEditor.apply();
    }

    public void remove(String name) {
        mEditor.remove(name);
        mEditor.apply();
    }

    public void clear() {
        mEditor.clear();
        mEditor.apply();
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public Boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public Boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public Editor getEditor() {
        return mEditor;
    }
}

