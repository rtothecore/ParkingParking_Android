package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
import java.util.List;

import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.DB.ParkingZoneDBCtrct;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class PZDataManager extends Activity {

    private int mode = 0;   // 0: just parse & insert, 1: check datadate, delete & insert PZ table
    Handler mHandler = null;

    JSONArray result = null;
    ArrayList<PZData> pzData = new ArrayList<>();

    public PZDataManager(Handler handlerVal) {
        mHandler = handlerVal;
    }

    public void initPzDB() {
        SQLiteDatabase db = DBManager.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ParkingZoneDBCtrct.SQL_SELECT, null);
        cursor.moveToFirst();

        // PARKING_ZONE 테이블의 데이터셋이 0인경우 ppRestServer서버에 접속하여 json파일을 참조하여 테이블에 INSERT
        // PARKKIG_ZONE 테이블에 데이터셋이 있는 경우 ppRestServer서버에 접속하여 json파일의 DATADATE로 테이블 SELECT하여 데이터날짜를 비교
        if(0 == cursor.getCount()) {
            mode = 0;
        } else {
            mode = 1;
        }

        runPZDataThreadProcess();
    }

    private void runPZDataThreadProcess() {
        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                getPZData();

                Message message = Message.obtain();
                message.arg1 = 777;
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void getPZData() {
        Log.i("getPZData()-0", "Get PZ data");

        StringBuilder urlBuilder = new StringBuilder("http://192.168.0.73:8083/pzData"); /*URL*/

        Log.i("getPZData()-1", urlBuilder.toString());

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
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getPZData-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        switch (mode) {
            case 0 :
                parseNInsertPZ();
                break;
            case 1 :
                checkDataDateNInsertPZ();
                break;
            default :
                break;
        }
    }

    private void checkDataDateNInsertPZ() {
        String dataDateVal = "";
        try{
            JSONObject jsonTemp = (JSONObject)result.get(0);
            dataDateVal = jsonTemp.getString("data_date");
            if(isExistDataWithDataDate(dataDateVal)) {
                // dateDataVal과 일치하는 데이터셋이 존재하므로 테이블 업데이트 안함
            } else {
                // dateDataVal과 데이터셋이 일치하지 않으므로 테이블 업데이트(delete & insert)
                deletePZTable();
                parseNInsertPZ();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("checkDataDateNInsertPZ", "Could not parse malformed JSON");
        }
    }

    private void deletePZTable() {
        SQLiteDatabase db = DBManager.dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DELETE);
    }

    private boolean isExistDataWithDataDate(String dataDateVal) {
        SQLiteDatabase db = DBManager.dbHelper.getReadableDatabase();
        String sqlSelect = ParkingZoneDBCtrct.SQL_SELECT_WITH_DATADATE + dataDateVal + "' LIMIT 1";
        Log.i("isExistDataWithDataDate", sqlSelect);
        Cursor cursor = db.rawQuery(sqlSelect, null);
        cursor.moveToFirst();
        if(0 == cursor.getCount()) {
            return false;
        } else {
            return true;
        }
    }

    private void parseNInsertPZ() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < result.length(); i++) {
                JSONObject jsonTemp = (JSONObject)result.get(i);

                PZData tempPz = new PZData();
                tempPz.no = jsonTemp.getString("no");
                tempPz.name = jsonTemp.getString("name");
                tempPz.division = jsonTemp.getString("division");
                tempPz.type = jsonTemp.getString("type");
                tempPz.addr_road = jsonTemp.getString("addr_road");
                tempPz.addr_jibun = jsonTemp.getString("addr_jibun");
                tempPz.total_p = jsonTemp.getString("total_p");
                tempPz.feed = jsonTemp.getString("feed");
                tempPz.buje = jsonTemp.getString("buje");
                tempPz.op_date = jsonTemp.getString("op_date");

                JSONObject jsonTemp2 = (JSONObject)jsonTemp.get("w_op");
                tempPz.w_op = new PZTermData();
                tempPz.w_op.start_date = jsonTemp2.getString("start_date");
                tempPz.w_op.end_date = jsonTemp2.getString("end_date");

                jsonTemp2 = (JSONObject)jsonTemp.get("s_op");
                tempPz.s_op = new PZTermData();
                tempPz.s_op.start_date = jsonTemp2.getString("start_date");
                tempPz.s_op.end_date = jsonTemp2.getString("end_date");

                jsonTemp2 = (JSONObject)jsonTemp.get("h_op");
                tempPz.h_op = new PZTermData();
                tempPz.h_op.start_date = jsonTemp2.getString("start_date");
                tempPz.h_op.end_date = jsonTemp2.getString("end_date");

                tempPz.fee_info = jsonTemp.getString("fee_info");

                jsonTemp2 = (JSONObject)jsonTemp.get("park_base");
                tempPz.park_base = new PZTFData();
                tempPz.park_base.time = jsonTemp2.getString("time");
                tempPz.park_base.fee = jsonTemp2.getString("fee");

                jsonTemp2 = (JSONObject)jsonTemp.get("add_term");
                tempPz.add_term = new PZTFData();
                tempPz.add_term.time = jsonTemp2.getString("time");
                tempPz.add_term.fee = jsonTemp2.getString("fee");

                jsonTemp2 = (JSONObject)jsonTemp.get("one_day_park");
                tempPz.one_day_park = new PZTFData();
                tempPz.one_day_park.time = jsonTemp2.getString("time");
                tempPz.one_day_park.fee = jsonTemp2.getString("fee");

                tempPz.month_fee = jsonTemp.getString("month_fee");
                tempPz.payment = jsonTemp.getString("payment");
                tempPz.remarks = jsonTemp.getString("remarks");
                tempPz.manager = jsonTemp.getString("manager");
                tempPz.tel = jsonTemp.getString("tel");

                tempPz.loc = new Location("");
                tempPz.loc.setLatitude(ParseDouble(jsonTemp.getString("lat")));
                tempPz.loc.setLongitude(ParseDouble(jsonTemp.getString("lng")));

                tempPz.data_date = jsonTemp.getString("data_date");
                pzData.add(tempPz);
            }
        } catch (Throwable t) {
            Log.e("parseNInsertPZ()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        ///////////////////////////////// INSERT DB //////////////////////////////////////////
        for(int i = 0; i < pzData.size(); i++) {
            PZData tempPzData = pzData.get(i);
            // Log.i("GEOCODING", "lat:" + tempPzData.loc.getLatitude() + ", lng:" + tempPzData.loc.getLongitude());
            // 1. PZData에 위,경도값이 없을 경우 주소를 지오코딩하여 DB에 Insert
            if("-1".equals(tempPzData.loc.getLatitude()) || "-1".equals(tempPzData.loc.getLongitude())
                    || "-1.0".equals(tempPzData.loc.getLatitude()) || "-1.0".equals(tempPzData.loc.getLongitude())
                    || "0".equals(tempPzData.loc.getLatitude()) || "0".equals(tempPzData.loc.getLongitude())
                    || "0.0".equals(tempPzData.loc.getLatitude()) || "0.0".equals(tempPzData.loc.getLongitude())
                    || 0 == tempPzData.loc.getLatitude() || 0 == tempPzData.loc.getLongitude()
                    || -1.0 == tempPzData.loc.getLatitude() || -1.0 == tempPzData.loc.getLongitude()
                    ) {
                Location tempLoc = UtilManager.runGeoCoding(tempPzData.addr_road);
                tempPzData.loc.setLatitude(tempLoc.getLatitude());
                tempPzData.loc.setLongitude(tempLoc.getLongitude());
                Log.i("parseNInsertPZ()-1", "주소 지오코딩 후 DB Insert");
            } else {    // 2. PZData에 위,경도값이 있는 경우
                Log.i("parseNInsertPZ()-2", "주소 지오코딩 없이 DB Insert");
            }
            insertPZTableWithPzData(tempPzData);
        }
    }

    private double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }

    private void insertPZTableWithPzData(PZData pzData) {
        SQLiteDatabase db = DBManager.dbHelper.getWritableDatabase();

        String sqlInsert = ParkingZoneDBCtrct.SQL_INSERT +
                " (" +
                "'" + pzData.no + "', " +
                "'" + pzData.name + "', " +
                "'" + pzData.division + "', " +
                "'" + pzData.type + "', " +
                "'" + pzData.addr_road + "', " +
                "'" + pzData.addr_jibun + "', " +
                "'" + pzData.total_p + "', " +
                "'" + pzData.feed + "', " +
                "'" + pzData.buje + "', " +
                "'" + pzData.op_date + "', " +

                "'" + pzData.w_op.start_date + "', " +
                "'" + pzData.w_op.end_date + "', " +
                "'" + pzData.s_op.start_date + "', " +
                "'" + pzData.s_op.end_date + "', " +
                "'" + pzData.h_op.start_date + "', " +
                "'" + pzData.h_op.end_date + "', " +

                "'" + pzData.fee_info + "', " +

                "'" + pzData.park_base.time + "', " +
                "'" + pzData.park_base.fee + "', " +
                "'" + pzData.add_term.time + "', " +
                "'" + pzData.add_term.fee + "', " +
                "'" + pzData.one_day_park.time + "', " +
                "'" + pzData.one_day_park.fee + "', " +

                "'" + pzData.month_fee + "', " +
                "'" + pzData.payment + "', " +
                "'" + pzData.remarks + "', " +
                "'" + pzData.manager + "', " +
                "'" + pzData.tel + "', " +
                "'" + pzData.loc.getLatitude() + "', " +
                "'" + pzData.loc.getLongitude() + "', " +
                "'" + pzData.data_date +
                "')";
        db.execSQL(sqlInsert);
        Log.i("insertPZTableWithPzData", sqlInsert);
    }

    public ArrayList<PZData> getAllPZData() {
        SQLiteDatabase db = DBManager.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ParkingZoneDBCtrct.SQL_SELECT, null);
        ArrayList<PZData> tempPZDatas = new ArrayList<>();

        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                //Log.i("selectLatLng", "LAT:" + cursor.getString(0) + " LNG:" + cursor.getString(1) );
                PZData tempPZData = new PZData();
                tempPZData.no = cursor.getString(0);
                tempPZData.name = cursor.getString(1);
                tempPZData.division = cursor.getString(2);
                tempPZData.type = cursor.getString(3);
                tempPZData.addr_road = cursor.getString(4);
                tempPZData.addr_jibun = cursor.getString(5);
                tempPZData.total_p = cursor.getString(6);
                tempPZData.feed = cursor.getString(7);
                tempPZData.buje = cursor.getString(8);
                tempPZData.op_date = cursor.getString(9);
                tempPZData.w_op = new PZTermData();
                tempPZData.w_op.start_date = cursor.getString(10);
                tempPZData.w_op.end_date = cursor.getString(11);
                tempPZData.s_op = new PZTermData();
                tempPZData.s_op.start_date = cursor.getString(12);
                tempPZData.s_op.end_date = cursor.getString(13);
                tempPZData.h_op = new PZTermData();
                tempPZData.h_op.start_date = cursor.getString(14);
                tempPZData.h_op.end_date = cursor.getString(15);
                tempPZData.fee_info = cursor.getString(16);
                tempPZData.park_base = new PZTFData();
                tempPZData.park_base.time = cursor.getString(17);
                tempPZData.park_base.fee = cursor.getString(18);
                tempPZData.add_term = new PZTFData();
                tempPZData.add_term.time = cursor.getString(19);
                tempPZData.add_term.fee = cursor.getString(20);
                tempPZData.one_day_park = new PZTFData();
                tempPZData.one_day_park.time = cursor.getString(21);
                tempPZData.one_day_park.fee = cursor.getString(22);
                tempPZData.month_fee = cursor.getString(23);
                tempPZData.payment = cursor.getString(24);
                tempPZData.remarks = cursor.getString(25);
                tempPZData.manager = cursor.getString(26);
                tempPZData.tel = cursor.getString(27);
                tempPZData.loc = new Location("");
                tempPZData.loc.setLatitude(Double.parseDouble(cursor.getString(28)));
                tempPZData.loc.setLongitude(Double.parseDouble(cursor.getString(29)));
                tempPZData.data_date = cursor.getString(30);
                tempPZDatas.add(tempPZData);
                cursor.moveToNext();
            }
        }

        return tempPZDatas;
    }

    public ArrayList<PZData> getPZDataWithParam(int division, int type, int op, int fee) {
        SQLiteDatabase db = DBManager.dbHelper.getReadableDatabase();

        String query = ParkingZoneDBCtrct.SQL_SELECT;
        /*
        if((0 != division) || (0 != type) || (0 != op) || (0 != fee)) {
            query += " WHERE ";
        }
        */
        query += " WHERE ";

        if(0 != division) {
            if(1 == division) {
                query += ParkingZoneDBCtrct.COL_DIVISION + "='공영'";
            } else if(2 == division) {
                query += ParkingZoneDBCtrct.COL_DIVISION + "='민영'";
            }
        } else {
            query += "1";
        }

        query += " AND ";
        if(0 != type) {
            if(1 == type) {
                query += ParkingZoneDBCtrct.COL_TYPE + "='노상'";
            } else if(2 == type) {
                query += ParkingZoneDBCtrct.COL_TYPE + "='노외'";
            }
        } else {
            query += "1";
        }

        query += " AND ";
        if(0 != op) {
            if(1 == op) {
                query += ParkingZoneDBCtrct.COL_OP_DATE + "='평일'";
            } else if(2 == op) {
                query += ParkingZoneDBCtrct.COL_OP_DATE + "='평일+토요일'";
            } else if(3 == op) {
                query += ParkingZoneDBCtrct.COL_OP_DATE + "='평일+토요일+공휴일'";
            } else if(4 == op) {
                query += ParkingZoneDBCtrct.COL_OP_DATE + "='공휴일'";
            }
        } else {
            query += "1";
        }

        query += " AND ";
        if(0 != fee) {
            if(1 == fee) {
                query += ParkingZoneDBCtrct.COL_FEE_INFO + "='유료'";
            } else if(2 == fee) {
                query += ParkingZoneDBCtrct.COL_FEE_INFO + "='무료'";
            }
        } else {
            query += "1";
        }

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<PZData> tempPZDatas = new ArrayList<>();

        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                //Log.i("selectLatLng", "LAT:" + cursor.getString(0) + " LNG:" + cursor.getString(1) );
                PZData tempPZData = new PZData();
                tempPZData.no = cursor.getString(0);
                tempPZData.name = cursor.getString(1);
                tempPZData.division = cursor.getString(2);
                tempPZData.type = cursor.getString(3);
                tempPZData.addr_road = cursor.getString(4);
                tempPZData.addr_jibun = cursor.getString(5);
                tempPZData.total_p = cursor.getString(6);
                tempPZData.feed = cursor.getString(7);
                tempPZData.buje = cursor.getString(8);
                tempPZData.op_date = cursor.getString(9);
                tempPZData.w_op = new PZTermData();
                tempPZData.w_op.start_date = cursor.getString(10);
                tempPZData.w_op.end_date = cursor.getString(11);
                tempPZData.s_op = new PZTermData();
                tempPZData.s_op.start_date = cursor.getString(12);
                tempPZData.s_op.end_date = cursor.getString(13);
                tempPZData.h_op = new PZTermData();
                tempPZData.h_op.start_date = cursor.getString(14);
                tempPZData.h_op.end_date = cursor.getString(15);
                tempPZData.fee_info = cursor.getString(16);
                tempPZData.park_base = new PZTFData();
                tempPZData.park_base.time = cursor.getString(17);
                tempPZData.park_base.fee = cursor.getString(18);
                tempPZData.add_term = new PZTFData();
                tempPZData.add_term.time = cursor.getString(19);
                tempPZData.add_term.fee = cursor.getString(20);
                tempPZData.one_day_park = new PZTFData();
                tempPZData.one_day_park.time = cursor.getString(21);
                tempPZData.one_day_park.fee = cursor.getString(22);
                tempPZData.month_fee = cursor.getString(23);
                tempPZData.payment = cursor.getString(24);
                tempPZData.remarks = cursor.getString(25);
                tempPZData.manager = cursor.getString(26);
                tempPZData.tel = cursor.getString(27);
                tempPZData.loc = new Location("");
                tempPZData.loc.setLatitude(Double.parseDouble(cursor.getString(28)));
                tempPZData.loc.setLongitude(Double.parseDouble(cursor.getString(29)));
                tempPZData.data_date = cursor.getString(30);
                tempPZDatas.add(tempPZData);
                cursor.moveToNext();
            }
        }

        return tempPZDatas;
    }
}
