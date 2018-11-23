package kr.co.ezinfotech.parkingparking;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class PasswordChangeActivity extends AppCompatActivity {

    Toolbar PCToolbar = null;
    private String whereIFrom = null;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
                                                                    "(?=.*[0-9])" +         //at least 1 digit
                                                                    "(?=.*[a-zA-Z])" +      //any letter
                                                                    "(?=.*[!~*@#\\[\\]$%^&+=])" +    //at least 1 special character
                                                                    "(?=\\S+$)" +           //no white spaces
                                                                    ".{6,}" +               //at least 6 characters
                                                                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        PCToolbar = (Toolbar) findViewById(R.id.pc_toolbar);
        setSupportActionBar(PCToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("비밀번호 변경");

        // Get parcel data
        whereIFrom = getIntent().getStringExtra("whereFrom");
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

    private boolean checkPwInputForm() {

        EditText etNowPwCont = findViewById(R.id.etNowPwCont);
        if(etNowPwCont.getText().toString().trim().equals("") || etNowPwCont.getText().toString().length() < 6) {
            etNowPwCont.setError("기존 비밀번호는 최소 6자리 입니다.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(etNowPwCont.getText().toString()).matches()) {
            etNowPwCont.setError("기존 비밀번호는 문자 + 숫자 + 특수기호로 이루어져 있습니다.");
            return false;
        }

        EditText etPwCont = findViewById(R.id.etPwCont);
        if(etPwCont.getText().toString().trim().equals("") || etPwCont.getText().toString().length() < 6) {
            etPwCont.setError("비밀번호는 최소 6자리 입니다.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(etPwCont.getText().toString()).matches()) {
            etPwCont.setError("비밀번호는 문자 + 숫자 + 특수기호로 이루어져야 합니다.");
            return false;
        }

        EditText etPw2Cont = findViewById(R.id.etPw2Cont);
        if(etPw2Cont.getText().toString().trim().equals("") || !(etPwCont.getText().toString().equals(etPw2Cont.getText().toString()))) {
            etPw2Cont.setError("비밀번호를 확인해주세요.");
            return false;
        }

        return true;
    }

    public void btnChangePassword(View v) {
        if(checkPwInputForm()) {
            // ppRestServer에 사용자 비밀번호 변경 요청
            Handler mHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    if(200 == msg.arg1) {
                        Toast.makeText(getApplicationContext(), "비밀번호 변경 성공", Toast.LENGTH_LONG).show();
                        LoginManager.logout();  // 로그아웃
                        if("MapActivity".equals(whereIFrom)) {
                            // 로그인 폼으로 이동
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                            getApplicationContext().startActivity(intent);
                        } else if ("PrivateInfoActivity".equals(whereIFrom)) {
                        }
                        finish();
                    } else if(201 == msg.arg1) {
                        Toast.makeText(getApplicationContext(), "기존 비밀번호를 정확히 입력해주세요", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("UserDataManagerForLogin", "DB Update 실패");
                    }
                }
            };

            UserDataManagerForLogin udmfl = new UserDataManagerForLogin(mHandler);
            String email = LoginManager.getEmail();
            String nowPassword = ((EditText)findViewById(R.id.etNowPwCont)).getText().toString();
            String password = ((EditText)findViewById(R.id.etPwCont)).getText().toString();
            udmfl.changePassword(email, nowPassword, password);
        }
    }
}
