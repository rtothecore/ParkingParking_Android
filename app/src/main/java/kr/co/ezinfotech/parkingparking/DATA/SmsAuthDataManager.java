package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

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

import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.DB.ParkingZoneDBCtrct;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class SmsAuthDataManager extends Activity {

    Context ctx = null;
    Handler mHandler = null;
    Handler mHandler2 = null;
    Handler mHandler3 = null;
    String phoneNo = null;
    String authCode = null;

    JSONArray result = null;
    ArrayList<SmsAuthData> smsAuthDatas = new ArrayList<>();

    public SmsAuthDataManager(Handler handlerVal, Context ctxVal) {
        mHandler = handlerVal;
        ctx = ctxVal;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    public void deleteSmsAuthCode(String phoneNoVal) {
        phoneNo = phoneNoVal;
        deleteSMSAuthThreadProcess();
    }

    private void deleteSMSAuthThreadProcess() {
        mHandler3 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    Log.i("deleteSMSAuth", "Delete 성공");
                    showDeleteSuccessDlg();
                } else {
                    Log.i("deleteSMSAuth", "Delete 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callDeleteSMSAuthREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler3.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callDeleteSMSAuthREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/deleteSMSAuth/" + phoneNo);

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

        try {
            responseCode = conn.getResponseCode();
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
            Log.e("CarTypeREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("CarTypeREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }

/*
    private int callDeleteSMSAuthREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/deleteSMSAuth/" + phoneNo);

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
            conn.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        try {
            responseCode = conn.getResponseCode();
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
            Log.e("CarTypeREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("CarTypeREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
*/
    // http://webnautes.tistory.com/1094
    private void showDeleteSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("인증시간이 만료되었습니다. 재발송 버튼을 눌러주세요.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        // finish();
                    }
                });
        builder.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START insertReportData
    public void createSmsAuthCode(String phoneNoVal) {
        phoneNo = phoneNoVal;
        createSmsAuthCodeThreadProcess();
    }

    private void createSmsAuthCodeThreadProcess() {
        mHandler2 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    // Toast.makeText(getApplicationContext(), "Insert 성공!", Toast.LENGTH_SHORT).show();
                    Log.i("createSmsAuthCode", "Insert 성공");
                    showInsertSuccessDlg();
                } else {
                    // Toast.makeText(getApplicationContext(), "Insert 실패", Toast.LENGTH_SHORT).show();
                    Log.i("createSmsAuthCode", "Insert 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callInsertREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler2.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callInsertREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/addNewSMSAuth"); /*URL*/

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
            jsonObject.accumulate("phone_no", phoneNo);
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
            responseCode = conn.getResponseCode();
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
        } catch (Throwable t) {
            Log.e("callInsertREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }

    // http://webnautes.tistory.com/1094
    private void showInsertSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("인증번호를 전송했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        // finish();
                    }
                });
        builder.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    /////////////////////////////////////////////////////////////////////////////////////////////////// START getMySmsAuthCode
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
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getAuthCode/" + phoneNo); /*URL*/

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
    /////////////////////////////////////////////////////////////////////////////////////////////////// END getMySmsAuthCode
}
