package com.example.drsfordealer.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class LoginSessionManager {

    SharedPreferences loginSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_LOGIN = "SessionRememberMe";
    private  static  final  String IS_REMEMBER_ME = "isRememberMe";

    public   static  final  String USER_ID = "dealerID";
    public   static  final  String KEY_PASSWORD = "dealerPassword";

    public LoginSessionManager(Context context) {
        loginSession = context.getSharedPreferences(SESSION_LOGIN, Context.MODE_PRIVATE);
        editor = loginSession.edit();
        editor.apply();
    }

    public void createRememberMeSession(String userID, String password) {
        editor.putBoolean(IS_REMEMBER_ME, true);

        editor.putString(USER_ID, userID);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> loginCred = new HashMap<>();

        loginCred.put(USER_ID, loginSession.getString(USER_ID,null));
        loginCred.put(KEY_PASSWORD, loginSession.getString(KEY_PASSWORD,null));

        return loginCred;
    }

    public boolean checkLogin() {
        if (loginSession.getBoolean(IS_REMEMBER_ME, false)) {
            return true;
        } else return false;
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }
}
