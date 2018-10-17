package kr.co.ezinfotech.parkingparking;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DATA.PZPSData;
import kr.co.ezinfotech.parkingparking.DATA.PZTFData;
import kr.co.ezinfotech.parkingparking.DATA.PZTermData;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.DetailCustomPagerAdapter;

public class DetailActivity extends AppCompatActivity {

    Toolbar detailToolbar = null;
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private PZData pzData = new PZData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(detailToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        // getSupportActionBar().setTitle("지명, 주소 입력");

        // Get data
        Intent intent = getIntent();
        pzData.name = intent.getStringExtra("name");
        getSupportActionBar().setTitle(pzData.name);

        pzData.addr_road = intent.getStringExtra("addr_road");
        pzData.tel = intent.getStringExtra("tel");

        pzData.loc = new Location("lat");
        pzData.loc.setLatitude(intent.getDoubleExtra("lat", 0));
        pzData.loc.setLongitude(intent.getDoubleExtra("lng", 0));

        pzData.park_base = new PZTFData();
        pzData.park_base.time = intent.getStringExtra("park_base_time");
        pzData.park_base.fee = intent.getStringExtra("park_base_fee");
        pzData.add_term = new PZTFData();
        pzData.add_term.time = intent.getStringExtra("add_term_time");
        pzData.add_term.fee = intent.getStringExtra("add_term_fee");

        pzData.w_op = new PZTermData();
        pzData.w_op.start_time = intent.getStringExtra("w_op_start_time");
        pzData.w_op.end_time = intent.getStringExtra("w_op_end_time");
        pzData.s_op = new PZTermData();
        pzData.s_op.start_time = intent.getStringExtra("s_op_start_time");
        pzData.s_op.end_time = intent.getStringExtra("s_op_end_time");
        pzData.h_op = new PZTermData();
        pzData.h_op.start_time = intent.getStringExtra("h_op_start_time");
        pzData.h_op.end_time = intent.getStringExtra("h_op_end_time");

        pzData.fee_info = intent.getStringExtra("fee_info");

        pzData.one_day_park = new PZTFData();
        pzData.one_day_park.fee = intent.getStringExtra("one_day_park_fee");
        pzData.month_fee = intent.getStringExtra("month_fee");

        pzData.homepage = intent.getStringExtra("homepage");
        pzData.park_space_count = new PZPSData();
        pzData.park_space_count.small = intent.getStringExtra("park_space_small");
        pzData.park_space_count.mid = intent.getStringExtra("park_space_mid");
        pzData.park_space_count.big = intent.getStringExtra("park_space_big");
        pzData.park_space_count.elec = intent.getStringExtra("park_space_elec");
        pzData.park_space_count.hand = intent.getStringExtra("park_space_hand");
        pzData.sale_info = intent.getStringExtra("sale_info");

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.detail_tab);
        tabLayout.addTab(tabLayout.newTab().setText("상세"));
        tabLayout.addTab(tabLayout.newTab().setText("요금"));
        tabLayout.addTab(tabLayout.newTab().setText("운영"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager - https://www.journaldev.com/10096/android-viewpager-example-tutorial
        viewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        viewPager.setAdapter(new DetailCustomPagerAdapter(this, pzData));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "이전 버튼 터치됨", Toast.LENGTH_LONG).show();
                super.onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnDetailReserv(View v) {
        Toast.makeText(getApplicationContext(), "예약 터치됨", Toast.LENGTH_SHORT).show();
    }

    public void btnDetailPredict(View v) {
        Toast.makeText(getApplicationContext(), "예측 터치됨", Toast.LENGTH_SHORT).show();
    }

    public void btnDetailFavorites(View v) {
        Toast.makeText(getApplicationContext(), "즐겨찾기 터치됨", Toast.LENGTH_SHORT).show();
    }
}
