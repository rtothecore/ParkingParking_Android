package kr.co.ezinfotech.parkingparking;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                if(777 == msg.arg1) {
                    Log.i("onCreate", "Thread job ended!");
                    /*
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    getApplicationContext().startActivity(intent);
                    */
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                    getApplicationContext().startActivity(intent);

                    finish();   // Destroy MainActivity
                } else if (666 == msg.arg1) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("서버에 접속할 수 없습니다")
                            .setMessage("잠시후 다시 실행해 주세요")
                            .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                }
            }
        };
        runPermissionListener(this);
        DBManager.setContext(this);
        UtilManager.setContext(this);
        // getHashKey2();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "called onDestroy");
    }

    // Using TedPermission library - https://github.com/ParkSangGwon/TedPermission
    private void runPermissionListener(Context ctx) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(ctx)
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Rational Title")
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("bla bla")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void Initialize() {
        SplashPageProcessor spp = new SplashPageProcessor();
        spp.setHandler(mHandler);
        spp.runProcess();

        // Authentificate Tmap api key
        TmapManager.setContext(this);
        TmapManager.authentifacateKey();
    }

    // http://superwony.tistory.com/12
    private void getHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.ezinfotech.parkingparking", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HashKey","key_hash="+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // https://developers.kakao.com/apps/227318/settings/general
    // https://stackoverflow.com/questions/44355452/google-play-app-signing-key-hash/44448437#44448437
    private void getHashKey2(){
        byte[] sha1 = {
                0x6A, (byte)0xFF, (byte)0xC6, (byte)0xDC, (byte)0xF1, 0x62, (byte)0xCC, 0x07, 0x13, 0x0D, 0x58, 0x50, (byte)0x9C, (byte)0xAE, (byte)0xF2, 0x01, (byte)0xD4, 0x32, (byte)0xCB, (byte)0xB0
        };
        Log.e("keyhash", Base64.encodeToString(sha1, Base64.NO_WRAP));
    }
}
