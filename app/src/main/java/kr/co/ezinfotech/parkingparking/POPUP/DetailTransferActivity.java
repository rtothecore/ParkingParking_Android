package kr.co.ezinfotech.parkingparking.POPUP;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.kakao.kakaolink.v2.*;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

import kr.co.ezinfotech.parkingparking.R;

// http://ghj1001020.tistory.com/9
public class DetailTransferActivity extends Activity {

    String name = null;
    String addr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_transfer);

        //데이터 가져오기
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        addr = intent.getStringExtra("addr");

        //
        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(getApplicationContext(), errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Toast.makeText(getApplicationContext(), "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            }
        };
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    */

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    public void mOnCickByKakaotalk(View v) {
        Log.d("DTA", "mOnCickByKakaotalk");
        String text = "앱 이지파킹에서 안내합니다.\n\n[" + name + "]\n주소 : " + addr;
        sendDefaultTextTemplate(text);
    }

    public void mOnCickBySMS(View v) {
        Log.d("DTA", "mOnCickBySMS");
        String text = "앱 이지파킹에서 안내합니다.\n\n[" + name + "]\n주소 : " + addr;
        sendMmsIntent(text);
    }

    public void mOnCickCopyAddress(View v) {
        Log.d("DTA", "mOnCickCopyAddress");
        setClipBoardLink(this, addr);
    }

    private Map<String, String> getServerCallbackArgs() {
        Map<String, String> callbackParameters = new HashMap<>();
        callbackParameters.put("user_id", "1234");
        callbackParameters.put("title", "프로방스 자동차 여행 !@#$%");
        return callbackParameters;
    }

    private void sendDefaultTextTemplate(String textVal) {
        TextTemplate params = TextTemplate.newBuilder(
                textVal,
                LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .build()
        )
                .setButtonTitle("이지파킹에서 확인")
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private ResponseCallback<KakaoLinkResponse> callback;
    private Map<String, String> serverCallbackArgs = getServerCallbackArgs();

    // http://developside.tistory.com/33
    public void sendMmsIntent(String smsBodyVal){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", smsBodyVal);
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // http://iw90.tistory.com/154
    public static void setClipBoardLink(Context context , String link){
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", link);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
    }

}
