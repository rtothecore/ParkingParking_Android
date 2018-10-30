package kr.co.ezinfotech.parkingparking.PRIVATE_INFO;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.LoginActivity;
import kr.co.ezinfotech.parkingparking.PasswordChangeActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class PrivateInfoActivity extends AppCompatActivity {

    Toolbar PIToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info);

        PIToolbar = (Toolbar) findViewById(R.id.pi_toolbar);
        setSupportActionBar(PIToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("개인정보");

        // Set data
        setData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "이전 버튼 터치됨", Toast.LENGTH_LONG).show();
                super.onBackPressed();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public void setData() {
        TextView tvPiPhone = findViewById(R.id.tvPiPhone);
        tvPiPhone.setText(LoginManager.getPhone());

        TextView tvPiEmail = findViewById(R.id.tvPiEmail);
        tvPiEmail.setText(LoginManager.getEmail());
    }

    public void onClickPiLogout(View v) {
        LoginManager.logout();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        getApplicationContext().startActivity(intent);
        finish();
    }

    public void onClickPiChangePw(View v) {
        Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
        intent.putExtra("whereFrom", "PrivateInfoActivity");
        getApplicationContext().startActivity(intent);
    }
}
