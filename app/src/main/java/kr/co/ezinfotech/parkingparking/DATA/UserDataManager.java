package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

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

import kr.co.ezinfotech.parkingparking.LoginActivity;
import kr.co.ezinfotech.parkingparking.PRIVATE_INFO.CarNoChangeActivity;
import kr.co.ezinfotech.parkingparking.PRIVATE_INFO.CarTypeChangeActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class UserDataManager extends Activity {

    Handler mHandler = null;
    Handler mHandler2 = null;
    Handler mHandler3 = null;
    Handler mHandler4 = null;
    String name = null;
    String email = null;
    String password = null;
    String phone_no = null;
    boolean isInsertSuccess = false;
    boolean isExistEmail = false;

    Context parentCtx = null;
    JSONArray resultData = null;
    UserData uData = null;

    String carNo = null;
    String carType = null;
    String leaveReason = null;

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

    public void setUserDataWithCarNo(Context ctxVal, String emailVal, String carNoVal) {
        parentCtx = ctxVal;
        email = emailVal;
        carNo = carNoVal;
        setUserDataWithCarNoThreadProcess();
    }

    public void setUserDataWithCarType(Context ctxVal, String emailVal, String carTypeVal) {
        parentCtx = ctxVal;
        email = emailVal;
        carType = carTypeVal;
        setUserDataWithCarTypeThreadProcess();
    }

    public void isExistEmail(String emailVal) {
        email = emailVal;
        checkDuplicateEmail();
    }

    public void getUserData(Context ctxVal, String emailVal) {
        parentCtx = ctxVal;
        email = emailVal;
        getUserDataThreadProcess();
    }

    public void leaveUser(Context ctxVal, String emailVal, String leaveReasonVal) {
        parentCtx = ctxVal;
        email = emailVal;
        leaveReason = leaveReasonVal;
        deleteUserDataThreadProcess();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    private void deleteUserDataThreadProcess() {
        mHandler4 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    Log.i("deleteUserData", "Delete 성공");
                    showDeleteuUserSuccessDlg();
                } else {
                    Log.i("deleteUserData", "Delete 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callDeleteUserREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler4.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callDeleteUserREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/leaveUserWithEmail/" + email); /*URL*/

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

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("leave_reason", leaveReason);
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
            Log.e("CarTypeREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("CarTypeREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    private void setUserDataWithCarTypeThreadProcess() {
        mHandler3 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    Log.i("setUserDataWithCarType", "Update 성공");
                    showUpdateuUserCarTypeSuccessDlg();
                } else {
                    Log.i("setUserDataWithCarType", "Update 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callUpdateUserCarTypeREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler3.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callUpdateUserCarTypeREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/updateUserWithCarType/" + email); /*URL*/

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
            jsonObject.accumulate("car_type", carType);
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
            Log.e("CarTypeREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("CarTypeREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    private void setUserDataWithCarNoThreadProcess() {
        mHandler3 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    // Toast.makeText(getApplicationContext(), "Insert 성공!", Toast.LENGTH_SHORT).show();
                    Log.i("setUserDataWithCarNo", "Update 성공");
                    showUpdateuUserCarNoSuccessDlg();
                } else {
                    // Toast.makeText(getApplicationContext(), "Insert 실패", Toast.LENGTH_SHORT).show();
                    Log.i("setUserDataWithCarNo", "Update 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callUpdateUserCarNoREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler3.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callUpdateUserCarNoREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/updateUserWithCarNo/" + email); /*URL*/

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
            jsonObject.accumulate("car_no", carNo);
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
            Log.e("UserCarNoREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("UserCarNoREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    // http://webnautes.tistory.com/1094
    private void showDeleteuUserSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentCtx);
        builder.setMessage("회원탈퇴를 실행했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.logout();
                        Intent intent = new Intent(parentCtx, LoginActivity.class);
                        parentCtx.startActivity(intent);
                        ((Activity)parentCtx).finish();
                    }
                });
        builder.show();
    }

    // http://webnautes.tistory.com/1094
    private void showUpdateuUserCarTypeSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentCtx);
        builder.setMessage("차량종류를 수정했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((CarTypeChangeActivity)CarTypeChangeActivity.mContext).callParentSetCarType(carType);
                        ((Activity)parentCtx).finish();
                    }
                });
        builder.show();
    }

    // http://webnautes.tistory.com/1094
    private void showUpdateuUserCarNoSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentCtx);
        builder.setMessage("차량번호를 수정했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((CarNoChangeActivity)CarNoChangeActivity.mContext).callParentSetCarNo(carNo);
                        ((Activity)parentCtx).finish();
                    }
                });
        builder.show();
    }

    private void getUserDataThreadProcess() {
        mHandler2 = new Handler(Looper.getMainLooper()) {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Get reportCode successfully
                    Log.i("getUserDataThread", "Get user data successfully");
                    setDataToUI();
                } else {
                    Log.i("getUserDataThread", "Get user data failed");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = getUserDataWithEmail();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler2.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void setDataToUI() {
        TextView tvPrivateInfoName = ((Activity)parentCtx).findViewById(R.id.tvPrivateInfoName);
        tvPrivateInfoName.setText(uData.name);

        TextView tvPiCarNo = ((Activity)parentCtx).findViewById(R.id.tvPiCarNo);
        tvPiCarNo.setText(uData.car_no);

        TextView tvPiCarType = ((Activity)parentCtx).findViewById(R.id.tvPiCarType);
        switch(uData.car_type) {
            case "0" :
                tvPiCarType.setText("소형");
                break;
            case "1" :
                tvPiCarType.setText("중형");
                break;
            case "2" :
                tvPiCarType.setText("대형");
                break;
            case "3" :
                tvPiCarType.setText("전기");
                break;
            case "4" :
                tvPiCarType.setText("장애");
                break;
        }
    }

    private int getUserDataWithEmail() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getUser/" + email); /*URL*/

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

        try {
            resultData = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getUserDataThread", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(200 == responseCode) {
            parseUserData();
        }

        return responseCode;
    }

    private void parseUserData() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData.get(i);

                UserData tempUser = new UserData();
                tempUser.name = jsonTemp.getString("name");
                tempUser.email = jsonTemp.getString("email");
                // tempUser.password = jsonTemp.getString("password");
                tempUser.phone_no = jsonTemp.getString("phone_no");
                tempUser.car_no = jsonTemp.getString("car_no");
                tempUser.car_type = jsonTemp.getString("car_type");
                tempUser.level = jsonTemp.getString("level");

                uData = tempUser;
            }
        } catch (Throwable t) {
            Log.e("parseUserData", "Could not parse malformed JSON");
            t.printStackTrace();
        }
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
