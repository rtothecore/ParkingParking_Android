package kr.co.ezinfotech.parkingparking.DATA;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.ReportEditActivity;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

/**
 * Created by hkim on 2018-04-17.
 */

public class ReportDataManager extends Activity {

    String email = null;
    Handler mHandler = null;
    Context parentCtx = null;
    JSONArray result = null;
    ArrayList<ReportData> reportData = new ArrayList<>();

    public void setContext(Context ctxVal) {
        parentCtx = ctxVal;
    }

    public void setEmail(String emailVal) {
        email = emailVal;
    }

    public void getReports() {
        runGetReportsThreadProcess();
    }

    private void runGetReportsThreadProcess() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override public void handleMessage(Message msg) {
                if(200 == msg.arg1) {   // Get reportCode successfully
                    Log.i("runGetReports", "Get reports successfully");
                    setDataToTableRow();
                } else {
                    Log.i("runGetReports", "Get reports failed");
                }
            }
        };

        ///////////////////////////////// Thread of network START //////////////////////////////
        // http://nocomet.tistory.com/10
        new Thread() {
            public void run() {
                int responseCode = getReportsData();

                Message message = Message.obtain();
                message.arg1 = responseCode;
                mHandler.sendMessage(message);
            }
        }.start();
        ///////////////////////////////// Thread of network END //////////////////////////////
    }

    private void setDataToTableRow() {
        TableLayout tlReportList = ((Activity)parentCtx).findViewById(R.id.tlReportList);

        // Remove table row except header title row
        tlReportList.removeViews(1, tlReportList.getChildCount() - 1);

        // Log.i("ReportData size:", String.valueOf(reportData.size()));
        for(int i = 0; i < reportData.size(); i++) {
            TableRow tr = new TableRow(parentCtx);
            // TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 100);
            tr.setLayoutParams(lp);
            //tr.setPadding(0, 0, 0, 2);

            //lp.setMargins(0, 0, 2, 0);

            TextView tvParkingName = new TextView(parentCtx);
            tvParkingName.setLayoutParams(lp);
            // tvParkingName.setBackgroundColor(parentCtx.getResources().getColor(R.color.table_row_text_bg_color));
            tvParkingName.setBackground(parentCtx.getResources().getDrawable(R.drawable.cell_shape));
            tvParkingName.setGravity(Gravity.CENTER);
            tvParkingName.setTextSize(16);
            tvParkingName.setText(reportData.get(i).parking_name);

            TextView tvReportDate = new TextView(parentCtx);
            tvReportDate.setLayoutParams(lp);
            // tvReportDate.setBackgroundColor(parentCtx.getResources().getColor(R.color.table_row_text_bg_color));
            tvReportDate.setBackground(parentCtx.getResources().getDrawable(R.drawable.cell_shape));
            tvReportDate.setGravity(Gravity.CENTER);
            tvReportDate.setTextSize(16);
            tvReportDate.setText(reportData.get(i).report_date.substring(0, 10));

            TextView tvReportStatus = new TextView(parentCtx);
            tvReportStatus.setLayoutParams(lp);
            // tvReportStatus.setBackgroundColor(parentCtx.getResources().getColor(R.color.table_row_text_bg_color));
            tvReportStatus.setBackground(parentCtx.getResources().getDrawable(R.drawable.cell_shape));
            tvReportStatus.setGravity(Gravity.CENTER);
            tvReportStatus.setTextSize(16);
            final int dataIndex = i;
            switch(reportData.get(i).status) {
                case "0" :
                    tvReportStatus.setText("접수");
                    tvParkingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveToReportEditActivity(dataIndex);
                        }
                    });
                    tvReportDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveToReportEditActivity(dataIndex);
                        }
                    });
                    break;
                case "1" :
                    tvReportStatus.setText("승인중");
                    tvParkingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(parentCtx, "승인중 상태입니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    tvReportDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(parentCtx, "승인중 상태입니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "2" :
                    tvReportStatus.setText("승인");
                    tvParkingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(parentCtx, "승인 상태입니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    tvReportDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(parentCtx, "승인 상태입니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "3" :
                    tvReportStatus.setText("보류");
                    tvParkingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveToReportEditActivity(dataIndex);
                        }
                    });
                    tvReportDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveToReportEditActivity(dataIndex);
                        }
                    });
                    tvReportStatus.setOnClickListener(new View.OnClickListener() {  // 보류사유
                        @Override
                        public void onClick(View v) {
                            // Toast.makeText(parentCtx, "tvReportStatus", Toast.LENGTH_SHORT).show();
                            showHoldReason(reportData.get(dataIndex).hold_reason);
                        }
                    });
                    break;
                default:
                    break;
            }

            if("1".equals(reportData.get(dataIndex).delete_status)) {    // 삭제요청이 있을 경우
                tvReportStatus.setText("삭제요청");
                tvParkingName.setOnClickListener(new View.OnClickListener() {   // 삭제요청 상태일 경우 터치이벤트 없앰
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(parentCtx, "삭제요청 상태입니다", Toast.LENGTH_SHORT).show();
                    }
                });
                tvReportDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(parentCtx, "삭제요청 상태입니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            tr.addView(tvParkingName);
            tr.addView(tvReportDate);
            tr.addView(tvReportStatus);

            tlReportList.addView(tr);
        }
    }

    private void moveToReportEditActivity(int dataIndex) {
        Intent intent = new Intent(parentCtx, ReportEditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
        intent.putExtra("code", reportData.get(dataIndex).code);
        //  intent.putExtra("report_date", reportData.get(dataIndex));
        //  intent.putExtra("user_email", reportData.get(dataIndex));
        //  intent.putExtra("user_phone_no", reportData.get(dataIndex));
        intent.putExtra("parking_name", reportData.get(dataIndex).parking_name);
        //  intent.putExtra("parking_lat", reportData.get(dataIndex));
        //  intent.putExtra("parking_lng", reportData.get(dataIndex));
        intent.putExtra("parking_tel", reportData.get(dataIndex).parking_tel);
        intent.putExtra("parking_fee_info", reportData.get(dataIndex).parking_fee_info);
        intent.putExtra("parking_etc_info", reportData.get(dataIndex).parking_etc_info);
        intent.putExtra("parking_pictureA", reportData.get(dataIndex).parking_pictureA);
        intent.putExtra("parking_pictureB", reportData.get(dataIndex).parking_pictureB);
        intent.putExtra("parking_pictureC", reportData.get(dataIndex).parking_pictureC);
        //  intent.putExtra("status", reportData.get(dataIndex));
        //  intent.putExtra("hold_reason", reportData.get(dataIndex));
        //  intent.putExtra("delete_status", reportData.get(dataIndex));
        //  intent.putExtra("delete_reason", reportData.get(dataIndex));

        parentCtx.startActivity(intent);
    }

    private void showHoldReason(String holdReason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentCtx);
        builder.setMessage("보류사유 : " + holdReason);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    private int getReportsData() {
        int responseCode = 0;
        Log.i("getReportsData()-0", "Get PZ data");

        StringBuilder urlBuilder = new StringBuilder(UtilManager.getPPServerIp() + "/getReports/" + email); /*URL*/
        Log.i("getReportsData()-1", urlBuilder.toString());

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
            result = new JSONArray(sb.toString());
        } catch (Throwable t) {
            Log.e("getReportsData-3", "Could not parse malformed JSON");
            t.printStackTrace();
        }

        if(200 == responseCode) {
            parseReports();
        }

        return responseCode;
    }

    private void parseReports() {
        ////////////////////////////// Parsing JSON ////////////////////////////////////////
        try {
            for(int i = 0; i < result.length(); i++) {
                JSONObject jsonTemp = (JSONObject)result.get(i);
                ReportData tempRD = new ReportData();

                if(jsonTemp.has("code")) {
                    tempRD.code = jsonTemp.getString("code");
                }
                if(jsonTemp.has("report_date")) {
                    tempRD.report_date = jsonTemp.getString("report_date");
                }
                if(jsonTemp.has("user_email")) {
                    tempRD.user_email = jsonTemp.getString("user_email");
                }
                if(jsonTemp.has("user_phone_no")) {
                    tempRD.user_phone_no = jsonTemp.getString("user_phone_no");
                }
                if(jsonTemp.has("parking_name")) {
                    tempRD.parking_name = jsonTemp.getString("parking_name");
                }
                if(jsonTemp.has("parking_lat")) {
                    tempRD.parking_lat = jsonTemp.getString("parking_lat");
                }
                if(jsonTemp.has("parking_lng")) {
                    tempRD.parking_lng = jsonTemp.getString("parking_lng");
                }
                if(jsonTemp.has("parking_tel")) {
                    tempRD.parking_tel = jsonTemp.getString("parking_tel");
                }
                if(jsonTemp.has("parking_fee_info")) {
                    tempRD.parking_fee_info = jsonTemp.getString("parking_fee_info");
                }
                if(jsonTemp.has("parking_etc_info")) {
                    tempRD.parking_etc_info = jsonTemp.getString("parking_etc_info");
                }
                if(jsonTemp.has("parking_pictureA")) {
                    tempRD.parking_pictureA = jsonTemp.getString("parking_pictureA");
                }
                if(jsonTemp.has("parking_pictureB")) {
                    tempRD.parking_pictureB = jsonTemp.getString("parking_pictureB");
                }
                if(jsonTemp.has("parking_pictureC")) {
                    tempRD.parking_pictureC = jsonTemp.getString("parking_pictureC");
                }
                if(jsonTemp.has("status")) {
                    tempRD.status = jsonTemp.getString("status");
                }
                if(jsonTemp.has("hold_reason")) {
                    tempRD.hold_reason = jsonTemp.getString("hold_reason");
                }
                if(jsonTemp.has("delete_status")) {
                    tempRD.delete_status = jsonTemp.getString("delete_status");
                }
                if(jsonTemp.has("delete_reason")) {
                    tempRD.delete_reason = jsonTemp.getString("delete_reason");
                }

                reportData.add(tempRD);
            }
        } catch (Throwable t) {
            Log.e("parseReports()-0", "Could not parse malformed JSON");
            t.printStackTrace();
        }
    }
}
