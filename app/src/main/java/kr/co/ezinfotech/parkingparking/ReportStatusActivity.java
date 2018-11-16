package kr.co.ezinfotech.parkingparking;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DATA.ReportData;
import kr.co.ezinfotech.parkingparking.DATA.ReportDataManager;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class ReportStatusActivity extends AppCompatActivity {

    Toolbar reportStatusToolbar = null;
    public ArrayList<ReportData> reportData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_status);

        reportStatusToolbar = (Toolbar) findViewById(R.id.report_status_toolbar);
        setSupportActionBar(reportStatusToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("제보현황");

        // Get report data from ppRestServer
        ReportDataManager rdm = new ReportDataManager();
        rdm.setContext(this);
        rdm.setEmail(LoginManager.getEmail());
        rdm.getReports();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }
}
