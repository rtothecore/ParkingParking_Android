package kr.co.ezinfotech.parkingparking;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.LIST_TAB.DistanceOrderListAdapter;
import kr.co.ezinfotech.parkingparking.LIST_TAB.FeeOrderListAdapter;
import kr.co.ezinfotech.parkingparking.LIST_TAB.ListItemData;
import kr.co.ezinfotech.parkingparking.LIST_TAB.ListTabPagerAdapter;

public class ListActivity extends AppCompatActivity {

    Toolbar listToolbar = null;
    public ArrayList<PZData> pzData = new ArrayList<>();
    public Location centerPoint = new Location("centerPoint");
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(listToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("리스트");

        // Get parcel data
        centerPoint.setLatitude(getIntent().getDoubleExtra("centerPointLat", 0));
        centerPoint.setLongitude(getIntent().getDoubleExtra("centerPointLng", 0));
        // Toast.makeText(getApplicationContext(), "중심좌표:" + centerPoint.getLatitude() + ", " + centerPoint.getLongitude(), Toast.LENGTH_LONG).show();
        pzData = getIntent().getParcelableArrayListExtra("pzData");

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.list_tab);
        tabLayout.addTab(tabLayout.newTab().setText("거리순"));
        tabLayout.addTab(tabLayout.newTab().setText("요금순"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager - http://android-java-development.blogspot.com/2012/05/system.html
        viewPager = (ViewPager) findViewById(R.id.list_viewpager);

        ListView listview1 = new ListView(this);
        ListView listview2 = new ListView(this);

        Vector<View> pages = new Vector<View>();

        pages.add(listview1);
        pages.add(listview2);
        ListTabPagerAdapter pagerAdapter = new ListTabPagerAdapter(this, pages);
        viewPager.setAdapter(pagerAdapter);

        // Set Listview
        setLV_listOrderByDistance(listview1);
        setLV_listOrderByFee(listview2);

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
        //return super.onOptionsItemSelected(item);
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

    private void setLV_listOrderByDistance(ListView listViewVal) {
        final ArrayList<ListItemData> oData = new ArrayList<>();

        for(int i = 0; i < pzData.size(); i++) {
            ListItemData oItem = new ListItemData();
            oItem.strName = pzData.get(i).name;
            if(pzData.get(i).addr_road.equals("null")) {
                oItem.strAddress = "주소 : 미등록";
            } else {
                oItem.strAddress = "주소 : " + pzData.get(i).addr_road;
            }

            if(pzData.get(i).tel.equals("null")) {
                oItem.strTel = "전화번호 : 미등록";
            } else {
                oItem.strTel = "전화번호 : " + pzData.get(i).tel;
            }

            // https://developer88.tistory.com/70
            float distance = centerPoint.distanceTo(pzData.get(i).loc);
            Log.d("CENTERPOINT", "중심점 LAT:" + centerPoint.getLatitude());
            Log.d("CENTERPOINT", "중심점 LNG:" + centerPoint.getLongitude());
            Log.d("PZ", "주차장 LAT:" + pzData.get(i).loc.getLatitude());
            Log.d("PZ", "주차장 LNG:" + pzData.get(i).loc.getLongitude());
            Log.d("DISTANCE", "distance:" + distance);

            oItem.loc = new Location("loc");
            oItem.loc.setLatitude(pzData.get(i).loc.getLatitude());
            oItem.loc.setLongitude(pzData.get(i).loc.getLongitude());

            if(5000 >= distance) {  // 맵의 중심점으로부터 5Km 이내에 있는 주차장만 보여주기
                oItem.strDistance = String.format("%.2f", (distance / 1000));
                oData.add(oItem);
            }
        }

        // Sort
        Ascending ascending = new Ascending();
        Collections.sort(oData, ascending);

        // Set textview after sort
        for(int j = 0; j < oData.size(); j++) {
            oData.get(j).strDistance += "km";
        }

        DistanceOrderListAdapter oAdapter = new DistanceOrderListAdapter(oData);
        listViewVal.setAdapter(oAdapter);

        listViewVal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // MapActivity로 되돌아가기
                Intent intent = new Intent();
                intent.putExtra("lat", Double.toString(oData.get(position).loc.getLatitude()));
                intent.putExtra("lng", Double.toString(oData.get(position).loc.getLongitude()));
                setResult(RESULT_OK, intent);
                finish();
                // Toast.makeText(getApplicationContext(), pzData.get(position).addr_road + " 버튼 터치됨", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), oData.get(position).strAddress + " 버튼 터치됨", Toast.LENGTH_LONG).show();
            }
        });
    }

    // 오름차순 - http://happyryu.tistory.com/267, http://codeman77.tistory.com/4
    class Ascending implements Comparator<ListItemData> {
        @Override
        public int compare(ListItemData o1, ListItemData o2) {
            return Float.compare(Float.parseFloat(o1.strDistance), Float.parseFloat(o2.strDistance));
        }
    }

    private void setLV_listOrderByFee(ListView listViewVal) {
        final ArrayList<ListItemData> oData = new ArrayList<>();

        for(int i = 0; i < pzData.size(); i++) {
            ListItemData oItem = new ListItemData();
            oItem.strName = pzData.get(i).name;

            // oItem.strAddress = "주소 : " + pzData.get(i).addr_road;
            // oItem.strTel = "전화번호 : " + pzData.get(i).tel;
            if(pzData.get(i).addr_road.equals("null")) {
                oItem.strAddress = "주소 : 미등록";
            } else {
                oItem.strAddress = "주소 : " + pzData.get(i).addr_road;
            }

            if(pzData.get(i).tel.equals("null")) {
                oItem.strTel = "전화번호 : 미등록";
            } else {
                oItem.strTel = "전화번호 : " + pzData.get(i).tel;
            }

            oItem.strFee = pzData.get(i).park_base.fee;

            float distance = centerPoint.distanceTo(pzData.get(i).loc);
            Log.d("CENTERPOINT", "중심점 LAT:" + centerPoint.getLatitude());
            Log.d("CENTERPOINT", "중심점 LNG:" + centerPoint.getLongitude());
            Log.d("PZ", "주차장 LAT:" + pzData.get(i).loc.getLatitude());
            Log.d("PZ", "주차장 LNG:" + pzData.get(i).loc.getLongitude());
            Log.d("DISTANCE", "distance:" + distance);

            oItem.loc = new Location("loc");
            oItem.loc.setLatitude(pzData.get(i).loc.getLatitude());
            oItem.loc.setLongitude(pzData.get(i).loc.getLongitude());

            if(5000 >= distance) {  // 맵의 중심점으로부터 5Km 이내에 있는 주차장만 보여주기
                // oItem.strDistance = String.format("%.2f", (distance / 1000));
                oData.add(oItem);
            }
        }

        // Sort
        AscendingFee ascending = new AscendingFee();
        Collections.sort(oData, ascending);

        // Set textview after sort
        for(int j = 0; j < oData.size(); j++) {
            oData.get(j).strFee += "원";
        }

        FeeOrderListAdapter oAdapter = new FeeOrderListAdapter(oData);
        listViewVal.setAdapter(oAdapter);

        listViewVal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // MapActivity로 되돌아가기
                Intent intent = new Intent();
                intent.putExtra("lat", Double.toString(oData.get(position).loc.getLatitude()));
                intent.putExtra("lng", Double.toString(oData.get(position).loc.getLongitude()));
                setResult(RESULT_OK, intent);
                finish();
                // Toast.makeText(getApplicationContext(), pzData.get(position).addr_road + " 버튼 터치됨", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), oData.get(position).strAddress + " 버튼 터치됨", Toast.LENGTH_LONG).show();
            }
        });
    }

    // 오름차순 - http://happyryu.tistory.com/267, http://codeman77.tistory.com/4
    class AscendingFee implements Comparator<ListItemData> {
        @Override
        public int compare(ListItemData o1, ListItemData o2) {
            return Float.compare(Float.parseFloat(o1.strFee), Float.parseFloat(o2.strFee));
        }
    }

}
