package kr.co.ezinfotech.parkingparking.PRIVATE_INFO;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManager;
import kr.co.ezinfotech.parkingparking.LoginActivity;
import kr.co.ezinfotech.parkingparking.MapActivity;
import kr.co.ezinfotech.parkingparking.PasswordChangeActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class PrivateInfoActivity extends AppCompatActivity implements View.OnClickListener{

    // http://itbrain.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%8B%A4%EB%A5%B8-%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0%EC%9D%98-%ED%95%A8%EC%88%98-%ED%98%B8%EC%B6%9C
    public static Context mContext;
    Toolbar PIToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info);

        mContext = this;

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
                // Toast.makeText(getApplicationContext(), "이전 버튼 터치됨", Toast.LENGTH_LONG).show();
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

        UserDataManager udm = new UserDataManager(null);
        udm.getUserData(this, LoginManager.getEmail());
    }

    public void onClickPiLogout(View v) {
        /*
        LoginManager.logout();
        Toast.makeText(getApplicationContext(), "로그아웃 했습니다.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        getApplicationContext().startActivity(intent);
        finish();
        */
        AlertDialog.Builder dialog = new AlertDialog.Builder(PrivateInfoActivity.this);
        dialog.setTitle("로그아웃 알림")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃 합니다.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.logout();
                        Toast.makeText(getApplicationContext(), "로그아웃 했습니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                        getApplicationContext().startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PrivateInfoActivity.this, "로그아웃 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }

    public void onClickPiChangePw(View v) {
        Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        intent.putExtra("whereFrom", "PrivateInfoActivity");
        getApplicationContext().startActivity(intent);
    }

    public void onClickPiChangePhoneNo(View v) {
        /*
        Intent intent = new Intent(getApplicationContext(), PhoneNoChangeActivity.class);
        getApplicationContext().startActivity(intent);
        */
        Intent intent = new Intent(this, PhoneNoChangeActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode == RESULT_OK) {
                String newPhoneNo = data.getStringExtra("newPhoneNo");
                TextView tvPiPhone = findViewById(R.id.tvPiPhone);
                tvPiPhone.setText(newPhoneNo);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

/*
    public void onClickPiChangeCarNo(View v) {
        Intent intent = new Intent(getApplicationContext(), CarNoChangeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        getApplicationContext().startActivity(intent);
    }

    public void setCarNo(String carNoVal) {
        TextView tvPiCarNo = findViewById(R.id.tvPiCarNo);
        tvPiCarNo.setText(carNoVal);
    }

    public void onClickPiChangeCarType(View v) {
        Intent intent = new Intent(getApplicationContext(), CarTypeChangeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        getApplicationContext().startActivity(intent);
    }

    public void setCarType(String carTypeVal) {
        TextView tvPiCarType = findViewById(R.id.tvPiCarType);
        switch(carTypeVal) {
            case "0" :
                tvPiCarType.setText("소형");
                break;
            case "1" :
                tvPiCarType.setText("중형");
                break;
            case "2" :
                tvPiCarType.setText("대형");
                break;
            case "3" :
                tvPiCarType.setText("전기");
                break;
            case "4" :
                tvPiCarType.setText("장애");
                break;
            default :
                break;
        }
    }
*/
}
