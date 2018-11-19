package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

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

import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class MapClusterDataManager extends Activity {

    JSONArray resultData = null;
    JSONArray resultData2 = null;
    ArrayList<MapClusterData> mcDatas = new ArrayList<>();

    public MapClusterDataManager() {
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET
    // http://itmir.tistory.com/624
    private class GetMapClustersTask extends AsyncTask<Void, Void, Void> {
        Context ctx = null;
        MapView mapView = null;

        public GetMapClustersTask(Context ctxVal, MapView mapViewVal) {
            ctx = ctxVal;
            mapView = mapViewVal;
        }

        protected Void doInBackground(Void... noVal) {
            getMapClusterData();
            getMapClusterCount();
            return null;
        }

        protected void onPostExecute(Void result) {
            MapPOIItem mCustomMarker = null;
            for (int i = 0; i < mcDatas.size(); i++) {
                // https://devtalk.kakao.com/t/android-mapview-custom-view/46225/3
                MapCircle circle = new MapCircle(
                        MapPoint.mapPointWithGeoCoord(Double.parseDouble(mcDatas.get(i).lat), Double.parseDouble(mcDatas.get(i).lng)), // center
                        Integer.parseInt(mcDatas.get(i).radius), // radius
                        Color.argb(128, 255, 0, 0), // strokeColor
                        Color.argb(128, 0, 255, 0) // fillColor
                );
                mapView.addCircle(circle);

                mCustomMarker = new MapPOIItem();
                mCustomMarker.setItemName(mcDatas.get(i).no);
                mCustomMarker.setTag(i);
                mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(mcDatas.get(i).lat), Double.parseDouble(mcDatas.get(i).lng)));
                mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

                // https://devtalk.kakao.com/t/android-mapview-custom-view/46225/3
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View inflatedFrame = inflater.inflate(R.layout.view_map_bb_b, null);

                ((TextView)inflatedFrame.findViewById(R.id.view_m_b_tv)).setText(mcDatas.get(i).clusterCount);

                Bitmap bitmap = UtilManager.createBitmapFromView(inflatedFrame.findViewById(R.id.view_m_b));
                mCustomMarker.setCustomImageBitmap(bitmap);

                mapView.addPOIItem(mCustomMarker);
            }
            // mapView.selectPOIItem(mCustomMarker, true);
        }
    }

    public void GetMapClusterDataAndSet (Context ctxVal, MapView mapViewVal) {
        new GetMapClustersTask(ctxVal, mapViewVal).execute();
    }

    private void getMapClusterCount() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getMapClusterCount"); /*URL*/

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
            resultData2 = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getFavoriteData", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(200 == responseCode) {
            parseMapClusterCount();
        }
    }

    private void getMapClusterData() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getAllMapcenter"); /*URL*/

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
            parseMapClusterData();
        }
    }

    private void parseMapClusterCount() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData2.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData2.get(i);
                String tempId = jsonTemp.getString("_id");
                String tempCount = jsonTemp.getString("count");

                for(int j = 0; j < mcDatas.size(); j++) {
                    if(tempId.equals(mcDatas.get(j).no)) {
                        mcDatas.get(j).clusterCount = tempCount;
                    }
                }
            }
        } catch (Throwable t) {
            Log.e("parseMapClusterCount", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }

    private void parseMapClusterData() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < resultData.length(); i++) {
                JSONObject jsonTemp = (JSONObject)resultData.get(i);

                MapClusterData tempMCD = new MapClusterData();
                tempMCD.no = jsonTemp.getString("no");
                tempMCD.lat = jsonTemp.getString("lat");
                tempMCD.lng = jsonTemp.getString("lng");
                tempMCD.radius = jsonTemp.getString("radius");

                mcDatas.add(tempMCD);
            }
        } catch (Throwable t) {
            Log.e("parseMapClusterData", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////// GET

}
