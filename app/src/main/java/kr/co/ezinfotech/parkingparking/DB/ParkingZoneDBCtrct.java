package kr.co.ezinfotech.parkingparking.DB;

/**
 * Created by hkim on 2018-04-03.
 */

public class ParkingZoneDBCtrct {

    private ParkingZoneDBCtrct() {};

    public static final String TBL_PARKING_ZONE = "PARKING_ZONE";
    public static final String COL_NO = "NO";
    public static final String COL_NAME = "NAME";
    public static final String COL_DIVISION = "DIVISION";
    public static final String COL_TYPE = "TYPE";
    public static final String COL_ADDR_ROAD = "ADDR_ROAD";
    public static final String COL_ADDR_JIBUN = "ADDR_JIBUN";
    public static final String COL_TOTALP = "TOTALP";
    public static final String COL_FEED = "FEED";
    public static final String COL_BUJE = "BUJE";
    public static final String COL_OP_DATE = "OP_DATE";
    public static final String COL_WOP_START = "WOP_START";
    public static final String COL_WOP_END = "WOP_END";
    public static final String COL_SOP_START = "SOP_START";
    public static final String COL_SOP_END = "SOP_END";
    public static final String COL_HOP_START = "HOP_START";
    public static final String COL_HOP_END = "HOP_END";
    public static final String COL_FEE_INFO = "FEE_INFO";
    public static final String COL_BASE_TIME = "BASE_TIME";
    public static final String COL_BASE_FEE = "BASE_FEE";
    public static final String COL_ADDTERM_TIME = "ADDTERM_TIME";
    public static final String COL_ADDTERM_FEE = "ADDTERM_FEE";
    public static final String COL_ONEDAYPARK_TIME = "ONEDAYPARK_TIME";
    public static final String COL_ONEDAYPARK_FEE = "ONEDAYPARK_FEE";
    public static final String COL_MONTH_FEE = "MONTH_FEE";
    public static final String COL_PAYMENT = "PAYMENT";
    public static final String COL_REMARKS = "REMARKS";
    public static final String COL_MANAGER = "MANAGER";
    public static final String COL_TEL = "TEL";
    public static final String COL_LAT = "LAT";
    public static final String COL_LNG = "LNG";
    public static final String COL_DATA_DATE = "DATA_DATE";
    public static final String COL_HOMEPAGE = "HOMEPAGE";
    public static final String COL_PARK_SPACE_SMALL = "PARK_SPACE_SMALL";
    public static final String COL_PARK_SPACE_MID = "PARK_SPACE_MID";
    public static final String COL_PARK_SPACE_BIG = "PARK_SPACE_BIG";
    public static final String COL_PARK_SPACE_ELEC = "PARK_SPACE_ELEC";
    public static final String COL_PARK_SPACE_HAND = "PARK_SPACE_HAND";
    public static final String COL_SALE_INFO = "SALE_INFO";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_PARKING_ZONE + " " +
            "(" +
                COL_NO +            " TEXT PRIMARY KEY" + ", " +
                COL_NAME +          " TEXT"                                   + ", " +
                COL_DIVISION +      " TEXT"                                   + ", " +
                COL_TYPE +      " TEXT"                                   + ", " +
                COL_ADDR_ROAD +      " TEXT"                                   + ", " +
                COL_ADDR_JIBUN +          " TEXT"                                   + ", " +
                COL_TOTALP +        " TEXT"                                   + ", " +
                COL_FEED +        " TEXT"                                   + ", " +
                COL_BUJE +        " TEXT"                                   + ", " +
                COL_OP_DATE+        " TEXT"                                   + ", " +
                COL_WOP_START +        " TEXT"                                   + ", " +
                COL_WOP_END +        " TEXT"                                   + ", " +
                COL_SOP_START +        " TEXT"                                   + ", " +
                COL_SOP_END +        " TEXT"                                   + ", " +
                COL_HOP_START +        " TEXT"                                   + ", " +
                COL_HOP_END +        " TEXT"                                   + ", " +
                COL_FEE_INFO +        " TEXT"                                   + ", " +
                COL_BASE_TIME +        " TEXT"                                   + ", " +
                COL_BASE_FEE +        " TEXT"                                   + ", " +
                COL_ADDTERM_TIME +        " TEXT"                                   + ", " +
                COL_ADDTERM_FEE +        " TEXT"                                   + ", " +
                COL_ONEDAYPARK_TIME +        " TEXT"                                   + ", " +
                COL_ONEDAYPARK_FEE +        " TEXT"                                   + ", " +
                COL_MONTH_FEE +        " TEXT"                                   + ", " +
                COL_PAYMENT +        " TEXT"                                   + ", " +
                COL_REMARKS +        " TEXT"                                   + ", " +
                COL_MANAGER +        " TEXT"                                   + ", " +
                COL_TEL +           " TEXT"                                   + ", " +
                COL_LAT +           " TEXT"                                   + ", " +
                COL_LNG +           " TEXT"                                   + ", " +
                COL_DATA_DATE +          " TEXT"                              + ", " +
                COL_HOMEPAGE +               " TEXT"                               + ", " +
                COL_PARK_SPACE_SMALL +               " TEXT"                               + ", " +
                COL_PARK_SPACE_MID +               " TEXT"                               + ", " +
                COL_PARK_SPACE_BIG +               " TEXT"                               + ", " +
                COL_PARK_SPACE_ELEC +               " TEXT"                               + ", " +
                COL_PARK_SPACE_HAND +               " TEXT"                               + ", " +
                COL_SALE_INFO +          " TEXT"                                  +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT_LAT_LNG_WITH_NAME = "SELECT " + COL_LAT + ", " + COL_LNG + " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NAME + "='";

