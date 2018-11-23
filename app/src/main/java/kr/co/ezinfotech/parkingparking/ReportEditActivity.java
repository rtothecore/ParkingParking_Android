package kr.co.ezinfotech.parkingparking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class ReportEditActivity extends AppCompatActivity {

    Toolbar reportEditToolbar = null;
    String code = null;
    String parking_name = null;
    String parking_tel = null;
    String parking_fee_info = null;
    String parking_etc_info = null;
    String parking_pictureA = null;
    String parking_pictureB = null;
    String parking_pictureC = null;

    Handler mHandler = null;
    Handler mHandler3 = null;

    int imageViewIndex = 0;
    static String postUrl = UtilManager.getPPServerIp() + "/reportImg/upload";
    static String boundary = "*****mgd*****";
    private DataOutputStream dataStream = null;
    static String CRLF = "\r\n";
    static String twoHyphens = "--";
    private File[] uploadFile = new File[3];
    private String[] uploadedFilename = new String[3];
    private boolean[] uploadFlag = new boolean[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_edit);

        reportEditToolbar = (Toolbar) findViewById(R.id.report_edit_toolbar);
        setSupportActionBar(reportEditToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icons8_left_24);
        getSupportActionBar().setTitle("제보수정");

        // Init value
        Arrays.fill(uploadFlag, false);

        // Get parcel data
        code = getIntent().getStringExtra("code");
        parking_name = getIntent().getStringExtra("parking_name");
        parking_tel = getIntent().getStringExtra("parking_tel");
        parking_fee_info = getIntent().getStringExtra("parking_fee_info");
        parking_etc_info = getIntent().getStringExtra("parking_etc_info");
        parking_pictureA = getIntent().getStringExtra("parking_pictureA");
        parking_pictureB = getIntent().getStringExtra("parking_pictureB");
        parking_pictureC = getIntent().getStringExtra("parking_pictureC");

        // Set data to UI
        EditText etReportEditParkingName = findViewById(R.id.etReportEditParkingName);
        etReportEditParkingName.setText(parking_name);

        EditText etReportEditPhoneNo = findViewById(R.id.etReportEditPhoneNo);
        etReportEditPhoneNo.setText(parking_tel);

        EditText etReportEditFeeInfo = findViewById(R.id.etReportEditFeeInfo);
        etReportEditFeeInfo.setText(parking_fee_info);

        if(null != parking_pictureA) {
            new DownloadImageTask((ImageView) findViewById(R.id.ivEditPhotoParkingEntrance))
                    .execute(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureA);
            Uri tempUri = Uri.parse(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureA);
            uploadFile[0] = new File(tempUri.toString());
        }

        if(null != parking_pictureB) {
            new DownloadImageTask((ImageView) findViewById(R.id.ivEditPhotoParkingTariff))
                    .execute(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureB);
            Uri tempUri = Uri.parse(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureB);
            uploadFile[1] = new File(tempUri.toString());
        }

        if(null != parking_pictureC) {
            new DownloadImageTask((ImageView) findViewById(R.id.ivEditPhotoParkingBills))
                    .execute(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureC);
            Uri tempUri = Uri.parse(UtilManager.getPPServerIp() + "/getReportImg/" + parking_pictureC);
            uploadFile[2] = new File(tempUri.toString());
        }

        EditText etReportEditEtcInfo = findViewById(R.id.etReportEditEtcInfo);
        etReportEditEtcInfo.setText(parking_etc_info);

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

    // https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
    // http://itmir.tistory.com/624
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void onClickPhotoParkingEntranceEdit(View v) {
        // Toast.makeText(getApplicationContext(), "주차장 입구 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
        // https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/jpeg");
        startActivityForResult(photoPickerIntent, 777);
        imageViewIndex = 0;
    }

    public void onClickPhotoParkingTariffEdit(View v) {
        // Toast.makeText(getApplicationContext(), "요금표 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 777);
        imageViewIndex = 1;
    }

    public void onClickPhotoParkingBillsEdit(View v) {
        // Toast.makeText(getApplicationContext(), "영수증 사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 777);
        imageViewIndex = 2;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                switch(imageViewIndex) {
                    case 0 :
                        ImageView ivPhotoParkingEntrance = findViewById(R.id.ivEditPhotoParkingEntrance);
                        ivPhotoParkingEntrance.setImageBitmap(selectedImage);

                        Uri tempUri = getImageUri(getApplicationContext(), selectedImage);
                        uploadFile[0] = new File(getRealPathFromURI(tempUri));
                        uploadFlag[0] = true;
                        break;
                    case 1:
                        ImageView ivPhotoParkingTariff = findViewById(R.id.ivEditPhotoParkingTariff);
                        ivPhotoParkingTariff.setImageBitmap(selectedImage);

                        tempUri = getImageUri(getApplicationContext(), selectedImage);
                        uploadFile[1] = new File(getRealPathFromURI(tempUri));
                        uploadFlag[1] = true;
                        break;
                    case 2:
                        ImageView ivPhotoParkingBills = findViewById(R.id.ivEditPhotoParkingBills);
                        ivPhotoParkingBills.setImageBitmap(selectedImage);

                        tempUri = getImageUri(getApplicationContext(), selectedImage);
                        uploadFile[2] = new File(getRealPathFromURI(tempUri));
                        uploadFlag[2] = true;
                        break;
                    default:
                        break;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void onClickBtnReportDel(View v) {
        Intent intent = new Intent(getApplicationContext(), ReportDelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        intent.putExtra("code", code);
        getApplicationContext().startActivity(intent);
    }

    public void onClickBtnReportEdit(View v) {
        if(checkAllInputForm()) {
            runUploadImgThreadProcess();
        }
    }

    private boolean checkAllInputForm() {
        EditText etReportEditParkingName = findViewById(R.id.etReportEditParkingName);
        if(etReportEditParkingName.getText().toString().trim().equals("")) {
            etReportEditParkingName.setError("주차장명을 입력해주세요.");
            return false;
        }

        EditText etReportEditPhoneNo = findViewById(R.id.etReportEditPhoneNo);
        if(etReportEditPhoneNo.getText().toString().trim().equals("") || etReportEditPhoneNo.getText().toString().length() < 7) {
            etReportEditPhoneNo.setError("주차장 전화번호를 입력해주세요.");
            return false;
        }

        EditText etReportEditFeeInfo = findViewById(R.id.etReportEditFeeInfo);
        if(etReportEditFeeInfo.getText().toString().trim().equals("")) {
            etReportEditFeeInfo.setError("주차장 요금정보를 입력해주세요.");
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START updateReportData
    private void updateReportData() {
        mHandler3 = new Handler(Looper.getMainLooper()) {   // http://ecogeo.tistory.com/329
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Insert 성공
                    // Toast.makeText(getApplicationContext(), "Insert 성공!", Toast.LENGTH_SHORT).show();
                    Log.i("updateReportData", "Update 성공");
                    showUpdateSuccessDlg();
                } else {
                    // Toast.makeText(getApplicationContext(), "Insert 실패", Toast.LENGTH_SHORT).show();
                    Log.i("updateReportData", "Update 실패");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = callUpdateREST();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler3.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private int callUpdateREST() {
        int responseCode = 0;
        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/updateReportWithData"); /*URL*/

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
            conn.setRequestMethod("PUT");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Content-type", "application/json");

        // https://m.blog.naver.com/beodeulpiri/220730560270
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            //
            EditText etReportEditParkingName = findViewById(R.id.etReportEditParkingName);
            String parkingName = etReportEditParkingName.getText().toString();

            EditText etReportEditPhoneNo = findViewById(R.id.etReportEditPhoneNo);
            String parkingTel = etReportEditPhoneNo.getText().toString();

            EditText etReportEditFeeInfo = findViewById(R.id.etReportEditFeeInfo);
            String parkingFeeInfo = etReportEditFeeInfo.getText().toString();

            EditText etReportEditEtcInfo = findViewById(R.id.etReportEditEtcInfo);
            String parkingEtcInfo = etReportEditEtcInfo.getText().toString();
            //
            jsonObject.accumulate("code", code);
            jsonObject.accumulate("parking_name", parkingName);
            jsonObject.accumulate("parking_tel", parkingTel);
            jsonObject.accumulate("parking_fee_info", parkingFeeInfo);
            jsonObject.accumulate("parking_etc_info", parkingEtcInfo);
            jsonObject.accumulate("parking_pictureA", uploadedFilename[0]);
            jsonObject.accumulate("parking_pictureB", uploadedFilename[1]);
            jsonObject.accumulate("parking_pictureC", uploadedFilename[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // convert JSONObject to JSON to String
        String json = jsonObject.toString();

        // Set some headers to inform server about the type of the content
        conn.setRequestProperty("Accept", "application/json");

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        conn.setDoOutput(true);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        conn.setDoInput(true);

        try {
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            responseCode = conn.getResponseCode();
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
            Log.e("callUpdateREST-0", sb.toString());
        } catch (Throwable t) {
            Log.e("callUpdateREST-1", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        return responseCode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// START runUploadImgThreadProcess
    private void runUploadImgThreadProcess() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "업로드 성공");
                    updateReportData();
                } else if(400 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 실패 - Malformed URL", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "Malformed URL");
                } else if(401 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "Upload failed");
                } else if(402 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 실패 - file no exists", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "file no exists");
                } else if(500 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 실패 - IOE", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "IOE");
                } else if(501 == msg.arg1) {
                    Toast.makeText(getApplicationContext(), "업로드 실패 - unknown", Toast.LENGTH_SHORT).show();
                    Log.i("runUploadImg", "unknown");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = uploadPictures();

                Message message = Message.obtain();
                switch(responseCode) {
                    case 200:
                        message.arg1 = 200;
                        break;
                    case 400:
                        message.arg1 = 400;
                        break;
                    case 401:
                        message.arg1 = 401;
                        break;
                    case 402:
                        message.arg1 = 402;
                        break;
                    case 500:
                        message.arg1 = 500;
                        break;
                    case 501:
                        message.arg1 = 501;
                        break;
                    default:
                        break;
                }
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    // http://micropilot.tistory.com/1872
    private int uploadPictures()
    {
        for(int i = 0; i < 3; i++) {
            if(uploadFlag[i]) {
                uploadPicture(uploadFile[i], i);
            } else {    // 업로드할 필요 없을 경우 기존 파일명을 그대로 쓴다
                switch(i) {
                    case 0:
                        uploadedFilename[0] = parking_pictureA;
                        break;
                    case 1:
                        uploadedFilename[1] = parking_pictureB;
                        break;
                    case 2:
                        uploadedFilename[2] = parking_pictureC;
                        break;
                }
            }
        }

        return 200;
    }

    private int uploadPicture(File pictureFile, int uploadIndex) {
        if (null != pictureFile && pictureFile.exists()) {
            String strUploadFile = pictureFile.toString();
            String fileName = strUploadFile.substring(strUploadFile.lastIndexOf("/") + 1);
            try
            {
                FileInputStream fileInputStream = new FileInputStream(pictureFile);
                URL connectURL = new URL(postUrl);
                HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("User-Agent", "myGeodiary-V1");
                conn.setRequestProperty("Connection","Keep-Alive");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

                conn.connect();

                dataStream = new DataOutputStream(conn.getOutputStream());

                // writeFormField("login", name);
                // writeFormField("password", password);
                writeFileField("img", fileName, "image/jpg", fileInputStream);

                // final closing boundary line
                dataStream.writeBytes(twoHyphens + boundary + twoHyphens + CRLF);

                fileInputStream.close();
                dataStream.flush();
                dataStream.close();
                dataStream = null;

                String response = getResponse(conn);
                parseUploadedResult(response, uploadIndex);
                // int responseCode = conn.getResponseCode();

                if (response.contains("uploaded successfully"))
                    return 200;
                else
                    // for now assume bad name/password
                    return 401;
            }
            catch (MalformedURLException mue) {
                // Log.e(Tag, "error: " + mue.getMessage(), mue);
                System.out.println("uploadPicture: Malformed URL: " + mue.getMessage());
                return 400;
            }
            catch (IOException ioe) {
                // Log.e(Tag, "error: " + ioe.getMessage(), ioe);
                System.out.println("uploadPicture: IOE: " + ioe.getMessage());
                return 500;
            }
            catch (Exception e) {
                // Log.e(Tag, "error: " + ioe.getMessage(), ioe);
                System.out.println("uploadPicture: unknown: " + e.getMessage());
                return 501;
            }
        } else {
            return 402;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// END

    private void parseUploadedResult(String response, int uploadIndex) {
        Log.e("parseUploadedResult", response);
        JSONArray result = null;

        try {
            result = new JSONArray(response);
        } catch (Throwable t) {
            Log.e("parseUploadedResult", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        JSONObject jsonTemp = null;
        try {
            jsonTemp = (JSONObject)result.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            uploadedFilename[uploadIndex] =  jsonTemp.getString("imgName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getResponse(HttpURLConnection conn)
    {
        try
        {
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            byte[] data = new byte[1024];
            int len = dis.read(data, 0, 1024);

            dis.close();
            int responseCode = conn.getResponseCode();

            if (len > 0)
                return new String(data, 0, len);
            else
                return "";
        }
        catch(Exception e)
        {
            System.out.println("GeoPictureUploader: biffed it getting HTTPResponse");
            //Log.e(TAG, "GeoPictureUploader: biffed it getting HTTPResponse");
            return "";
        }
    }

    private void writeFileField(String fieldName, String fieldValue, String type, FileInputStream fis)
    {
        try
        {
            // opening boundary line
            dataStream.writeBytes(twoHyphens + boundary + CRLF);
            dataStream.writeBytes("Content-Disposition: form-data; name=\""
                    + fieldName
                    + "\";filename=\""
                    + fieldValue
                    + "\""
                    + CRLF);
            dataStream.writeBytes("Content-Type: " + type +  CRLF);
            dataStream.writeBytes(CRLF);

            // create a buffer of maximum size
            int bytesAvailable = fis.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            // read file and write it into form...
            int bytesRead = fis.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                dataStream.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }

            // closing CRLF
            dataStream.writeBytes(CRLF);
        }
        catch(Exception e)
        {
            System.out.println("GeoPictureUploader.writeFormField: got: " + e.getMessage());
            //Log.e(TAG, "GeoPictureUploader.writeFormField: got: " + e.getMessage());
        }
    }

    // http://webnautes.tistory.com/1094
    private void showUpdateSuccessDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("제보정보를 수정했습니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        builder.show();
    }
}
