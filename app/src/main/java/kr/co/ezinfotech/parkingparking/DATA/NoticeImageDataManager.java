package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.POPUP.NoticeDetailActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class NoticeImageDataManager extends Activity {

    JSONArray resultData = null;
    ArrayList<NoticeImageData> nDatas = new ArrayList<>();

    public NoticeImageDataManager() {
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class GetNoticeImgsTask extends AsyncTask<Void, Void, Void> {
        Context ctx = null;

        public GetNoticeImgsTask(Context ctxVal) {
            ctx = ctxVal;
        }

        protected Void doInBackground(Void... noVal) {
            getNoticeImgData();
            return null;
        }

        protected void onPostExecute(Void result) {
            for(int i = 0; i < nDatas.size(); i++) {

                // sharedPreference 불러오기
                // http://arabiannight.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9CAndroid-SharedPreferences-%EC%82%AC%EC%9A%A9-%EC%98%88%EC%A0%9C
                SharedPreferences spPopup = ctx.getSharedPreferences("popup", MODE_PRIVATE);
                String dateForPopup = spPopup.getString(nDatas.get(i)._id, "");
                if(dateForPopup.equals(UtilManager.getNowDate())) { // spPopup에 저장된 날짜가 오늘날짜와 같을 경우 팝업을 생성하지 않는다.

                } else {
                    // https://stackoverflow.com/questions/7803771/call-to-getlayoutinflater-in-places-not-in-activity
                    LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View popupView = inflater.inflate(R.layout.popup_window, null);
                    final PopupWindow mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //popupView 에서 (LinearLayout 을 사용) 레이아웃이 둘러싸고 있는 컨텐츠의 크기 만큼 팝업 크기를 지정

                    mPopupWindow.setFocusable(true);
                    // 외부 영역 선택시 PopUp 종료

                    mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    Button cancel = (Button) popupView.findViewById(R.id.btn_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                        }
                    });

                    final int dataIdx = i;

                    Button ok = (Button) popupView.findViewById(R.id.btn_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // sharedPreference에 저장
                            SharedPreferences spPopup = ctx.getSharedPreferences("popup", MODE_PRIVATE);
                            SharedPreferences.Editor editor = spPopup.edit();
                            editor.putString(nDatas.get(dataIdx)._id, UtilManager.getNowDate());
                            editor.commit();

                            // Toast.makeText(ctx.getApplicationContext(), "오늘 다시보지 않기", Toast.LENGTH_SHORT).show();
                            mPopupWindow.dismiss();
                        }
                    });

                    // Set webview
                    // https://stackoverflow.com/questions/8987509/how-to-pass-html-string-to-webview-on-android
                    // https://developer.android.com/guide/webapps/webview#java
                    WebView mWebView = popupView.findViewById(R.id.wvPopup);

                    String data = "<html><body><a href=\"" + nDatas.get(i).link_url + "\"><img id=\"resizeImage\" src=\"" + "http://59.8.37.86:9081/getNoticeImg/" + nDatas.get(i).img_file_name + "\" width=\"100%\" alt=\"\" align=\"middle\" /></a></body></html>";
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
                    mWebView.getSettings().setLoadWithOverviewMode(true);
                    mWebView.getSettings().setUseWideViewPort(true);

                    // https://code.i-harness.com/ko-kr/q/2f4ad0
                    /*
                    mWebView.loadUrl("http://59.8.37.86:9081/getNoticeImg/" + nDatas.get(i).img_file_name);
                    mWebView.getSettings().setLoadWithOverviewMode(true);
                    mWebView.getSettings().setUseWideViewPort(true);
                    */
                }
            }
        }
    }

    public void GetNoticeImgDataAndSet (Context ctxVal) {
        new GetNoticeImgsTask(ctxVal).execute();
    }

    private void getNoticeImgData() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getAllNoticeImgs"); /*URL*/

        URL url = null;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");
        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
            responseCode = conn.getResponseCode();
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.disconnect();

        try {
            resultData = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getNoticeImgData", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(200 == responseCode) {
            parseNoticeImgsData();
        }
    }

    private void parseNoticeImgsData() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData.get(i);

                NoticeImageData tempNID = new NoticeImageData();
                tempNID._id = jsonTemp.getString("_id");
                tempNID.img_file_name = jsonTemp.getString("img_file_name");
                tempNID.display = jsonTemp.getString("display");
                tempNID.start_date = jsonTemp.getString("start_date");
                tempNID.end_date = jsonTemp.getString("end_date");
                tempNID.link_url = jsonTemp.getString("link_url");

                nDatas.add(tempNID);
            }
        } catch (Throwable t) {
            Log.e("parseNoticeImgsData", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET

}
