package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class PlaceDataManager extends Activity {

    String searchKeyword = null;
    JSONObject result = null;
    public PlaceData placeData = new PlaceData();
    public ListView lvSearched = null;
    Context ctx = null;

    public PlaceDataManager(String searchKeywordVal) {
        searchKeyword = searchKeywordVal;
    }

    public void setListView(ListView lvVal) {
        lvSearched = lvVal;
    }

    public void setContext(Context ctxVal) {
        ctx = ctxVal;
    }

    public void runSearch() {
        // http://mainia.tistory.com/5090
        new Thread() {
            public void run() {
                getSearchedPlace();
            }
        }.start();
    }

    private void getSearchedPlace() {
        Log.i("getSearchedPlace()-0", "Get Place data");

        // StringBuilder urlBuilder = new StringBuilder("http://192.168.0.73:8083/searchWithKeyword/코엑스"); /*URL*/
        // StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/searchWithKeyword/" + searchKeyword); /*URL*/
        StringBuilder urlBuilder = null;
        try {
            // https://www.androidpub.com/484030
            urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/searchWithKeyword/" + URLEncoder.encode(searchKeyword, "UTF-8")); /*URL*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("getSearchedPlace()-1", urlBuilder.toString());

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
            result = new JSONObject(sb.toString());
        } catch (Throwable t) {
            Log.e("getSearchedPlace-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        parseData();

        // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLV();
            }
        });

        Log.i("getSearchedPlace()-2", "Success");
    }

    private void parseData() {
        PlaceData tempPd = new PlaceData();
        tempPd.documents = new ArrayList<PDDocuments>();
        try {
            JSONArray jsonTemp = (JSONArray)result.get("documents");
            for(int i = 0; i < jsonTemp.length(); i++) {
                JSONObject jsonTemp2 = (JSONObject)jsonTemp.get(i);
                PDDocuments tempPDD = new PDDocuments();
                tempPDD.address_name = jsonTemp2.getString("address_name");
                tempPDD.category_group_code = jsonTemp2.getString("category_group_code");
                tempPDD.category_group_name = jsonTemp2.getString("category_group_name");
                tempPDD.category_name = jsonTemp2.getString("category_name");
                tempPDD.distance = jsonTemp2.getString("distance");
                tempPDD.id = jsonTemp2.getString("id");
                tempPDD.phone = jsonTemp2.getString("phone");
                tempPDD.place_name = jsonTemp2.getString("place_name");
                tempPDD.place_url = jsonTemp2.getString("place_url");
                tempPDD.road_address_name = jsonTemp2.getString("road_address_name");
                tempPDD.x = jsonTemp2.getString("x");
                tempPDD.y = jsonTemp2.getString("y");
                tempPd.documents.add(tempPDD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonTemp3 = (JSONObject)result.get("meta");
            tempPd.meta = new PDMeta();
            tempPd.meta.is_end = jsonTemp3.getBoolean("is_end");
            tempPd.meta.pageable_count = jsonTemp3.getInt("pageable_count");
            tempPd.meta.same_name = new PDMetaSameName();
            tempPd.meta.same_name.keyword = jsonTemp3.getJSONObject("same_name").getString("keyword");
            tempPd.meta.same_name.region = new ArrayList<String>();
            JSONArray jsonArrayTemp = jsonTemp3.getJSONObject("same_name").getJSONArray("region");
            for(int j = 0; j < jsonArrayTemp.length(); j++) {
                tempPd.meta.same_name.region.add(jsonArrayTemp.get(j).toString());
            }
            tempPd.meta.same_name.selected_region = jsonTemp3.getJSONObject("same_name").getString("selected_region");
            tempPd.meta.total_count = jsonTemp3.getInt("total_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        placeData = tempPd;
    }

    private void setLV() {
        ArrayList<SearchedItemData> oData = new ArrayList<>();

        for(int i = 0; i < placeData.documents.size(); i++) {
            SearchedItemData oItem = new SearchedItemData();
            oItem.strKeyword = placeData.documents.get(i).place_name;
            oItem.strAddress = placeData.documents.get(i).address_name;
            oData.add(oItem);
        }

        SearchedListAdapter oAdapter = new SearchedListAdapter(oData);
        lvSearched.setAdapter(oAdapter);
    }
}