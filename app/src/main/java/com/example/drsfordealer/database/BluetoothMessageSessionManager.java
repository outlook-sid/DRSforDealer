package com.example.drsfordealer.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class BluetoothMessageSessionManager {
    SharedPreferences loginSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_LOGIN = "BluetoothMessage";
    private  static  final  String IS_BT = "IsBt";

    public   static  final  String KEY_BT_MESSAGE = "Bluetooth_Message";
    public   static  final  String KEY_UID = "User_ID";

    public BluetoothMessageSessionManager(Context context) {
        loginSession = context.getSharedPreferences(SESSION_LOGIN, Context.MODE_PRIVATE);
        editor = loginSession.edit();
        editor.apply();
    }

    public void createBTMessageSession(String message, String uid) {
        editor.putBoolean(IS_BT, true);

        editor.putString(KEY_BT_MESSAGE, message);
        editor.putString(KEY_UID, uid);

        editor.commit();
    }

    public HashMap<String, String> getBTMessageDetailsFromSession() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_BT_MESSAGE, loginSession.getString(KEY_BT_MESSAGE,null));
        hashMap.put(KEY_UID, loginSession.getString(KEY_UID,null));
        return hashMap;
    }

    public boolean checkLogin() {
        if (loginSession.getBoolean(IS_BT, false)) {
            return true;
        } else return false;
    }

    public void logoutFromSession() {
        editor.clear();
        editor.commit();
    }
}
