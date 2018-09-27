package kr.co.ezinfotech.parkingparking.DB;

/**
 * Created by hkim on 2018-09-27.
 */

public class SearchHistoryDBCtrct {

    private SearchHistoryDBCtrct() {};

    public static final String TBL_SEARCH_HISTORY = "SEARCH_HISTORY";
    public static final String COL_NO = "NO";
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_PLACE_NAME = "PLACE_NAME";
    public static final String COL_ADDRESS_NAME = "ADDRESS_NAME";
    public static final String COL_ROAD_ADDRESS_NAME = "ROAD_ADDRESS_NAME";
    public static final String COL_X = "X";
    public static final String COL_Y = "Y";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_SEARCH_HISTORY + " " +
            "(" +
                COL_NO +          " INTEGER PRIMARY KEY AUTOINCREMENT"          + ", " +
                COL_USER_ID +            " TEXT NOT NULL"                                + ", " +
                COL_PLACE_NAME +      " TEXT NOT NULL"                                   + ", " +
                COL_ADDRESS_NAME +      " TEXT NOT NULL"                                 + ", " +
                COL_ROAD_ADDRESS_NAME +      " TEXT NOT NULL"                            + ", " +
                COL_X +          " TEXT NOT NULL"                                        + ", " +
                COL_Y +        " TEXT NOT NULL"                                          +
            ")";

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_SEARCH_HISTORY;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_SEARCH_HISTORY;

    public static final String SQL_SELECT_ALL_WITH_PLACE_NAME = "SELECT * FROM " + TBL_SEARCH_HISTORY + " WHERE " + COL_PLACE_NAME + "='";

    public static final String SQL_SELECT_ALL_WITH_USER_ID = "SELECT * FROM " + TBL_SEARCH_HISTORY + " WHERE " + COL_USER_ID + "='";

    public static final String SQL_SELECT_ALL_WITH_NO = "SELECT * FROM " + TBL_SEARCH_HISTORY + " WHERE " + COL_NO + "='";

    public static final String SQL_INSERT = "INSERT OR REPLACE INTO " + TBL_SEARCH_HISTORY + " " +
            "(" + COL_USER_ID + ", " + COL_PLACE_NAME + ", " + COL_ADDRESS_NAME + ", " + COL_ROAD_ADDRESS_NAME + ", " + COL_X + ", " + COL_Y +
            ") VALUES ";

    public static final String SQL_DELETE = "DELETE FROM " + TBL_SEARCH_HISTORY;
}
