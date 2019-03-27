package kr.co.ezinfotech.parkingparking.PRIVATE_INFO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class CarNoChangeActivity extends AppCompatActivity {

    // http://itbrain.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%8B%A4%EB%A5%B8-%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0%EC%9D%98-%ED%95%A8%EC%88%98-%ED%98%B8%EC%B6%9C
    public static Context mContext;
    Toolbar CNCToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_no_change);

        mContext = this;

        CNCToolbar = (Toolbar) findViewById(R.id.cnc_toolbar);
        setSupportActionBar(CNCToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("차량번호 변경");
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

    public void onClickChangeCarNo(View v) {
        EditText etNewCarNoCont = findViewById(R.id.etNewCarNoCont);
        String carNo = etNewCarNoCont.getText().toString();

        UserDataManager udm = new UserDataManager(null);
        udm.setUserDataWithCarNo(this, LoginManager.getEmail(), carNo);
    }

    public void callParentSetCarNo(String carNoVal) {
        // ((PrivateInfoActivity)PrivateInfoActivity.mContext).setCarNo(carNoVal);
    }

}