    public static final String SQL_SELECT_NO_WITH_NAME = "SELECT " + COL_NO + " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NAME + "='";

    public static final String SQL_SELECT_NO_LAT_LNG_NAME = "SELECT " + COL_NO + ", " + COL_LAT + ", " + COL_LNG  + ", " + COL_NAME + " FROM " + TBL_PARKING_ZONE;

    public static final String SQL_SELECT_WITH_NO = "SELECT " + COL_NO + ", " + COL_NAME + ", " + COL_ADDR_ROAD + ", " + COL_TEL + ", " + COL_LAT + ", " + COL_LNG + ", " + COL_TOTALP + ", " +
                                                                   COL_WOP_START + ", " + COL_WOP_END + ", " + COL_SOP_START + ", " + COL_SOP_END + ", " + COL_HOP_START + ", " + COL_HOP_END +
                                                                   ", " + COL_FEE_INFO + ", " + COL_BASE_TIME + ", " + COL_BASE_FEE + ", " + COL_ADDTERM_TIME + ", " + COL_ADDTERM_FEE + ", " + COL_REMARKS + ", " + COL_DATA_DATE +
                                                                   " FROM " + TBL_PARKING_ZONE + " WHERE " + COL_NO + "='";

    public static final String SQL_SELECT_COLUMN = "SELECT * FROM " + TBL_PARKING_ZONE + " LIMIT 0";

    public static final String SQL_SELECT_WITH_DATADATE = "SELECT * FROM " + TBL_PARKING_ZONE + " WHERE " + COL_DATA_DATE + "='";

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_PARKING_ZONE + " " +
            "(" + COL_NO + ", " + COL_NAME + ", " + COL_DIVISION + ", " + COL_TYPE + ", " + COL_ADDR_ROAD + ", " + COL_ADDR_JIBUN + ", " + COL_TOTALP + ", " + COL_FEED + ", " +
                  COL_BUJE + ", " + COL_OP_DATE + ", " + COL_WOP_START + ", " + COL_WOP_END + ", " + COL_SOP_START + ", " + COL_SOP_END + ", " + COL_HOP_START + ", " + COL_HOP_END + ", " +
                  COL_FEE_INFO + ", " + COL_BASE_TIME + ", " + COL_BASE_FEE + ", " + COL_ADDTERM_TIME + ", " + COL_ADDTERM_FEE + ", " + COL_ONEDAYPARK_TIME + ", " + COL_ONEDAYPARK_FEE + ", " +
                  COL_MONTH_FEE + ", " + COL_PAYMENT + ", " + COL_REMARKS + ", " + COL_MANAGER + ", " + COL_TEL + ", " + COL_LAT + ", " + COL_LNG + ", " + COL_DATA_DATE + ", " +
                  COL_HOMEPAGE + ", " + COL_PARK_SPACE_SMALL + ", " + COL_PARK_SPACE_MID + ", " + COL_PARK_SPACE_BIG + ", " + COL_PARK_SPACE_ELEC + ", " + COL_PARK_SPACE_HAND + ", " + COL_SALE_INFO +
            ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_PARKING_ZONE;
}
