package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class UserDataManager extends Activity {

    Handler mHandler = null;
    String name = null;
    String email = null;
    String password = null;
    String phone_no = null;
    boolean isInsertSuccess = false;
    boolean isExistEmail = false;

    public UserDataManager(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void setUserData(String nameVal, String emailVal, String pwVal, String phoneNoVal) {
        name = nameVal;
        email = emailVal;
        password = pwVal;
        phone_no = phoneNoVal;
        insertUserData();
    }

    public void isExistEmail(String emailVal) {
        email = emailVal;
        checkDuplicateEmail();
    }

    private void checkDuplicateEmail() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getExistEmail();

                if(isExistEmail) {      // 이미 존재하는 이메일
                    Message message = Message.obtain();
                    message.arg1 = 677;
                    mHandler.sendMessage(message);
                } else {                // 존재하지 않는 이메일
                    Message message = Message.obtain();
                    message.arg1 = 670;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getExistEmail() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getDuplicatedEmail/" + email); /*URL*/

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
            // responseCode = conn.getResponseCode();
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

        JSONArray result = null;
        try {
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getSmsAuthData-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(0 == result.length()) {
            isExistEmail = false;
        } else {
            isExistEmail = true;
        }
        // Log.e("getSmsAuthData-4", Integer.toString(responseCode));
    }

    private void insertUserData() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                callInsertREST();

                if(isInsertSuccess) {   // insert 성공
                    Message message = Message.obtain();
                    message.arg1 = 666;
                    mHandler.sendMessage(message);
                } else {                // insert 실패
                    Message message = Message.obtain();
                    message.arg1 = 660;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void callInsertREST() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/addNewUser"); /*URL*/

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
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("name", name);
            jsonObject.accumulate("email", email);
            jsonObject.accumulate("password", password);
            jsonObject.accumulate("phone_no", phone_no);
            jsonObject.accumulate("level", "99");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // convert JSONObject to JSON to String
        String json = jsonObject.toString();

        // Set some headers to inform server about the type of the content
        conn.setRequestProperty("Accept", "application/json");

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        conn.setDoOutput(true);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        conn.setDoInput(true);

        try {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            Log.e("callInsertREST-0", sb.toString());
            isInsertSuccess = true;
        } catch (Throwable t) {
            Log.e("callInsertREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
}
