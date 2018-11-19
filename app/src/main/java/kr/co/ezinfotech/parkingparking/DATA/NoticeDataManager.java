package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.MAP.DaumMapManager;
import kr.co.ezinfotech.parkingparking.POPUP.NoticeDetailActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class NoticeDataManager extends Activity {

    JSONArray resultData = null;
    ArrayList<NoticeData> nDatas = new ArrayList<>();

    public NoticeDataManager() {
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class GetNoticesTask extends AsyncTask<Void, Void, Void> {
        Context ctx = null;
        LinearLayout ll = null;

        public GetNoticesTask(Context ctxVal, LinearLayout llVal) {
            ctx = ctxVal;
            ll = llVal;
        }

        protected Void doInBackground(Void... noVal) {
            getNoticeData();
            return null;
        }

        protected void onPostExecute(Void result) {
            LinearLayout rootLL = ll;

            for (int i = 0; i < nDatas.size(); i++) {
                View view = new View(ctx);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UtilManager.dpToPx(ctx, 1)));
                view.setBackgroundColor(Color.DKGRAY);

                rootLL.addView(view);

                LinearLayout childLL = new LinearLayout(ctx);
                childLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UtilManager.dpToPx(ctx, 50)));
                childLL.setGravity(Gravity.CENTER_VERTICAL);
                childLL.setOrientation(LinearLayout.HORIZONTAL);

                final int dataIndex = i;
                childLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, NoticeDetailActivity.class);
                        intent.putExtra("subject", nDatas.get(dataIndex).subject);
                        intent.putExtra("contents", nDatas.get(dataIndex).contents);
                        ctx.startActivity(intent);
                    }
                });

                rootLL.addView(childLL);

                TextView tvSubject = new TextView(ctx);
                tvSubject.setLayoutParams(new TableLayout.LayoutParams(UtilManager.dpToPx(ctx, 300), TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                tvSubject.setText(nDatas.get(i).subject);
                tvSubject.setTextSize(18);

                childLL.addView(tvSubject);

                TextView tvDate = new TextView(ctx);
                tvDate.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                tvDate.setText(nDatas.get(i).date);
                tvDate.setTextSize(12);

                childLL.addView(tvDate);
            }
        }
    }

    public void GetNoticeDataAndSet (Context ctxVal, LinearLayout llVal) {
        new GetNoticesTask(ctxVal, llVal).execute();
    }

    private void getNoticeData() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getAllNotices"); /*URL*/

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
            Log.e("getFavoriteData", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(200 == responseCode) {
            parseNoticesData();
        }
    }

    private void parseNoticesData() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData.get(i);

                NoticeData tempND = new NoticeData();
                tempND.subject = jsonTemp.getString("subject");
                tempND.contents = jsonTemp.getString("contents");
                tempND.date = jsonTemp.getString("date");

                nDatas.add(tempND);
            }
        } catch (Throwable t) {
            Log.e("parseNoticesData", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET

}
