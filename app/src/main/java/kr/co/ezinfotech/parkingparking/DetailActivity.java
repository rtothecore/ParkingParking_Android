package kr.co.ezinfotech.parkingparking;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DATA.PZTFData;
import kr.co.ezinfotech.parkingparking.DATA.PZTermData;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.DetailBasicAdapter;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.DetailBasicData;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.DetailCustomPagerAdapter;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.DetailTabPagerAdapter;
import kr.co.ezinfotech.parkingparking.LIST_TAB.DistanceOrderListAdapter;
import kr.co.ezinfotech.parkingparking.LIST_TAB.ListItemData;
import kr.co.ezinfotech.parkingparking.LIST_TAB.ListTabPagerAdapter;

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
        pzData.w_op.start_date = intent.getStringExtra("w_op_start_time");
        pzData.w_op.end_date = intent.getStringExtra("w_op_end_time");
        pzData.s_op = new PZTermData();
        pzData.s_op.start_date = intent.getStringExtra("s_op_start_time");
        pzData.s_op.end_date = intent.getStringExtra("s_op_end_time");
        pzData.h_op = new PZTermData();
        pzData.h_op.start_date = intent.getStringExtra("h_op_start_time");
        pzData.h_op.end_date = intent.getStringExtra("h_op_end_time");

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
}
