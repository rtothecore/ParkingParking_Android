package kr.co.ezinfotech.parkingparking.UTIL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UtilManager {
    private static final UtilManager ourInstance = new UtilManager();

    private static Geocoder mCoder;

    public static UtilManager getInstance() {
        return ourInstance;
    }

    private UtilManager() {
    }

    public static void setContext(Context ctxVal) {
        mCoder = new Geocoder(ctxVal);
    }

    // 실제주소 => 위,경도 변환 - http://bitsoul.tistory.com/135
    public static Location runGeoCoding(String addr) {
        Location result = null;
        List<Address> list = null;

        try {
            list = mCoder.getFromLocationName(
                    addr, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("runGeoCoding","입출력 오류 - 서버에서 주소변환시 에러발생-" + addr);
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.e("runGeoCoding",addr + " 해당되는 주소 정보는 없습니다");
                result = new Location("");
                result.setLatitude(0);
                result.setLongitude(0);
            } else {
                result = new Location("");
                result.setLatitude(list.get(0).getLatitude());
                result.setLongitude(list.get(0).getLongitude());
            }
        }

        return result;
    }

    // 위,경도 => 실제주소 변환
    public static String runReverseGeoCoding(double latVal, double lonVal) {
        String resultAddr = "";
        try {
            List<Address> list = mCoder.getFromLocation(latVal, lonVal, 5);
            Log.i("runReverseGeoCoding", list.get(0).toString());

            if (null != list) {
                if(0 < list.size()) {
                    resultAddr = list.get(0).toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("runReverseGeoCoding", "getFromLocation IO exception");
        }
        return resultAddr;
    }

    // 이메일 주소 체크 - https://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getPPServerIp() {
        // return "http://192.168.0.73:9081";
        return "http://59.8.37.86:9081";
    }

    // https://medium.com/@euryperez/android-pearls-set-size-to-a-view-in-dp-programatically-71d22eed7fc0
    public static int dpToPx(Context ctx, int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    // https://devtalk.kakao.com/t/android-mapview-custom-view/46225/3
    public static Bitmap createBitmapFromView(View v) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }

    public static String cutTheString(String strVal, int length) {
        if(strVal.length() <= length) {  // 문자열의 길이가 자를 길이보다 작은 경우 문자열을 자를 필요 없이 그대로 내보낸다
            return strVal;
        } else {
            return strVal.substring(0, length) + "...";
        }
    }

    public static String getNowDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);

        return getTime;
    }
}
