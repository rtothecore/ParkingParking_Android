package kr.co.ezinfotech.parkingparking.UTIL;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
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
        return "http://192.168.0.73:9081";
    }
}
