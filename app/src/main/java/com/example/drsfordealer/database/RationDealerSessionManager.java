package com.example.drsfordealer.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class RationDealerSessionManager {
    SharedPreferences dealerSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_DEALER = "SessionLogIn";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    public static final String KEY_DEALER_ID = "dealerID";
    public static final String KEY_DEALER_NAME = "dealerName";
    public static final String KEY_DEALER_PASSWORD = "dealerPassword";
    public static final String KEY_DEALER_ACC_RECOVERY_KEY = "dealerAccountRecoveryCode";
    public static final String KEY_DEALER_LOCATION_1 = "dealerLocationState";
    public static final String KEY_DEALER_LOCATION_2 = "dealerLocationMunicipality";
    public static final String KEY_DEALER_LOCATION_3 = "dealerLocationDistrict";


    public RationDealerSessionManager(Context context) {
        dealerSession = context.getSharedPreferences(SESSION_DEALER, Context.MODE_PRIVATE);
        editor = dealerSession.edit();
        editor.apply();
    }

    public void createRationDealerSession(String dealerID, String dealerName, String dealerPassword,
                                          String dealerAccountRecoveryCode, String dealerLocationState,
                                          String dealerLocationDistrict, String dealerLocationMunicipality) {

        editor.putBoolean(IS_LOGGED_IN, true);

        editor.putString(KEY_DEALER_ID, dealerID);
        editor.putString(KEY_DEALER_NAME, dealerName);
        editor.putString(KEY_DEALER_PASSWORD, dealerPassword);
        editor.putString(KEY_DEALER_ACC_RECOVERY_KEY, dealerAccountRecoveryCode);
        editor.putString(KEY_DEALER_LOCATION_1, dealerLocationState);
        editor.putString(KEY_DEALER_LOCATION_2, dealerLocationDistrict);
        editor.putString(KEY_DEALER_LOCATION_3, dealerLocationMunicipality);
        editor.commit();
    }

    public HashMap<String, String> getDealerDetailsFromSession() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_DEALER_ID, dealerSession.getString(KEY_DEALER_ID, null));
        hashMap.put(KEY_DEALER_NAME, dealerSession.getString(KEY_DEALER_NAME, null));
        hashMap.put(KEY_DEALER_PASSWORD, dealerSession.getString(KEY_DEALER_PASSWORD, null));
        hashMap.put(KEY_DEALER_ACC_RECOVERY_KEY, dealerSession.getString(KEY_DEALER_ACC_RECOVERY_KEY, null));
        hashMap.put(KEY_DEALER_LOCATION_1, dealerSession.getString(KEY_DEALER_LOCATION_1, null));
        hashMap.put(KEY_DEALER_LOCATION_2, dealerSession.getString(KEY_DEALER_LOCATION_2, null));
        hashMap.put(KEY_DEALER_LOCATION_3, dealerSession.getString(KEY_DEALER_LOCATION_3, null));
        return hashMap;
    }

    public boolean checkLogin() {
        if (dealerSession.getBoolean(IS_LOGGED_IN, false)) {
            return true;
        } else return false;
    }

    public void logoutFromSession() {
        editor.clear();
        editor.commit();
    }
}
