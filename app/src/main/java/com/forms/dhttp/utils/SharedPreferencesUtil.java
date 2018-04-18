package com.forms.dhttp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil sharedPreferencesUtil;

    private String name;

    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtil == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sharedPreferencesUtil == null) {
                    sharedPreferencesUtil = new SharedPreferencesUtil();
                }
            }
        }
        return sharedPreferencesUtil;
    }

    public void init(String name) {
        this.name = name;
    }

    public <T> boolean setValue(Context context, String key, T value) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        if (value.getClass().isAssignableFrom(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else if (value.getClass().isAssignableFrom(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().isAssignableFrom(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass().isAssignableFrom(Long.class)) {
            editor.putLong(key, (Long) value);
        } else if (value.getClass().isAssignableFrom(Float.class)) {
            editor.putFloat(key, (Float) value);
        } else {
            throw new RuntimeException("Not support type.Please ensure type is in [Integer,String,Boolean,Long,Float]");
        }
        return editor.commit();
    }

    public <T> T getValue(Context context, String key, Class<T> clazz) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return getValue(key, clazz, sp);
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        if (clazz.isAssignableFrom(Integer.class)) {
            return (T) Integer.valueOf(sp.getInt(key, 0));
        } else if (clazz.isAssignableFrom(String.class)) {
            return (T) sp.getString(key, null);
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            return (T) Boolean.valueOf(sp.getBoolean(key, false));
        } else if (clazz.isAssignableFrom(Long.class)) {
            return (T) Long.valueOf(sp.getLong(key, 0L));
        } else if (clazz.isAssignableFrom(Float.class)) {
            return (T) Float.valueOf(sp.getFloat(key, 0L));
        }
        return null;
    }

    public boolean setObject(Context context, String key, Object object) {
        boolean isSuccess = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            isSuccess = setValue(context, key, objectVal);
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
            }
        }
        return isSuccess;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Context context, String key, Class<T> clazz) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, "");
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                return (T) ois.readObject();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            } finally {
                try {
                    bais.close();
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    LogUtil.e(e.getMessage());
                }
            }
        }
        return null;
    }

    public boolean delete(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }
}
