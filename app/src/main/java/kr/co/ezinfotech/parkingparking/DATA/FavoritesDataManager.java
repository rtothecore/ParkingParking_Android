package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.DB.FavoritesDBCtrct;
import kr.co.ezinfotech.parkingparking.MAP.DaumMapManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class FavoritesDataManager extends Activity {

    JSONArray resultData = null;
    ArrayList<FavoriteData> fDatas = new ArrayList<>();
    String[] favoriteNos;

    public FavoritesDataManager () {
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class CheckExistFavoriteTask2 extends AsyncTask<String, Void, Boolean> {
        Context ctx = null;
        AppCompatButton acbFavorite = null;
        String no = null;
        boolean isFavorites = false;
        public CheckExistFavoriteTask2(Context ctxVal, AppCompatButton acbFavoriteVal) {
            ctx = ctxVal;
            acbFavorite = acbFavoriteVal;
        }

        protected Boolean doInBackground(String... noVal) {
            no = noVal[0];
            return getFavoriteDataWithNo(no);
        }

        protected void onPostExecute(Boolean result) {
            if(result) {    // 즐겨찾기된 주차장일 경우
                acbFavorite.setText("즐겨찾기 해제");
            } else {        // 즐겨찾기 안된 주차장일 경우
                acbFavorite.setText("즐겨찾기 추가");
            }
            isFavorites = result;

            // 즐겨찾기 버튼 이벤트
            acbFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFavorites) {
                        Toast.makeText(ctx, "즐겨찾기 해제-" + no, Toast.LENGTH_SHORT).show();
                        deleteFavorite(no);
                        acbFavorite.setText("즐겨찾기 추가");
                    } else {
                        Toast.makeText(ctx, "즐겨찾기 추가-" + no, Toast.LENGTH_SHORT).show();
                        insertFavorite(no);
                        acbFavorite.setText("즐겨찾기 해제");
                    }
                }
            });
        }
    }

    public void CheckExistFavoriteInDBAndSet2 (Context ctxVal, AppCompatButton acbFavoriteVal, String noVal) {
        new CheckExistFavoriteTask2(ctxVal, acbFavoriteVal).execute(noVal);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class CheckExistFavoriteTask extends AsyncTask<String, Void, Boolean> {
        Context ctx = null;
        ImageView ivStar = null;
        String no = null;
        boolean isFavorites = false;
        public CheckExistFavoriteTask(Context ctxVal, ImageView ivStarVal) {
            ctx = ctxVal;
            ivStar = ivStarVal;
        }

        protected Boolean doInBackground(String... noVal) {
            no = noVal[0];
            return getFavoriteDataWithNo(no);
        }

        protected void onPostExecute(Boolean result) {
            if(result) {    // 즐겨찾기된 주차장일 경우
                ivStar.setImageResource(R.drawable.icons8_star_filled_50);
                isFavorites = result;
            } else {        // 즐겨찾기 안된 주차장일 경우
                ivStar.setImageResource(R.drawable.icons8_star_50);
                isFavorites = result;
            }
            // 즐겨찾기(별모양) 터치 이벤트
            ivStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFavorites) {
                        Toast.makeText(ctx, "즐겨찾기 해제-" + no, Toast.LENGTH_SHORT).show();
                        // fdm.deleteFavorite(no);
                        deleteFavorite(no);
                        ivStar.setImageResource(R.drawable.icons8_star_50);
                        isFavorites = false;
                    } else {
                        Toast.makeText(ctx, "즐겨찾기 추가-" + no, Toast.LENGTH_SHORT).show();
                        // fdm.insertFavorite(no);
                        insertFavorite(no);
                        ivStar.setImageResource(R.drawable.icons8_star_filled_50);
                        isFavorites = true;
                    }
                }
            });
        }
    }

    public void CheckExistFavoriteInDBAndSet (Context ctxVal, ImageView ivStarVal, String noVal) {
        new CheckExistFavoriteTask(ctxVal, ivStarVal).execute(noVal);
    }

    private boolean getFavoriteDataWithNo(String noVal) {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getFavoriteWithEmailNNo/" + LoginManager.getEmail() + "/" + noVal); /*URL*/

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
            return countFavoriteData();
        }

        return false;
    }

    private boolean countFavoriteData() {
        if(0 < resultData.length()) {
            return true;
        } else {
            return false;
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET

/////////////////////////////////////////////////////////////////////////////////////////////////////////// DELETE
    // http://itmir.tistory.com/624
    private class DeleteFavoriteTask extends AsyncTask<String, Void, Void> {
        public DeleteFavoriteTask() {}

        protected Void doInBackground(String... emailVal) {
            String email = emailVal[0];
            String no = emailVal[1];
            callDeleteFavoriteREST(email, no);
            return null;
        }

        protected void onPostExecute() {
        }
    }

    private void deleteFavorite(String noVal) {
        new DeleteFavoriteTask().execute(LoginManager.getEmail(), noVal);
    }

    private void callDeleteFavoriteREST(String emailVal, String noVal) {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/deleteFavorite/" + emailVal); /*URL*/

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
            conn.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("parkingNo", noVal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // convert JSONObject to JSON to String
        String json = jsonObject.toString();

        // Set some headers to inform server about the type of the content
        conn.setRequestProperty("Accept", "application/json");

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        conn.setDoOutput(true);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        conn.setDoInput(true);

        try {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            responseCode = conn.getResponseCode();
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
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
        /*
        try {
            Log.e("DeleteFavoriteREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("DeleteFavoriteREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
        */
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// DELETE

/////////////////////////////////////////////////////////////////////////////////////////////////////////// INSERT
    // http://itmir.tistory.com/624
    private class InsertFavoriteTask extends AsyncTask<String, Void, Void> {
        public InsertFavoriteTask() {}

        protected Void doInBackground(String... emailVal) {
            String email = emailVal[0];
            String no = emailVal[1];
            callInsertFavoriteREST(email, no);
            return null;
        }

        protected void onPostExecute() {
        }
    }

    private void insertFavorite(String noVal) {
        new InsertFavoriteTask().execute(LoginManager.getEmail(), noVal);
    }

    private void callInsertFavoriteREST(String emailVal, String noVal) {
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/addNewFavorite"); /*URL*/

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
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("email", emailVal);
            jsonObject.accumulate("parking_no", noVal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // convert JSONObject to JSON to String
        String json = jsonObject.toString();

        // Set some headers to inform server about the type of the content
        conn.setRequestProperty("Accept", "application/json");

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        conn.setDoOutput(true);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        conn.setDoInput(true);

        try {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Response code: " + conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        try {
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
        /*
        try {
            Log.e("callInsertREST-0", sb.toString());
            isInsertSuccess = true;
        } catch (Throwable t) {
            Log.e("callInsertREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }
        */
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// INSERT

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class SelectMyFavoritesTask extends AsyncTask<String, Void, String[]> {
        DaumMapManager dmm = null;
        public SelectMyFavoritesTask(DaumMapManager dmmVal) {dmm = dmmVal;}

        protected String[] doInBackground(String... emailVal) {
            String email = emailVal[0];
            return getFavoriteDataWithEmail(email);
        }

        protected void onPostExecute(String[] result) {
            dmm.setMode(3);
            dmm.setFavorites(result);
            dmm.runMapProcessWithFavorites(result);
        }
    }

    public void selectMyFavoritesAndSet(DaumMapManager dmmVal) {
        new SelectMyFavoritesTask(dmmVal).execute(LoginManager.getEmail());
    }

    private String[] getParkingNosFromFdatas() {
        favoriteNos = new String[fDatas.size()];

        for (int i = 0; i < fDatas.size(); i++) {
            favoriteNos[i] = fDatas.get(i).parking_no;
        }

        return favoriteNos;
    }

    private String[] getFavoriteDataWithEmail(String emailVal) {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getFavoritesWithEmail/" + emailVal); /*URL*/

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
            parseFavoritesData();
            return getParkingNosFromFdatas();
        }

        return null;
    }

    private void parseFavoritesData() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData.get(i);

                FavoriteData tempFavorite = new FavoriteData();
                tempFavorite.user_email = jsonTemp.getString("user_email");
                tempFavorite.parking_no = jsonTemp.getString("parking_no");
                tempFavorite.date = jsonTemp.getString("date");

                fDatas.add(tempFavorite);
            }
        } catch (Throwable t) {
            Log.e("parseFavoritesData", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
}
