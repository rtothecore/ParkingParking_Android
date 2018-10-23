package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.DB.ParkingZoneDBCtrct;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class SmsAuthDataManager extends Activity {

    Handler mHandler = null;
    String phoneNo = null;
    String authCode = null;

    JSONArray result = null;
    ArrayList<SmsAuthData> smsAuthDatas = new ArrayList<>();

    public SmsAuthDataManager(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void getMySmsAuthCode(String phoneNoVal, String authCodeVal) {
        phoneNo = phoneNoVal;
        authCode = authCodeVal;
        runGetSmsAuthCodeThreadProcess();
    }

    private void runGetSmsAuthCodeThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getSmsAuthData();

                if(authCode.equals(smsAuthDatas.get(0).auth_code)) {
                    Message message = Message.obtain();
                    message.arg1 = 888;
                    mHandler.sendMessage(message);
                } else {
                    Message message = Message.obtain();
                    message.arg1 = 880;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getSmsAuthData() {
        StringBuilder urlBuilder = new StringBuilder("http://192.168.0.73:8083/getAuthCode/" + phoneNo); /*URL*/

        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");
        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();

        try {
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getSmsAuthData-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        parseNaddDatas();
    }

    private void parseNaddDatas() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < result.length(); i++) {
                JSONObject jsonTemp = (JSONObject)result.get(i);

                SmsAuthData tempSAD = new SmsAuthData();
                tempSAD.phone_no = jsonTemp.getString("phone_no");
                tempSAD.auth_code = jsonTemp.getString("auth_code");
                tempSAD.auth_date = jsonTemp.getString("auth_date");

                smsAuthDatas.add(tempSAD);
            }
        } catch (Throwable t) {
            Log.e("parseNaddDatas()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
}
