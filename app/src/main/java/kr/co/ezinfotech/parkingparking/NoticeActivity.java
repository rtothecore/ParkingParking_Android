package kr.co.ezinfotech.parkingparking;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.DATA.NoticeDataManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class NoticeActivity extends AppCompatActivity {

    Toolbar NToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        NToolbar = (Toolbar) findViewById(R.id.notice_toolbar);
        setSupportActionBar(NToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("공지사항");

        // Set data
        NoticeDataManager ndm = new NoticeDataManager();
        ndm.GetNoticeDataAndSet(this, (LinearLayout)findViewById(R.id.notice_root_LL));

        // Set layout
        /*
        LinearLayout rootLL = findViewById(R.id.notice_root_LL);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UtilManager.dpToPx(this, 1)));
        view.setBackgroundColor(Color.DKGRAY);

        rootLL.addView(view);

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UtilManager.dpToPx(this, 50)));
        ll.setGravity(Gravity.CENTER_VERTICAL);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        rootLL.addView(ll);

        TextView tvSubject = new TextView(this);
        tvSubject.setLayoutParams(new TableLayout.LayoutParams(UtilManager.dpToPx(this, 300), TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvSubject.setText("공지사항공지사항공지사항공지사항공지사항공지사항");
        tvSubject.setTextSize(18);

        ll.addView(tvSubject);

        TextView tvDate = new TextView(this);
        tvDate.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvDate.setText("2018-11-16");
        tvDate.setTextSize(12);

        ll.addView(tvDate);
        */
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
