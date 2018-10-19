package kr.co.ezinfotech.parkingparking.SIGN_UP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import kr.co.ezinfotech.parkingparking.AUTH.AuthActivity;
import kr.co.ezinfotech.parkingparking.R;

public class TermsActivity extends AppCompatActivity {

    Toolbar termsToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        termsToolbar = (Toolbar) findViewById(R.id.terms_toolbar);
        setSupportActionBar(termsToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("이용약관");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Toast.makeText(getApplicationContext(), "이전 버튼 터치됨", Toast.LENGTH_LONG).show();
                super.onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 터치됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnServiceTermDetail(View v) {
        Intent intent = new Intent(getApplicationContext(), TermsDetailActivity.class);
        intent.putExtra("tabIndex", 0);
        getApplicationContext().startActivity(intent);
    }

    public void btnPrivateTermDetail(View v) {
        Intent intent = new Intent(getApplicationContext(), TermsDetailActivity.class);
        intent.putExtra("tabIndex", 1);
        getApplicationContext().startActivity(intent);
    }

    public void btnLocationTermDetail(View v) {
        Intent intent = new Intent(getApplicationContext(), TermsDetailActivity.class);
        intent.putExtra("tabIndex", 2);
        getApplicationContext().startActivity(intent);
    }

    public void chkAllAgreeTerms(View v) {
        if (((CheckBox)v).isChecked()) {
            // Toast.makeText(getApplicationContext(), "이용약관 전체동의!", Toast.LENGTH_LONG).show();

            CheckBox cbServiceTerm = (CheckBox)findViewById(R.id.cbServiceTerm);
            if(!cbServiceTerm.isChecked()) {
                cbServiceTerm.setChecked(true);
            }

            CheckBox cbPrivateTerm = (CheckBox)findViewById(R.id.cbPrivateTerm);
            if(!cbPrivateTerm.isChecked()) {
                cbPrivateTerm.setChecked(true);
            }

            CheckBox cbLocationTerm = (CheckBox)findViewById(R.id.cbLocationTerm);
            if(!cbLocationTerm.isChecked()) {
                cbLocationTerm.setChecked(true);
            }

            Button btnAgreeTerms = (Button)findViewById(R.id.btnAgreeTerms);
            btnAgreeTerms.setEnabled(true);
        } else {
            CheckBox cbServiceTerm = (CheckBox)findViewById(R.id.cbServiceTerm);
            if(cbServiceTerm.isChecked()) {
                cbServiceTerm.setChecked(false);
            }

            CheckBox cbPrivateTerm = (CheckBox)findViewById(R.id.cbPrivateTerm);
            if(cbPrivateTerm.isChecked()) {
                cbPrivateTerm.setChecked(false);
            }

            CheckBox cbLocationTerm = (CheckBox)findViewById(R.id.cbLocationTerm);
            if(cbLocationTerm.isChecked()) {
                cbLocationTerm.setChecked(false);
            }

            Button btnAgreeTerms = (Button)findViewById(R.id.btnAgreeTerms);
            btnAgreeTerms.setEnabled(false);
        }
    }

    public void chkServiceTerms(View v) {
        checkIsAllAgree();
    }

    public void chkPrivateTerms(View v) {
        checkIsAllAgree();
    }

    public void chkLocationTerms(View v) {
        checkIsAllAgree();
    }

    private void checkIsAllAgree() {
        Button btnAgreeTerms = (Button)findViewById(R.id.btnAgreeTerms);
        CheckBox cbAllAgreeTerms = (CheckBox)findViewById(R.id.cbAllAgreeTerms);

        CheckBox cbServiceTerm = (CheckBox)findViewById(R.id.cbServiceTerm);
        if(!cbServiceTerm.isChecked()) {
            btnAgreeTerms.setEnabled(false);
            cbAllAgreeTerms.setChecked(false);
            return;
        }

        CheckBox cbPrivateTerm = (CheckBox)findViewById(R.id.cbPrivateTerm);
        if(!cbPrivateTerm.isChecked()) {
            btnAgreeTerms.setEnabled(false);
            cbAllAgreeTerms.setChecked(false);
            return;
        }

        CheckBox cbLocationTerm = (CheckBox)findViewById(R.id.cbLocationTerm);
        if(!cbLocationTerm.isChecked()) {
            btnAgreeTerms.setEnabled(false);
            cbAllAgreeTerms.setChecked(false);
            return;
        }

        btnAgreeTerms.setEnabled(true);
        cbAllAgreeTerms.setChecked(true);
    }

    public void btnAgreeTermsOk(View v) {
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        getApplicationContext().startActivity(intent);
    }
}
