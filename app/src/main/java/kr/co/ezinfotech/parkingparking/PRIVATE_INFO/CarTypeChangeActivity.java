package kr.co.ezinfotech.parkingparking.PRIVATE_INFO;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class CarTypeChangeActivity extends AppCompatActivity {

    // http://itbrain.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%8B%A4%EB%A5%B8-%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0%EC%9D%98-%ED%95%A8%EC%88%98-%ED%98%B8%EC%B6%9C
    public static Context mContext;
    Toolbar CTCToolbar = null;
    String selectedCarType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_type_change);

        mContext = this;

        CTCToolbar = (Toolbar) findViewById(R.id.ctc_toolbar);
        setSupportActionBar(CTCToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("차량종류 변경");

        // http://milkye.tistory.com/4
        Spinner Main_spinner = (Spinner)findViewById(R.id.spinner);

        // 스피너 어댑터 설정
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.car_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Main_spinner.setAdapter(adapter);

        // 스피너 이벤트 발생
        Main_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 각 항목 클릭시 포지션값을 토스트에 띄운다.
                // Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                selectedCarType = Integer.toString(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    public void onClickChangeCarType(View v) {
        UserDataManager udm = new UserDataManager(null);
        udm.setUserDataWithCarType(this, LoginManager.getEmail(), selectedCarType);
    }

    public void callParentSetCarType(String carTypeVal) {
        // ((PrivateInfoActivity)PrivateInfoActivity.mContext).setCarType(carTypeVal);
    }
}
