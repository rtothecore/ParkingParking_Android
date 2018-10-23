package kr.co.ezinfotech.parkingparking.UTIL;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class LoginManager {
    private static final LoginManager ourInstance = new LoginManager();

    private static boolean isLogin = false;
    private static String email = null;

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private LoginManager() {
    }

    public static void setEmail(String emailVal) {
        email = emailVal;
        isLogin = true;
    }

    public static boolean isLogin() {
        return isLogin;
    }
}
