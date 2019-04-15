package kr.co.ezinfotech.parkingparking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.co.ezinfotech.parkingparking.DATA.PDDocuments;
import kr.co.ezinfotech.parkingparking.DATA.PlaceDataManager;
import kr.co.ezinfotech.parkingparking.DATA.SearchHistoryData;
import kr.co.ezinfotech.parkingparking.DATA.SearchedItemData;
import kr.co.ezinfotech.parkingparking.DATA.SearchedListAdapter;
import kr.co.ezinfotech.parkingparking.DB.DBManager;
import kr.co.ezinfotech.parkingparking.DB.SearchHistoryDBCtrct;

import static kr.co.ezinfotech.parkingparking.DB.DBManager.dbHelper;

public class SearchActivity extends AppCompatActivity {

    Toolbar searchToolbar = null;
    private ListView m_oListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        // getSupportActionBar().setTitle("지명, 주소 입력");

        setLV();

        // https://www.androidpub.com/37945
        ((EditText)findViewById(R.id.searchEditText)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(getApplicationContext(), "검색 버튼 터치됨-" + v.getText(), Toast.LENGTH_LONG).show();
                    final PlaceDataManager pdm = new PlaceDataManager(v.getText().toString());
                    pdm.setListView((ListView)findViewById(R.id.LV_searched));
                    pdm.setContext(SearchActivity.this);
                    pdm.runSearch();

                    // Click listener - https://medium.com/@henen/%EB%B9%A0%EB%A5%B4%EA%B2%8C-%EB%B0%B0%EC%9A%B0%EB%8A%94-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%B7%B0-listview-4-%ED%81%B4%EB%A6%AD%EC%9D%B4%EB%B2%A4%ED%8A%B8-onitemclicklistener-toast-4432e650cb
                    pdm.lvSearched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // 1. SearchHistory 테이블에 클릭한 장소정보 INSERT
                            if(!isExistSamePlaceName(pdm.placeData.documents.get(position).place_name)) {   // DB에 이미 존재하는 장소명일 경우 INSERT 하지 않는다.
                                insertSHTableWithPlaceData(pdm.placeData.documents.get(position));
                            }

                            // 2. MapActivity로 되돌아가기
                            Intent intent = new Intent();
                            intent.putExtra("lat", pdm.placeData.documents.get(position).y);
                            intent.putExtra("lng", pdm.placeData.documents.get(position).x);
                            intent.putExtra("name", pdm.placeData.documents.get(position).place_name);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "이전 버튼 터치됨", Toast.LENGTH_LONG).show();
                super.onBackPressed();
                return true;
                /*
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "검색 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_filter:
                Toast.makeText(getApplicationContext(), "필터 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;
                */
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean isExistSamePlaceName(String placeNameVal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sqlSelect = SearchHistoryDBCtrct.SQL_SELECT_ALL_WITH_PLACE_NAME + placeNameVal + "' LIMIT 1";
        Log.i("isExistSamePlaceName", sqlSelect);
        Cursor cursor = db.rawQuery(sqlSelect, null);
        cursor.moveToFirst();
        if(0 == cursor.getCount()) {
            return false;
        } else {
            return true;
        }
    }

    public void insertSHTableWithPlaceData(PDDocuments pddVal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sqlInsert = SearchHistoryDBCtrct.SQL_INSERT +
                " (" +
                "'" + "ABCDEFGHIJKLMN" + "', " +    // Fix me!
                "'" + pddVal.place_name + "', " +
                "'" + pddVal.address_name + "', " +
                "'" + pddVal.road_address_name + "', " +
                "'" + pddVal.x + "', " +
                "'" + pddVal.y +
                "')";
        db.execSQL(sqlInsert);
        Log.i("insertSHTableWith...", sqlInsert);
    }

    private List<SearchHistoryData> selectSHTableWithUserId(String userIdVal) {
        List<SearchHistoryData> listSHData = new ArrayList<>();

        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String sqlSelect = SearchHistoryDBCtrct.SQL_SELECT_ALL_WITH_USER_ID + userIdVal + "' ORDER BY NO DESC";
        Cursor cursor = db.rawQuery(sqlSelect, null);
        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                SearchHistoryData tempSHData = new SearchHistoryData();
                tempSHData.no = cursor.getInt(0);
                tempSHData.user_id = cursor.getString(1);
                tempSHData.place_name = cursor.getString(2);
                tempSHData.address_name = cursor.getString(3);
                tempSHData.road_address_name = cursor.getString(4);
                tempSHData.x = cursor.getString(5);
                tempSHData.y = cursor.getString(6);
                listSHData.add(tempSHData);
                cursor.moveToNext();
            }
        }
        return listSHData;
    }

    private void setLV() {
        final List<SearchHistoryData> SHData = selectSHTableWithUserId("ABCDEFGHIJKLMN");
        ArrayList<SearchedItemData> oData = new ArrayList<>();

        for(int i = 0; i < SHData.size(); i++) {
            SearchedItemData oItem = new SearchedItemData();
            oItem.strKeyword = SHData.get(i).place_name;
            oItem.strAddress = SHData.get(i).address_name;
            oData.add(oItem);
        }

        SearchedListAdapter oAdapter = new SearchedListAdapter(oData);
        ((ListView)findViewById(R.id.LV_searched)).setAdapter(oAdapter);

        // Click event for listview
        ((ListView)findViewById(R.id.LV_searched)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // MapActivity로 되돌아가기
                Intent intent = new Intent();
                intent.putExtra("lat", SHData.get(position).y);
                intent.putExtra("lng", SHData.get(position).x);
                intent.putExtra("name", SHData.get(position).place_name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
