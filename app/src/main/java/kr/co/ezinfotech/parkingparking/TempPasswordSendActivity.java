package kr.co.ezinfotech.parkingparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class TempPasswordSendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_password_send);
    }

    public void btnSendTempPassword(View v) {
        showSendEmailDlg(LoginManager.getEmail());
    }

    private void showSendEmailDlg(final String emailVal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("가입하신 이메일 '" + emailVal + "'로 비밀번호를 전송합니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 서버에 접속하여 임시비밀번호 발급
                        Handler mHandler = new Handler() {
                            @Override public void handleMessage(Message msg) {
                                if(200 == msg.arg1) {   // 임시비밀번호 발급 성공
                                    Toast.makeText(getApplicationContext(),"임시비밀번호를 메일로 전송했습니다.",Toast.LENGTH_LONG).show();
                                    LoginManager.logout();  // 로그아웃
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                                    getApplicationContext().startActivity(intent);
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
