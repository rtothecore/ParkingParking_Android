package kr.co.ezinfotech.parkingparking.PREFERENCES;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.LoginActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class UserLeaveActivity extends AppCompatActivity {

    Toolbar ULToolbar = null;

    RadioButton option1 = null;
    RadioButton option2 = null;
    RadioButton option3 = null;
    RadioButton option4 = null;
    RadioButton option5 = null;

    int leave_reason = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_leave);

        ULToolbar = (Toolbar) findViewById(R.id.ul_toolbar);
        setSupportActionBar(ULToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("회원탈퇴");

        // Initialize radio buttons - http://mainia.tistory.com/1159
        option1 = (RadioButton) findViewById(R.id.option1);
        option2 = (RadioButton) findViewById(R.id.option2);
        option3 = (RadioButton) findViewById(R.id.option3);
        option4 = (RadioButton) findViewById(R.id.option4);
        option5 = (RadioButton) findViewById(R.id.option5);
        option1.setOnClickListener(optionOnClickListener);
        option2.setOnClickListener(optionOnClickListener);
        option3.setOnClickListener(optionOnClickListener);
        option4.setOnClickListener(optionOnClickListener);
        option5.setOnClickListener(optionOnClickListener);
        option1.setChecked(true);
    }

    RadioButton.OnClickListener optionOnClickListener = new RadioButton.OnClickListener() {
        public void onClick(View v) {
            if(option1.isChecked()) {
                leave_reason = 0;
            } else if(option2.isChecked()) {
                leave_reason = 1;
            } else if(option3.isChecked()) {
                leave_reason = 2;
            } else if(option4.isChecked()) {
                leave_reason = 3;
            } else if(option5.isChecked()) {
                leave_reason = 4;
            }
        }
    };

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

    public void onClickUserLeave(View v) {
        showLeaveUserDlg();
    }

    private void showLeaveUserDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이지파킹을 탈퇴하시겠습니까? 탈퇴시 모든 주차정보가 삭제되며 복원할 수 없습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        runLeaveUser();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"취소를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private void runLeaveUser() {
        UserDataManager udm = new UserDataManager(null);
        udm.leaveUser(this, LoginManager.getEmail(), Integer.toString(leave_reason));
    }
}
