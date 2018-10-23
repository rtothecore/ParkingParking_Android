package kr.co.ezinfotech.parkingparking.SIGN_UP;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.LoginActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class SignUpActivity extends AppCompatActivity {

    Toolbar signupToolbar = null;
    String phoneNo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupToolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(signupToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("회원가입");

        // get parcel data
        phoneNo = getIntent().getStringExtra("phoneNo");

        setEditTextChange();
    }

    private boolean checkAllInputForm2() {
        EditText etSignupName = findViewById(R.id.etSignupName);
        if(etSignupName.getText().toString().trim().equals("") || etSignupName.getText().toString().length() < 2) {
            etSignupName.setError("이름을 입력해주세요.");
            return false;
        }

        EditText etSignupEmail = findViewById(R.id.etSignupEmail);
        if(etSignupEmail.getText().toString().trim().equals("") || !(UtilManager.isValidEmail(etSignupEmail.getText().toString()))) {
            etSignupEmail.setError("이메일 주소를 입력해주세요.");
            return false;
        }

        EditText etSignupPw = findViewById(R.id.etSignupPw);
        if(etSignupPw.getText().toString().trim().equals("") || etSignupPw.getText().toString().length() < 6) {
            etSignupPw.setError("비밀번호를 입력해주세요.");
            return false;
        }

        EditText etSignupPw2 = findViewById(R.id.etSignupPw2);
        if(etSignupPw2.getText().toString().trim().equals("") || !(etSignupPw2.getText().toString().equals(etSignupPw.getText().toString()))) {
            etSignupPw2.setError("비밀번호를 확인해주세요.");
            return false;
        }

        return true;
    }

    private void checkAllInputForm() {
        EditText etSignupName = findViewById(R.id.etSignupName);
        if(etSignupName.getText().toString().trim().equals("")) {
            return;
        }

        EditText etSignupEmail = findViewById(R.id.etSignupEmail);
        if(etSignupEmail.getText().toString().trim().equals("")) {
            return;
        }

        EditText etSignupPw = findViewById(R.id.etSignupPw);
        if(etSignupPw.getText().toString().trim().equals("")) {
            return;
        }

        EditText etSignupPw2 = findViewById(R.id.etSignupPw2);
        if(etSignupPw2.getText().toString().trim().equals("")) {
            return;
        }

        Button btnSignupComplete = (Button)findViewById(R.id.btnSignupComplete);
        btnSignupComplete.setEnabled(true);
    }

    // http://ccdev.tistory.com/15
    private void setEditTextChange() {
        // 이름
        EditText etSignupName = findViewById(R.id.etSignupName);
        etSignupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllInputForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 이메일
        EditText etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllInputForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 비밀번호
        EditText etSignupPw = findViewById(R.id.etSignupPw);
        etSignupPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllInputForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 비밀번호 확인
        EditText etSignupPw2 = findViewById(R.id.etSignupPw2);
        etSignupPw2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllInputForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

    public void btnSignUpOk(View v) {
        if(checkAllInputForm2()) {
            // ppRestServer에 가입정보 insert
            Handler mHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    if(666 == msg.arg1) {
                        Log.i("UserDataManager", "DB insert 성공");
                        Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();

                        // 로그인 폼으로 이동
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        getApplicationContext().startActivity(intent);
                    } else {
                        Log.i("UserDataManager", "DB insert 실패");
                    }
                }
            };

            UserDataManager udm = new UserDataManager(mHandler);
            String name = ((EditText)findViewById(R.id.etSignupName)).getText().toString();
            String email = ((EditText)findViewById(R.id.etSignupEmail)).getText().toString();
            String password = ((EditText)findViewById(R.id.etSignupPw)).getText().toString();
            udm.setUserData(name, email, password, phoneNo);
        }
    }
}
