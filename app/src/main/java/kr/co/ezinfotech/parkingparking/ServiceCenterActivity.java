package kr.co.ezinfotech.parkingparking;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ServiceCenterActivity extends AppCompatActivity {

    Toolbar SCToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);

        SCToolbar = (Toolbar) findViewById(R.id.sc_toolbar);
        setSupportActionBar(SCToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("고객센터");
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

    public void onClickNotice(View v) {
        Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        getApplicationContext().startActivity(intent);
    }

    public void onClickSendEmail(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hkim@ezinfotech.co.kr"});

            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            if(emailIntent.resolveActivity(getPackageManager()) != null)
                startActivity(emailIntent);

            startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();

            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hkim@ezinfotech.co.kr"});

            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        }
    }

    public void onClickAskWithKakaoTalk(View v) {
        // https://namcreative.tistory.com/258
        // http://gun0912.tistory.com/13
        String url ="https://pf.kakao.com/_ArmGj";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        startActivity(intent);
    }

    public void onClickAskWithPhone(View v) {
        String tel = "tel:0647536677";
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
    }
}
