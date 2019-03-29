package kr.co.ezinfotech.parkingparking.NAVI;

import android.content.Context;
import android.util.Log;

import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;

public class TmapManager {

    private static final TmapManager ourInstance = new TmapManager();
    private static Context mContext = null;
    private static TMapTapi tMapTapi = null;
    private static final String key = "db111816-f2ff-4567-8128-2e20837a7be8";
    private static boolean isAuth = false;

    public static TmapManager getInstance() {
        return ourInstance;
    }

    private TmapManager() {
    }

    public static void setContext(Context ctxVal) {
        mContext = ctxVal;
    }

    public static void authentifacateKey() {
        tMapTapi = new TMapTapi(mContext);
        if(!isAuth) {
            tMapTapi.setSKTMapAuthentication (key);

            tMapTapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
                @Override
                public void SKTMapApikeySucceed() {
                    Log.d("TMAP","Tmap 키인증 성공");
                    isAuth = true;
                }
                @Override
                public void SKTMapApikeyFailed(String errorMsg) {
                    Log.d("TMAP","Tmap 키인증 실패");
                    Log.d("TMAP", errorMsg);
                }
            });
        }
    }

    public static void showRoute(String name, float lat, float lng) {
        if(isAuth) {
            tMapTapi.invokeRoute(name, lng, lat);
        } else {
            Log.d("TMAP","Tmap 키가 인증되지 않았습니다");
        }
    }

    public static boolean isTmapInstalled() {
        return tMapTapi.isTmapApplicationInstalled();
    }

    public static ArrayList getTmapDownUrl() {
        return tMapTapi.getTMapDownUrl();
    }
}
