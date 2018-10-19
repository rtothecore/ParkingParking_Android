package kr.co.ezinfotech.parkingparking.SIGN_UP;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.R;

public class TermsDetailActivity extends AppCompatActivity {

    Toolbar termsDetailToolbar = null;
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_detail);

        termsDetailToolbar = (Toolbar) findViewById(R.id.terms_detail_toolbar);
        setSupportActionBar(termsDetailToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("이용약관");

        // Get data
        Intent intent = getIntent();
        int tabIndex = intent.getIntExtra("tabIndex", 0);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.terms_detail_tab);
        tabLayout.addTab(tabLayout.newTab().setText("서비스"));
        tabLayout.addTab(tabLayout.newTab().setText("개인정보취급방침"));
        tabLayout.addTab(tabLayout.newTab().setText("위치기반서비스"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager - https://www.journaldev.com/10096/android-viewpager-example-tutorial
        viewPager = (ViewPager) findViewById(R.id.terms_detail_viewpager);
        viewPager.setAdapter(new TermsDetailCustomPagerAdapter(this));

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

        // Set tab
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        tab.select();
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
}
