package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
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

/**
 * Created by hkim on 2018-04-17.
 */

public class UserDataManagerForLogin extends Activity {

    Handler mHandler = null;
    String email = null;
    String password = null;
    boolean isLogin = false;

    JSONArray result = null;
    ArrayList<UserData> userDatas = new ArrayList<>();

    public UserDataManagerForLogin(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void getEmailNPassword(String emailVal, String pwVal) {
        email = emailVal;
        password = pwVal;
        runGetUserDataThreadProcess();
    }

    private void runGetUserDataThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getUserData();

                if(isLogin) {
                    Message message = Message.obtain();
                    message.arg1 = 555;
                    mHandler.sendMessage(message);
                } else {
                    Message message = Message.obtain();
                    message.arg1 = 550;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getUserData() {
        StringBuilder urlBuilder = new StringBuilder("http://192.168.0.73:8083/login/" + email + "/" + password); /*URL*/

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

                UserData tempUD = new UserData();
                tempUD.name = jsonTemp.getString("name");
                tempUD.email = jsonTemp.getString("email");
                tempUD.password = jsonTemp.getString("password");

                userDatas.add(tempUD);
            }
        } catch (Throwable t) {
            Log.e("parseNaddDatas()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(1 <= userDatas.size()) {
            isLogin = true;
        }
    }
}
