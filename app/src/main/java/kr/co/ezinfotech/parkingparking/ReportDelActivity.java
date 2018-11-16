package kr.co.ezinfotech.parkingparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Map;

import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class ReportDelActivity extends AppCompatActivity {

    Toolbar reportDelToolbar = null;

    String code = null;
    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_del);

        reportDelToolbar = (Toolbar) findViewById(R.id.report_del_toolbar);
        setSupportActionBar(reportDelToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("제보삭제");

        // Get parcel data
        code = getIntent().getStringExtra("code");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkInputForm() {
        EditText etReportDelReason = findViewById(R.id.etReportDelReason);
        if(etReportDelReason.getText().toString().trim().equals("")) {
            etReportDelReason.setError("삭제사유를 입력해주세요.");
            return false;
        }

        return true;
    }

    public void onClickBtnReportDelReq(View v) {
        if(checkInputForm()) {
            updateReportDelStatus();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    private void updateReportDelStatus() {
        mHandler = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    // Toast.makeText(getApplicationContext(), "Insert 성공!", Toast.LENGTH_SHORT).show();
                    Log.i("updateReportDelStatus", "Update 성공");
                    showUpdateSuccessDlg();
                } else {
                    // Toast.makeText(getApplicationContext(), "Insert 실패", Toast.LENGTH_SHORT).show();
                    Log.i("updateReportDelStatus", "Update 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callUpdateREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callUpdateREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/updateReportWithDelReason"); /*URL*/

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
            EditText etReportDelReason = findViewById(R.id.etReportDelReason);
            String deleteReason = etReportDelReason.getText().toString();

            jsonObject.accumulate("code", code);
            jsonObject.accumulate("delete_reason", deleteReason);
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
            Log.e("callUpdateREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("callUpdateREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    // http://webnautes.tistory.com/1094
    private void showUpdateSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("제보정보를 삭제했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        // finish();
                        returnToMapActivity();
                    }
                });
        builder.show();
    }

    private void returnToMapActivity() {
        // https://stackoverflow.com/questions/37248300/how-to-finish-specific-activities-not-all-activities
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
