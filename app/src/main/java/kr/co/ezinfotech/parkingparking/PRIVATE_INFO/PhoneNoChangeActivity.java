package kr.co.ezinfotech.parkingparking.PRIVATE_INFO;

import android.content.Intent;
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

import kr.co.ezinfotech.parkingparking.DATA.SmsAuthDataManager;
import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.SIGN_UP.AuthActivity;
import kr.co.ezinfotech.parkingparking.SIGN_UP.SignUpActivity;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class PhoneNoChangeActivity extends AppCompatActivity {

    Toolbar PNCToolbar = null;
    static long after3min = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_change);

        PNCToolbar = (Toolbar) findViewById(R.id.pnc_toolbar);
        setSupportActionBar(PNCToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("휴대폰번호 변경");
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
        EditText etPhoneNo = (EditText)findViewById(R.id.etPncPhoneNo);
        // https://stackoverflow.com/questions/18225365/show-error-on-the-tip-of-the-edit-text-android
        if (etPhoneNo.getText().toString().trim().equalsIgnoreCase("") || (11 != etPhoneNo.getText().toString().length())) {
            etPhoneNo.setError("휴대폰번호를 입력하세요.");
            return;
        }

        // ppRestServer의 smsauths콜렉션에 인증코드를 생성하고 해당 휴대폰번호로 보냄
        SmsAuthDataManager sadm = new SmsAuthDataManager(null, this);
        sadm.createSmsAuthCode(etPhoneNo.getText().toString());

        Button btnSendAuthCode = (Button)findViewById(R.id.btnPncSendAuthCode);
        btnSendAuthCode.setText("입력대기");
        btnSendAuthCode.setEnabled(false);

        // 남은시간
        // http://devfarming.tistory.com/3
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                setBtnSendAuthCode();
            }
        };

        /*
        final Handler handler2 = new Handler(){
            public void handleMessage(Message msg){
                setBtnAuthOk();
            }
        };
        */

        final EditText etAuthWaitTime = (EditText)findViewById(R.id.etPncAuthWaitTime);

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        Calendar calAfter3Min = Calendar.getInstance();
        calAfter3Min.setTime(date);
        calAfter3Min.add(Calendar.MINUTE, 3);
        after3min = calAfter3Min.getTimeInMillis();

        //
        final Handler handler3 = new Handler(){
            public void handleMessage(Message msg){
                etAuthWaitTime.setText(msg.getData().getString("strDiff"));    // UI에 뿌리기
            }
        };
        //

        // http://blog.naver.com/PostView.nhn?blogId=ssarang8649&logNo=220947884163
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                // 다음버튼 관련 설정
                /*
                Message msg2 = handler2.obtainMessage();
                handler2.sendMessage(msg2);
                */

                // 남은시간 = 현재시간 - after3min
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

                Calendar calNow = Calendar.getInstance();
                calNow.setTime(date);
                long nowTime = calNow.getTimeInMillis();
                long diff = after3min - nowTime;
                String strDiff = sdf.format(diff);

                // etAuthWaitTime.setText(strDiff);    // UI에 뿌리기
                //
                Bundle data = new Bundle();
                Message msg3 = new Message();
                data.putString("strDiff", strDiff);
                msg3.setData(data);
                handler3.sendMessage(msg3);
                //

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

    // 인증 시간이 다 되었을때
    private void setBtnSendAuthCode() {
        // https://blog.sangyoung.me/2016/12/28/BadTokenException/
        if(!PhoneNoChangeActivity.this.isFinishing()) {
            // ppRestServer의 smsauths콜렉션의 해당 휴대폰번호로 입력된 인증코드를 삭제
            SmsAuthDataManager sadm = new SmsAuthDataManager(null, this);
            sadm.deleteSmsAuthCode(((EditText)findViewById(R.id.etPncPhoneNo)).getText().toString());

            Button btnSendAuthCode = (Button)findViewById(R.id.btnPncSendAuthCode);
            btnSendAuthCode.setText("재발송");
            btnSendAuthCode.setEnabled(true);
        }
    }

    // "다음" 버튼 설정
    private void setBtnAuthOk() {
        Button btnAuthOk = findViewById(R.id.btnPncAuthOk);
        EditText etAuthNo = findViewById(R.id.etPncAuthNo);
        if(4 > etAuthNo.getText().toString().length()) {
            btnAuthOk.setEnabled(false);
        } else {
            btnAuthOk.setEnabled(true);
        }
    }

    public void authOk(View v) {
        final EditText etPhoneNo = findViewById(R.id.etPncPhoneNo);
        final EditText etAuthNo = findViewById(R.id.etPncAuthNo);
        final EditText etAuthWaitTime = findViewById(R.id.etPncAuthWaitTime);

        // 1. 휴대폰번호를 입력했는지 체크
        if(11 > etPhoneNo.getText().toString().length()) {
            etPhoneNo.requestFocus();
            etPhoneNo.setError("휴대폰번호를 입력해주세요.");
            return;
        }

        // 2. 인증번호를 입력했는지 체크
        if(4 > etAuthNo.getText().toString().length()) {
            etAuthNo.requestFocus();
            etAuthNo.setError("인증번호를 입력해주세요.");
            return;
        }

        // 3. 입력시간이 다 되었는지 체크
        if("00:00".equals(etAuthWaitTime.getText().toString())) {
            etAuthNo.requestFocus();
            etAuthNo.setError("재발송 버튼을 눌러주세요.");
            return;
        }

        // 4. 인증번호가 올바른지 체크
        Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(888 == msg.arg1) {
                    Log.i("SmsAuthDataManager", "인증코드 맞음");

                    // 2. 개인정보 창으로 리턴
                    Intent intent = new Intent();
                    intent.putExtra("newPhoneNo", etPhoneNo.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.i("SmsAuthDataManager", "인증코드 다름");
                    etAuthNo.setError("인증번호가 올바르지 않습니다.");
                }
            }
        };

        SmsAuthDataManager sadm = new SmsAuthDataManager(mHandler, this);
        sadm.getMySmsAuthCode(etPhoneNo.getText().toString(), etAuthNo.getText().toString());

        UserDataManager udm = new UserDataManager(null);
        udm.updatePhoneNo(this, LoginManager.getEmail(), etPhoneNo.getText().toString());
    }
}
