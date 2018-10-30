package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.net.Uri;
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
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class UserDataManagerForLogin extends Activity {

    Handler mHandler = null;
    String email = null;
    String password = null;
    String nowPassword = null;
    int responseCode = 0;

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

    public void findPassword(String emailVal) {
        email = emailVal;
        runFindPasswordThreadProcess();
    }

    public void checkPasswordExpired(String emailVal) {
        email = emailVal;
        runCheckPasswordExpiredThreadProcess();
    }

    public void changePassword(String emailVal, String nowPwVal, String pwVal) {
        email = emailVal;
        nowPassword = nowPwVal;
        password = pwVal;
        runChangePasswordThreadProcess();
    }

    private void runChangePasswordThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                setPasswordData();

                Message message = Message.obtain();
                switch(responseCode) {
                    case 200:
                        message.arg1 = 200;
                        break;
                    case 201:
                        message.arg1 = 201;
                        break;
                    default:
                        break;
                }
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void setPasswordData() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/updateUserPassword"); /*URL*/

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
            conn.setRequestMethod("PUT");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("email", email);
            jsonObject.accumulate("nowPassword", nowPassword);
            jsonObject.accumulate("password", password);
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
            responseCode = conn.getResponseCode();
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
    }

    private void runCheckPasswordExpiredThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getPasswordExpiredData();

                Message message = Message.obtain();
                switch(responseCode) {
                    case 200:
                        message.arg1 = 200;
                        break;
                    case 201:
                        message.arg1 = 201;
                        break;
                    case 202:
                        message.arg1 = 202;
                        break;
                    case 203:
                        message.arg1 = 203;
                        break;
                    default:
                        break;
                }
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getPasswordExpiredData() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/checkPasswordExpired/" + email); /*URL*/

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
            responseCode = conn.getResponseCode();
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

        /*
        try {
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getPasswordExpiredData", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        Log.e("getPasswordExpiredData", Integer.toString(responseCode));
        */
    }

    private void runFindPasswordThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getPasswordData();

                Message message = Message.obtain();
                switch(responseCode) {
                    case 201:
                        message.arg1 = 201;
                        break;
                    case 200:
                        message.arg1 = 200;
                        break;
                    default:
                        break;
                }
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getPasswordData() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/findPassword/" + email); /*URL*/

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
            responseCode = conn.getResponseCode();
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

        /*
        try {
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getPasswordData", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        Log.e("getPasswordData", Integer.toString(responseCode));
        */
    }

    private void runGetUserDataThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getUserData();

                Message message = Message.obtain();
                switch(responseCode) {
                    case 201:
                        message.arg1 = 201;
                        break;
                    case 202:
                        message.arg1 = 202;
                        break;
                    case 200:
                        message.arg1 = 200;
                        break;
                    default:
                        break;
                }
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getUserData() {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/login/" + email + "/" + Uri.encode(password)); /*URL*/

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
            responseCode = conn.getResponseCode();
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

        // 응답코드가 200일 경우에만 데이터 파싱
        if(200 == responseCode) {
            try {
                result = new JSONArray(sb.toString());
            } catch (Throwable t) {
                Log.e("getUserData", "Could not parse malformed JSON");
                t.printStackTrace();
            }

            Log.e("getUserData", Integer.toString(responseCode));

            parseNaddDatas();
        }
    }

    private void parseNaddDatas() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < result.length(); i++) {
                JSONObject jsonTemp = (JSONObject)result.get(i);

                UserData tempUD = new UserData();
                tempUD.name = jsonTemp.getString("name");
                LoginManager.setName(tempUD.name);
                tempUD.email = jsonTemp.getString("email");
                tempUD.password = jsonTemp.getString("password");
                tempUD.phone_no = jsonTemp.getString("phone_no");
                LoginManager.setPhone(tempUD.phone_no);
                userDatas.add(tempUD);
            }
        } catch (Throwable t) {
            Log.e("parseNaddDatas()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
}
