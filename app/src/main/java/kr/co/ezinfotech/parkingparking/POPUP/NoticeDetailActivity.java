package kr.co.ezinfotech.parkingparking.POPUP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.R;

public class NoticeDetailActivity extends Activity {

    String subject = null;
    String contents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice_detail);

        // 데이터 가져오기
        Intent intent = getIntent();
        subject = intent.getStringExtra("subject");
        contents = intent.getStringExtra("contents");

        // 데이터 UI 셋팅
        TextView tvNoticePopupSubject = findViewById(R.id.tvNoticePopupSubject);
        tvNoticePopupSubject.setText(subject);

        TextView tvNoticePopupContents = findViewById(R.id.tvNoticePopupContents);
        tvNoticePopupContents.setText(contents);
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


}
