package kr.co.ezinfotech.parkingparking.SIGN_UP;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        // tabLayout.addTab(tabLayout.newTab().setText("서비스"));
        // tabLayout.addTab(tabLayout.newTab().setText("개인정보취급방침"));
        // tabLayout.addTab(tabLayout.newTab().setText("위치기반서비스"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // set font to tablayout - https://stackoverflow.com/questions/31067265/change-the-font-of-tab-text-in-android-design-support-tablayout
        /*
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    // http://recipes4dev.tistory.com/125, https://code.i-harness.com/ko-kr/q/1da0c81
                    Typeface tf = Typeface.createFromAsset(getAssets(), "nanum_gothic.ttf");//Font file in /assets
                    ((TextView) tabViewChild).setTypeface(tf, Typeface.NORMAL);
                }
            }
        }
        */

        // Initializing ViewPager - https://www.journaldev.com/10096/android-viewpager-example-tutorial
        viewPager = (ViewPager) findViewById(R.id.terms_detail_viewpager);
        viewPager.setAdapter(new TermsDetailCustomPagerAdapter(this));

        // https://m.blog.naver.com/PostView.nhn?blogId=pistolcaffe&logNo=220629248791&proxyReferer=https%3A%2F%2Fwww.google.com%2F
        // https://stackoverflow.com/questions/38049076/tablayout-tabs-text-not-displaying
        tabLayout.setupWithViewPager(viewPager);

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
