package kr.co.ezinfotech.parkingparking.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParkingParkingDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DBFILE_PARKING_PARKING = "parkingparking.db";

    public ParkingParkingDBHelper(Context context) {
        super(context, "/mnt/sdcard/" + DBFILE_PARKING_PARKING, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ParkingZoneDBCtrct.SQL_CREATE_TBL);
        sqLiteDatabase.execSQL(FavoritesDBCtrct.SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    public void onDownGrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
