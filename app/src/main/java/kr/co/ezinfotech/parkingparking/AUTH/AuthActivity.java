package kr.co.ezinfotech.parkingparking.AUTH;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.ezinfotech.parkingparking.R;

public class AuthActivity extends AppCompatActivity {

    Toolbar authToolbar = null;
    static long after3min = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authToolbar = (Toolbar) findViewById(R.id.auth_toolbar);
        setSupportActionBar(authToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("본인인증");
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

    public void sendAuthCode(View v) {
        EditText etPhoneNo = (EditText)findViewById(R.id.etPhoneNo);
        // https://stackoverflow.com/questions/18225365/show-error-on-the-tip-of-the-edit-text-android
        if (etPhoneNo.getText().toString().trim().equalsIgnoreCase("") || (11 != etPhoneNo.getText().toString().length())) {
            etPhoneNo.setError("휴대폰번호를 입력하세요.");
            return;
        }

        Button btnSendAuthCode = (Button)findViewById(R.id.btnSendAuthCode);
        btnSendAuthCode.setText("입력대기");
        btnSendAuthCode.setEnabled(false);

        // 남은시간
        // http://devfarming.tistory.com/3
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                setBtnSendAuthCode();
            }
        };

        final Handler handler2 = new Handler(){
            public void handleMessage(Message msg){
                setBtnAuthOk();
            }
        };

        final EditText etAuthWaitTime = (EditText)findViewById(R.id.etAuthWaitTime);

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        Calendar calAfter3Min = Calendar.getInstance();
        calAfter3Min.setTime(date);
        calAfter3Min.add(Calendar.MINUTE, 1);
        after3min = calAfter3Min.getTimeInMillis();

        // http://blog.naver.com/PostView.nhn?blogId=ssarang8649&logNo=220947884163
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                // 다음버튼 관련 설정
                Message msg2 = handler2.obtainMessage();
                handler2.sendMessage(msg2);

                // 남은시간 = 현재시간 - after3min
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

                Calendar calNow = Calendar.getInstance();
                calNow.setTime(date);
                long nowTime = calNow.getTimeInMillis();
                long diff = after3min - nowTime;
                String strDiff = sdf.format(diff);
                etAuthWaitTime.setText(strDiff);    // UI에 뿌리기
                // Log.e("남은시간:", strDiff);

                if(strDiff.equals("00:00")) {
                    // http://devfarming.tistory.com/3
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                    this.cancel();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 0, 1000);
    }

    private void setBtnSendAuthCode() {
        Button btnSendAuthCode = (Button)findViewById(R.id.btnSendAuthCode);
        btnSendAuthCode.setText("재발송");
        btnSendAuthCode.setEnabled(true);
    }

    private void setBtnAuthOk() {
        Button btnAuthOk = findViewById(R.id.btnAuthOk);
        EditText etAuthNo = findViewById(R.id.etAuthNo);
        if(4 > etAuthNo.getText().toString().length()) {
            btnAuthOk.setEnabled(false);
        } else {
            btnAuthOk.setEnabled(true);
        }
    }

    public void authOk(View v) {

    }
}
