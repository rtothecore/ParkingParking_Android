package kr.co.ezinfotech.parkingparking.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private static final DBManager ourInstance = new DBManager();

    public static ParkingParkingDBHelper dbHelper = null;

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
    }

    public static void setContext(Context ctxVal) {
        dbHelper = new ParkingParkingDBHelper(ctxVal);   // http://recipes4dev.tistory.com/124?category=698941
    }

    public static void dropTables() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DROP_TBL);
        db.execSQL(FavoritesDBCtrct.SQL_DROP_TBL);
    }

    public static void deleteTables() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(ParkingZoneDBCtrct.SQL_DELETE);
        db.execSQL(FavoritesDBCtrct.SQL_DELETE);
    }
}
