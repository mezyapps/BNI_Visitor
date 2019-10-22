package com.mezyapps.bni_visitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;


public class SharedLoginUtils {


    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static String getLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(ConstantFields.LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        return preferences.getString(ConstantFields.IS_LOGIN, "");
    }

    public static void putLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(ConstantFields.LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(ConstantFields.IS_LOGIN, "true");
        editor.commit();
    }

    public static void removeLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(ConstantFields.LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(ConstantFields.IS_LOGIN, "false");
        editor.commit();
    }
    public static void addUserId(Context mContext,String user_id)
    {
        preferences = mContext.getSharedPreferences(ConstantFields.LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(ConstantFields.USER_ID,user_id);
        editor.commit();
    }

    public static String  getUserId(Context mContext) {
        preferences = mContext.getSharedPreferences(ConstantFields.LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        String user_id=preferences.getString(ConstantFields.USER_ID, "");
        return user_id;
    }



}
