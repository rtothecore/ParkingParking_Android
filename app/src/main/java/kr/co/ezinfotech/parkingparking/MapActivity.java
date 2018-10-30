package kr.co.ezinfotech.parkingparking;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.MAP.DaumMapManager;
import kr.co.ezinfotech.parkingparking.PRIVATE_INFO.PrivateInfoActivity;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 툴바
    Toolbar myToolbar = null;
    // 다음맵
    DaumMapManager dmm = null;
    // 라디오 버튼
    RadioButton RB_publicService = null;
    RadioButton RB_private = null;
    RadioButton RB_onRoad = null;
    RadioButton RB_outside = null;
    RadioButton RB_weekday = null;
    RadioButton RB_weekdayNSaturday = null;
    RadioButton RB_weekdayNSaturdayNHoliday = null;
    RadioButton RB_holiday = null;
    RadioButton RB_freeFee = null;
    RadioButton RB_payFee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Toolbar를 생성한다.
        // https://blog.hanumoka.net/2017/10/28/android-20171028-android-toolbar/
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        // https://stackoverflow.com/questions/35810229/how-to-display-and-set-click-event-on-back-arrow-on-toolbar
        // https://developer.android.com/training/implementing-navigation/ancestral?hl=es
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_menu_24);
        getSupportActionBar().setTitle("주차왕파킹");

        // Drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Bottom navigation
        InitializeBottomNav();

        // Daum Map API
        dmm = new DaumMapManager(this);
        dmm.runMapProcess();

        // Initialize radio buttons of search filter
        InitializeRadioButtons();

        // Check passord expired
        if(LoginManager.isLogin()) {
            CheckPasswordExpired();
        }

        // set login info
        SetLoginInfo();
    }

    // ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    // ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "메뉴 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "검색 버튼 클릭됨", Toast.LENGTH_LONG).show();
                // https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_filter:
                Toast.makeText(getApplicationContext(), "필터 버튼 클릭됨", Toast.LENGTH_LONG).show();
                LinearLayout ll = (LinearLayout) findViewById(R.id.parkingTopLL);
                if(View.INVISIBLE == ll.getVisibility()) {
                    ll.setVisibility(View.VISIBLE);
                } else {
                    ll.setVisibility(View.INVISIBLE);
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    // https://medium.com/@hitherejoe/exploring-the-android-design-support-library-bottom-navigation-drawer-548de699e8e0
    private void InitializeBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_one:
                            Toast.makeText(getApplicationContext(), "공유 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.bottom_nav_two:
                            Toast.makeText(getApplicationContext(), "제보 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.bottom_nav_three:
                            Toast.makeText(getApplicationContext(), "리스트 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MapActivity.this, ListActivity.class);
                            intent.putExtra("centerPointLat", dmm.centerPoint.getLatitude());
                            intent.putExtra("centerPointLng", dmm.centerPoint.getLongitude());
                            intent.putParcelableArrayListExtra("pzData", dmm.pzData);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String lat = data.getStringExtra("lat");
                String lng = data.getStringExtra("lng");
                Toast.makeText(getApplicationContext(), "lat:" + lat + ", lng:" + lng, Toast.LENGTH_LONG).show();
                dmm.setMapCenter(Double.valueOf(lat), Double.valueOf(lng));
            }
        }
    }

    private void InitializeRadioButtons() {
        RB_publicService = (RadioButton) findViewById(R.id.RB_publicService);
        RB_private = (RadioButton) findViewById(R.id.RB_private);
        RB_onRoad = (RadioButton) findViewById(R.id.RB_onRoad);
        RB_outside = (RadioButton) findViewById(R.id.RB_outside);
        RB_weekday = (RadioButton) findViewById(R.id.RB_weekday);
        RB_weekdayNSaturday = (RadioButton) findViewById(R.id.RB_weekdayNSaturday);
        RB_weekdayNSaturdayNHoliday = (RadioButton) findViewById(R.id.RB_weekdayNSaturDayNHoliday);
        RB_holiday = (RadioButton) findViewById(R.id.RB_holiday);
        RB_freeFee = (RadioButton) findViewById(R.id.RB_freeFee);
        RB_payFee = (RadioButton) findViewById(R.id.RB_payFee);

        RB_publicService.setOnClickListener(ppOnClickListener);
        RB_private.setOnClickListener(ppOnClickListener);
        RB_onRoad.setOnClickListener(roOnClickListener);
        RB_outside.setOnClickListener(roOnClickListener);
        RB_weekday.setOnClickListener(opOnClickListener);
        RB_weekdayNSaturday.setOnClickListener(opOnClickListener);
        RB_weekdayNSaturdayNHoliday.setOnClickListener(opOnClickListener);
        RB_holiday.setOnClickListener(opOnClickListener);
        RB_freeFee.setOnClickListener(feeOnClickListener);
        RB_payFee.setOnClickListener(feeOnClickListener);

        // RB_publicService.setChecked(true);
    }

    RadioButton.OnClickListener ppOnClickListener = new RadioButton.OnClickListener() {
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "민영/공영 라디오버튼 클릭됨", Toast.LENGTH_LONG).show();
            refreshMap();
        }
    };

    RadioButton.OnClickListener roOnClickListener = new RadioButton.OnClickListener() {
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "노상/노외 라디오버튼 클릭됨", Toast.LENGTH_LONG).show();
            refreshMap();
        }
    };

    RadioButton.OnClickListener opOnClickListener = new RadioButton.OnClickListener() {
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "운영요일 라디오버튼 클릭됨", Toast.LENGTH_LONG).show();
            refreshMap();
        }
    };

    RadioButton.OnClickListener feeOnClickListener = new RadioButton.OnClickListener() {
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "요금 라디오버튼 클릭됨", Toast.LENGTH_LONG).show();
            refreshMap();
        }
    };

    private void refreshMap() {
        int division = 0;
        int type = 0;
        int op = 0;
        int fee = 0;

        if(RB_publicService.isChecked()) {
            division = 1;
        } else if(RB_private.isChecked()) {
            division = 2;
        }

        if(RB_onRoad.isChecked()) {
            type = 1;
        } else if(RB_outside.isChecked()) {
            type = 2;
        }

        if(RB_weekday.isChecked()) {
            op = 1;
        } else if(RB_weekdayNSaturday.isChecked()) {
            op = 2;
        } else if(RB_weekdayNSaturdayNHoliday.isChecked()) {
            op = 3;
        } else if(RB_holiday.isChecked()) {
            op = 4;
        }

        if(RB_payFee.isChecked()) {
            fee = 1;
        } else if(RB_freeFee.isChecked()) {
            fee = 2;
        }

        dmm.runMapProcessWithParam(division, type, op, fee);
    }

    public void btnInitAllRadio(View v) {
        RB_publicService.setChecked(false);
        RB_private.setChecked(false);
        RB_onRoad.setChecked(false);
        RB_outside.setChecked(false);
        RB_weekday.setChecked(false);
        RB_weekdayNSaturday.setChecked(false);
        RB_weekdayNSaturdayNHoliday.setChecked(false);
        RB_holiday.setChecked(false);
        RB_payFee.setChecked(false);
        RB_freeFee.setChecked(false);

        refreshMap();
    }

    private void CheckPasswordExpired() {
        // 서버에 접속하여 임시비밀번호 및 비밀번호 만료 체크
        Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // 만료된 것 없음
                } else if(201 == msg.arg1) {    // 비밀번호 만료 -> 비번 변경 창으로 이동
                    Toast.makeText(getApplicationContext(), "비밀번호가 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
                    intent.putExtra("whereFrom", "MapActivity");
                    getApplicationContext().startActivity(intent);
                    finish();
                    return;
                } else if(202 == msg.arg1) {    // 임시 비밀번호 만료 -> 임시비번 재발급 창으로 이동
                    Toast.makeText(getApplicationContext(), "임시 비밀번호가 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TempPasswordSendActivity.class);
                    getApplicationContext().startActivity(intent);
                    return;
                } else if(203 == msg.arg1) {    // 임시 비밀번호 만료 전 -> 비번 변경 창으로 이동
                    Toast.makeText(getApplicationContext(), "임시 비밀번호를 변경해야합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
                    intent.putExtra("whereFrom", "MapActivity");
                    getApplicationContext().startActivity(intent);
                    return;
                }
            }
        };

        UserDataManagerForLogin udmfl = new UserDataManagerForLogin(mHandler);
        udmfl.checkPasswordExpired(LoginManager.getEmail());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_my_reservation) {

        } else if (id == R.id.drawer_coupons) {

        } else if (id == R.id.drawer_points) {

        } else if (id == R.id.drawer_private_info) {
            Intent intent = new Intent(getApplicationContext(), PrivateInfoActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.drawer_alliance) {

        } else if (id == R.id.drawer_service_center) {

        } else if (id == R.id.drawer_preferences) {

        } else if (id == R.id.drawer_favorites) {

        } else if (id == R.id.drawer_share_status) {

        } else if (id == R.id.drawer_report_status) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SetLoginInfo() {
        // https://stackoverflow.com/questions/33194594/navigationview-get-find-header-layout
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView tvNavHeaderTitle = header.findViewById(R.id.tvNavHeaderTitle);
        tvNavHeaderTitle.setText(LoginManager.getName());

        TextView tvNavHeaderSub = header.findViewById(R.id.tvNavHeaderSub);
        tvNavHeaderSub.setText(LoginManager.getEmail());
    }

    public void onClickLogout(View v) {
        // Toast.makeText(getApplicationContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
        LoginManager.logout();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        getApplicationContext().startActivity(intent);
        finish();
    }

    public void onClickNavHeader(View v) {
        // Toast.makeText(getApplicationContext(), "개인정보", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), PrivateInfoActivity.class);
        getApplicationContext().startActivity(intent);
    }
}
