package kr.co.ezinfotech.parkingparking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.FavoritesDataManager;
import kr.co.ezinfotech.parkingparking.DATA.UserDataManagerForLogin;
import kr.co.ezinfotech.parkingparking.MAP.DaumMapManager;
import kr.co.ezinfotech.parkingparking.PREFERENCES.PreferencesActivity;
import kr.co.ezinfotech.parkingparking.PRIVATE_INFO.PrivateInfoActivity;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // 툴바
    Toolbar myToolbar = null;
    // 다음맵
    DaumMapManager dmm = null;

    // FAB
    private FloatingActionButton fab1, fab2, fab3, fab4;

    // 액티비티 요청코드
    public static final int REQUEST_CODE_SEARCH = 101;
    public static final int REQUEST_CODE_LIST = 102;

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
        getSupportActionBar().setTitle("이지파킹");

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

        // FAB - https://medium.com/wasd/android-floating-action-button-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-6ca52aba7a1f
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);

        /*
        fab1.setImageBitmap(textAsBitmap("무료", 50, Color.BLACK));
        fab2.setImageBitmap(textAsBitmap("유료", 50, Color.BLACK));
        fab3.setImageBitmap(textAsBitmap("전체", 50, Color.BLACK));
        */
        fab1.setImageResource(R.drawable.fab_free_n);
        fab2.setImageResource(R.drawable.fab_fee_n);
        fab3.setImageResource(R.drawable.fab_all_p);
        fab4.setImageResource(R.drawable.fab_mine);

        // fab3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.ezRed)));

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        // Daum Map API
        dmm = new DaumMapManager(this);
        dmm.setMode(0);
        dmm.runMapProcess(true, false);

        // Initialize radio buttons of search filter
        // InitializeRadioButtons();

        // Check passord expired
        if(LoginManager.isLogin()) {
            CheckPasswordExpired();
        }

        // Set login info
        SetLoginInfo();

        // Set click listener
        SetOnClickListenerToImg();
    }

    // https://stackoverflow.com/questions/33671196/floatingactionbutton-with-text-instead-of-image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab1 :
                // Toast.makeText(this, "무료", Toast.LENGTH_SHORT).show();
                fab1.setImageResource(R.drawable.fab_free_p);
                fab2.setImageResource(R.drawable.fab_fee_n);
                fab3.setImageResource(R.drawable.fab_all_n);

                dmm.setMode(2);
                dmm.runMapProcessWithFee(2, true);
                break;
            case R.id.fab2 :
                // Toast.makeText(this, "유료", Toast.LENGTH_SHORT).show();
                fab1.setImageResource(R.drawable.fab_free_n);
                fab2.setImageResource(R.drawable.fab_fee_p);
                fab3.setImageResource(R.drawable.fab_all_n);

                dmm.setMode(1);
                dmm.runMapProcessWithFee(1, true);
                break;
            case R.id.fab3 :
                // Toast.makeText(this, "전체", Toast.LENGTH_SHORT).show();
                fab1.setImageResource(R.drawable.fab_free_n);
                fab2.setImageResource(R.drawable.fab_fee_n);
                fab3.setImageResource(R.drawable.fab_all_p);

                dmm.setMode(0);
                dmm.runMapProcess(false, true);
                break;
            case R.id.fab4 :
                // Toast.makeText(this, "내위치", Toast.LENGTH_SHORT).show();
                dmm.setMapCenterWithMyLoc();
                break;
            default:
                break;
        }
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
                // Toast.makeText(getApplicationContext(), "메뉴 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_search:
                // Toast.makeText(getApplicationContext(), "검색 버튼 클릭됨", Toast.LENGTH_LONG).show();
                // https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android
                Intent intent = new Intent(this, SearchActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                // startActivityForResult(intent, 1);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
                return true;
/*
            case R.id.action_filter:
                Toast.makeText(getApplicationContext(), "필터 버튼 클릭됨", Toast.LENGTH_LONG).show();
                LinearLayout ll = (LinearLayout) findViewById(R.id.parkingTopLL);
                if(View.INVISIBLE == ll.getVisibility()) {
                    ll.setVisibility(View.VISIBLE);
                } else {
                    ll.setVisibility(View.INVISIBLE);
                }
                return true;
*/
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        //Alert로 종료시키기
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
        dialog.setTitle("종료 알림")
                .setMessage("정말 종료하시겠습니까?")
                .setPositiveButton("종료합니다.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapActivity.this, "종료하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create().show();
    }

    // https://medium.com/@hitherejoe/exploring-the-android-design-support-library-bottom-navigation-drawer-548de699e8e0
    private void InitializeBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        /*
                        case R.id.bottom_nav_one:
                            Toast.makeText(getApplicationContext(), "공유 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            break;
                        */
                        case R.id.bottom_nav_two:
                            // Toast.makeText(getApplicationContext(), "제보 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                                getApplicationContext().startActivity(intent);
                            } else {
                                final View ll = findViewById(R.id.mapOverlayLayout);
                                if (View.INVISIBLE == ll.getVisibility()) {
                                    ll.setVisibility(View.VISIBLE);
                                } else {
                                    ll.setVisibility(View.INVISIBLE);
                                }
                            }
                            break;
                        case R.id.bottom_nav_three:
                            // Toast.makeText(getApplicationContext(), "리스트 버튼 클릭됨", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MapActivity.this, ListActivity.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                            intent.putExtra("centerPointLat", dmm.centerPoint.getLatitude());
                            intent.putExtra("centerPointLng", dmm.centerPoint.getLongitude());
                            intent.putParcelableArrayListExtra("pzData", dmm.pzData);
                            // startActivity(intent);
                            startActivityForResult(intent, REQUEST_CODE_LIST);
                            break;
                    }
                    return true;
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH || requestCode == REQUEST_CODE_LIST) {
            if(resultCode == RESULT_OK) {
                String lat = data.getStringExtra("lat");
                String lng = data.getStringExtra("lng");
                // Toast.makeText(getApplicationContext(), "lat:" + lat + ", lng:" + lng, Toast.LENGTH_LONG).show();
                dmm.setMapCenter(Double.valueOf(lat), Double.valueOf(lng));     // 검색한 장소의 GPS로 이동
            }
        }
    }

    private void CheckPasswordExpired() {

        final Context myCtx = this;

        // 서버에 접속하여 임시비밀번호 및 비밀번호 만료 체크
        Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // 만료된 것 없음
                } else if(201 == msg.arg1) {    // 비밀번호 만료 -> 비번 변경 창으로 이동
                    // Toast.makeText(getApplicationContext(), "비밀번호가 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myCtx);
                    builder.setMessage("비밀번호가 만료(3개월)되었습니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(myCtx, PasswordChangeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                                    intent.putExtra("whereFrom", "MapActivity");
                                    myCtx.startActivity(intent);
                                    finish();
                                }
                            });
                    builder.show();
                    return;
                } else if(202 == msg.arg1) {    // 임시 비밀번호 만료 -> 임시비번 재발급 창으로 이동
                    // Toast.makeText(getApplicationContext(), "임시 비밀번호가 만료되었습니다.", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myCtx);
                    builder.setMessage("임시 비밀번호가 만료(1일)되었습니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(myCtx, TempPasswordSendActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                                    myCtx.startActivity(intent);
                                }
                            });
                    builder.show();
                    return;
                } else if(203 == msg.arg1) {    // 임시 비밀번호 만료 전 -> 비번 변경 창으로 이동
                    // Toast.makeText(getApplicationContext(), "임시 비밀번호를 변경해야합니다.", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myCtx);
                    builder.setMessage("임시 비밀번호를 변경해야합니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(myCtx, PasswordChangeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                                    intent.putExtra("whereFrom", "MapActivity");
                                    myCtx.startActivity(intent);
                                }
                            });
                    builder.show();
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
/* ORIGINAL
        if (id == R.id.drawer_my_reservation) {

        } else if (id == R.id.drawer_coupons) {

        } else if (id == R.id.drawer_points) {

        } else if (id == R.id.drawer_private_info) {
            Intent intent = new Intent(getApplicationContext(), PrivateInfoActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.drawer_alliance) {

        } else if (id == R.id.drawer_service_center) {

        } else if (id == R.id.drawer_preferences) {
            Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
            getApplicationContext().startActivity(intent);
        } else if (id == R.id.drawer_favorites) {
            FavoritesDataManager fdm = new FavoritesDataManager();
            String[] favorites = fdm.selectMyFavorites();
            dmm.runMapProcessWithFavorites(favorites);
        } else if (id == R.id.drawer_share_status) {

        } else if (id == R.id.drawer_report_status) {
            Intent intent = new Intent(getApplicationContext(), ReportStatusActivity.class);
            getApplicationContext().startActivity(intent);
        }
*/
        if (id == R.id.drawer_private_info) {
            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), PrivateInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            }
        } else if (id == R.id.drawer_service_center) {
            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), ServiceCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            }
        } else if (id == R.id.drawer_preferences) {
            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            }
        } else if (id == R.id.drawer_favorites) {
            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            } else {
                FavoritesDataManager fdm = new FavoritesDataManager();
                fdm.selectMyFavoritesAndSet(dmm);
            }
        } else if (id == R.id.drawer_report_status) {
            if (!LoginManager.isLogin()) {  // 로그인 안 한 경우
                Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), ReportStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                getApplicationContext().startActivity(intent);
            }
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

        TextView tvLoginTitle = header.findViewById(R.id.tvLoginTitle);
        if(LoginManager.isLogin()) {
            tvLoginTitle.setText("로그아웃");
        } else {
            tvLoginTitle.setText("로그인");
        }

        TextView tvNavHeaderSub = header.findViewById(R.id.tvNavHeaderSub);
        tvNavHeaderSub.setText(LoginManager.getEmail());
    }

    private void runLogInOutProcess() {
        if(LoginManager.isLogin()) {    // 로그인 한 상태
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
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
                            Toast.makeText(MapActivity.this, "로그아웃 하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create().show();
        } else {    // 로그아웃 된 상태
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
            getApplicationContext().startActivity(intent);
            finish();
        }
    }

    public void onClickLogout(View v) {
        runLogInOutProcess();
    }

    public void onClickLogin(View v) {
        runLogInOutProcess();
    }

    public void onClickNavHeader(View v) {
        // Toast.makeText(getApplicationContext(), "개인정보", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), PrivateInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        getApplicationContext().startActivity(intent);
    }

    private void SetOnClickListenerToImg() {
        ImageView imgOverlay3 = findViewById(R.id.imgOverlay3);
        imgOverlay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "제보 그만하기", Toast.LENGTH_SHORT).show();
                View ll = findViewById(R.id.mapOverlayLayout);
                if(View.VISIBLE == ll.getVisibility()) {
                    /*
                    Animation animate = new ScaleAnimation(
                            1f, 2f, // Start and end values for the X axis scaling
                            1f, 2f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
                    animate.setDuration(250);
                    animate.setFillAfter(true);
                    ll.startAnimation(animate);
                    */
                    ll.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void onClickReport(View v) {
        // Toast.makeText(getApplicationContext(), "제보하기", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        intent.putExtra("centerPointLat", dmm.centerPoint.getLatitude());
        intent.putExtra("centerPointLng", dmm.centerPoint.getLongitude());
        getApplicationContext().startActivity(intent);
    }
}
