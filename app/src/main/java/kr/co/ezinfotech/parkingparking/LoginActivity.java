package kr.co.ezinfotech.parkingparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.SIGN_UP.TermsActivity;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 커서 제거 - https://m.blog.naver.com/doryjj/194584359
        EditText et = (EditText)findViewById(R.id.etEmail);
        et.clearFocus();
    }

    public void btnLogin(View v) {
        // 이메일 유효성 검사
        final EditText et = (EditText)findViewById(R.id.etEmail);
        if(UtilManager.isValidEmail(et.getText().toString())) {
        } else {
            showWrongEmail();
            return;
        }

        // 비밀번호 유효성 검사
        EditText et2 = (EditText)findViewById(R.id.etPw);
        if(et2.getText().toString().equals("")) {
            showWrongPw();
            return;
        } else {

        }

        // 서버에 접속하여 이메일, 비밀번호가 존재하는지 체크
        Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // 로그인 성공
                    Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                    LoginManager.setEmail(et.getText().toString()); // 로그인 정보 업데이트
                    LoginManager.login();

                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    getApplicationContext().startActivity(intent);
                    finish();
                } else if(201 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "로그인 실패 - 존재하지 않는 이메일입니다", Toast.LENGTH_SHORT).show();
                    Log.i("UserDataManager", "로그인 실패 - 존재하지 않는 이메일입니다");
                } else if(202 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "로그인 실패 - 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    Log.i("UserDataManager", "로그인 실패 - 비밀번호가 맞지 않습니다");
                }
            }
        };

        UserDataManagerForLogin udmfl = new UserDataManagerForLogin(mHandler);
        udmfl.getEmailNPassword(et.getText().toString(), et2.getText().toString());
    }

    public void btnOutside(View v) {
        // Toast.makeText(getApplicationContext(), "이지파킹 둘러보기 터치!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        getApplicationContext().startActivity(intent);
        // finish();
    }

    public void btnFindPw(View v) {
        // Toast.makeText(getApplicationContext(), "비밀번호 찾기 터치!", Toast.LENGTH_SHORT).show();
        EditText et = (EditText)findViewById(R.id.etEmail);
        if(UtilManager.isValidEmail(et.getText().toString())) {
            showSendEmailDlg(et.getText().toString());
        } else {
            showInputEmailDlg();
        }
    }

    public void btnSignup(View v) {
        Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
        getApplicationContext().startActivity(intent);
    }

    private void showWrongEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이메일 입력창에 올바른 주소를 입력해주시기 바랍니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void showWrongPw() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("비밀번호 입력창에 올바른 비밀번호를 입력해주시기 바랍니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    // http://webnautes.tistory.com/1094
    private void showInputEmailDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이메일 입력창에 비밀번호를 찾으실 계정의 이메일을 입력해주시기 바랍니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void showSendEmailDlg(final String emailVal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("입력하신 이메일 '" + emailVal + "'로 가입된 계정의 비밀번호를 전송합니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 서버에 접속하여 임시비밀번호 발급
                        Handler mHandler = new Handler() {
                            @Override public void handleMessage(Message msg) {
                                if(200 == msg.arg1) {   // 임시비밀번호 발급 성공
                                    Toast.makeText(getApplicationContext(),"임시비밀번호를 메일로 전송했습니다.",Toast.LENGTH_LONG).show();
                                } else if(201 == msg.arg1) {
                                    Toast.makeText(getApplicationContext(), "임시비밀번호 발급 실패 - 존재하지 않는 이메일입니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };

                        UserDataManagerForLogin udmfl = new UserDataManagerForLogin(mHandler);
                        udmfl.findPassword(emailVal);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
}
