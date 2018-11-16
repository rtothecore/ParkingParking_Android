package kr.co.ezinfotech.parkingparking.PREFERENCES;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.SIGN_UP.TermsDetailActivity;

public class PreferencesActivity extends AppCompatActivity {

    Toolbar PreToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        PreToolbar = (Toolbar) findViewById(R.id.pre_toolbar);
        setSupportActionBar(PreToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("환경설정");
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

    public void onClickPreVersion(View v) {
        Toast.makeText(getApplicationContext(), "최신버전 입니다", Toast.LENGTH_LONG).show();
    }

    public void onClickPreTOS(View v) {
        Intent intent = new Intent(getApplicationContext(), TermsDetailActivity.class);
        intent.putExtra("tabIndex", 0);
        getApplicationContext().startActivity(intent);
    }

    public void onClickPreLeaveUser(View v) {
        Intent intent = new Intent(getApplicationContext(), UserLeaveActivity.class);
        getApplicationContext().startActivity(intent);
    }
}
