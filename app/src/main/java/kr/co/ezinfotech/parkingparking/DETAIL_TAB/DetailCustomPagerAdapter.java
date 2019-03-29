package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.POPUP.DetailTransferActivity;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class DetailCustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private PZData pzData = new PZData();

    private String[] tabTitles = new String[]{"상세", "요금", "운영"};

    public DetailCustomPagerAdapter(Context context, PZData pzDataVal) {
        mContext = context;
        pzData = pzDataVal;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout = null;

        if(0 == position) {
            BasicModelObject basicModelObject = BasicModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(basicModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);

            if(pzData.name.equals("null")) {
                ((TextView) layout.findViewById(R.id.textViewParkingTitleLabel)).setText("미등록");
            } else {
                ((TextView) layout.findViewById(R.id.textViewParkingTitleLabel)).setText(pzData.name);
            }

            if(pzData.addr_road.equals("null")) {
                ((TextView) layout.findViewById(R.id.textViewParkingAddr)).setText("미등록");
            } else {
                ((TextView) layout.findViewById(R.id.textViewParkingAddr)).setText(UtilManager.cutTheString(pzData.addr_road, 16));
            }
            if(pzData.tel.equals("null")) {
                ((TextView)layout.findViewById(R.id.textViewParkingTel)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.textViewParkingTel)).setText(pzData.tel);
            }
            if(pzData.homepage.equals("null")) {
                ((TextView)layout.findViewById(R.id.textViewParkingHomepage)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.textViewParkingHomepage)).setText(pzData.homepage);
            }

            // 주차면수
            if(pzData.park_space_count.small.equals("null")) {
                ((TextView)layout.findViewById(R.id.tvTableSmallCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTableSmallCont)).setText(pzData.park_space_count.small);
            }
            if(pzData.park_space_count.mid.equals("null")) {
                ((TextView)layout.findViewById(R.id.tvTableMiddleCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTableMiddleCont)).setText(pzData.park_space_count.mid);
            }
            if(pzData.park_space_count.big.equals("null")) {
                ((TextView)layout.findViewById(R.id.tvTableBigCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTableBigCont)).setText(pzData.park_space_count.big);
            }
            if(pzData.park_space_count.elec.equals("null")) {
                ((TextView)layout.findViewById(R.id.tvTableElecCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTableElecCont)).setText(pzData.park_space_count.elec);
            }
            if(pzData.park_space_count.hand.equals("null")) {
                ((TextView)layout.findViewById(R.id.tvTableHandCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTableHandCont)).setText(pzData.park_space_count.hand);
            }

            // Set click event - 전달
            LinearLayout transferLL = (LinearLayout) layout.findViewById(R.id.transferLL);
            transferLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "transferLL-OnClickListener:" + pzData.name + ", " + pzData.addr_road, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, DetailTransferActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                    intent.putExtra("name", pzData.name);
                    intent.putExtra("addr", pzData.addr_road);
                    mContext.startActivity(intent);
                }
            });

            // 로드뷰
            LinearLayout roadViewLL = (LinearLayout) layout.findViewById(R.id.roadViewLL);
            roadViewLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "roadViewLL-OnClickListener:" + pzData.loc.getLatitude() + ", " + pzData.loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    // http://gun0912.tistory.com/13
                    String url ="daummaps://roadView?p=" + pzData.loc.getLatitude() + "," + pzData.loc.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, "로드뷰기능을 이용하시기 위해서는 카카오맵을 설치해야 합니다", Toast.LENGTH_LONG).show();
                        // String url2 = "daummaps://storeview?id=659";
                        String url2 = "market://details?id=net.daum.android.map";
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                        mContext.startActivity(intent2);
                    }
                }
            });

            // 내비
            LinearLayout naviLL = (LinearLayout) layout.findViewById(R.id.naviLL);
            naviLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "naviLL-OnClickListener:" + pzData.loc.getLatitude() + ", " + pzData.loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    if (TmapManager.isTmapInstalled()) {
                        TmapManager.showRoute(pzData.name, (float)pzData.loc.getLatitude(), (float)pzData.loc.getLongitude());
                    } else {
                        Toast.makeText(mContext, "네비기능을 이용하시기 위해서는 T맵을 설치해야 합니다", Toast.LENGTH_LONG).show();
                        ArrayList tmapDownUrl = TmapManager.getTmapDownUrl();
                        Uri uri = Uri.parse(tmapDownUrl.get(0).toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if(1 == position) {
            FeeModelObject feeModelObject = FeeModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(feeModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            ((TextView)layout.findViewById(R.id.tvTblBaseTimeFee)).setText(pzData.park_base.time + "분 " + pzData.park_base.fee + "원");
            ((TextView)layout.findViewById(R.id.tvTblAddTimeFee)).setText(pzData.add_term.time + "분당 " + pzData.add_term.fee + "원");

            if(pzData.one_day_park.fee.equals("null")) {
                ((TextView) layout.findViewById(R.id.tvDayPKBaseContent)).setText("미등록");
            } else {
                ((TextView) layout.findViewById(R.id.tvDayPKBaseContent)).setText(pzData.one_day_park.fee + "원");
            }
            if(pzData.month_fee.equals("null")) {
                ((TextView) layout.findViewById(R.id.tvMonthPKBaseContent)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvMonthPKBaseContent)).setText(pzData.month_fee + "원");
            }
            if(pzData.sale_info.equals("null")) {
                ((TextView) layout.findViewById(R.id.tvTblSaleCont)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.tvTblSaleCont)).setText(pzData.sale_info);
            }
        } else if(2 == position) {
            OpModelObject opModelObject = OpModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(opModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);

            if(pzData.fee_info.equals("무료")) {
                ((TextView)layout.findViewById(R.id.tvTblWopTime)).setText("해당없음");
                ((TextView)layout.findViewById(R.id.tvTblSopTime)).setText("해당없음");
                ((TextView)layout.findViewById(R.id.tvTblHopTime)).setText("해당없음");

                ((TextView)layout.findViewById(R.id.tvTblWopFreeTime)).setText("00:00 ~ 00:00");
                ((TextView)layout.findViewById(R.id.tvTblSopFreeTime)).setText("00:00 ~ 00:00");
                ((TextView)layout.findViewById(R.id.tvTblHopFreeTime)).setText("00:00 ~ 00:00");
            } else {
                ((TextView)layout.findViewById(R.id.tvTblWopTime)).setText(pzData.w_op.start_time + " ~ " + pzData.w_op.end_time);
                ((TextView)layout.findViewById(R.id.tvTblSopTime)).setText(pzData.s_op.start_time + " ~ " + pzData.s_op.end_time);
                ((TextView)layout.findViewById(R.id.tvTblHopTime)).setText(pzData.h_op.start_time + " ~ " + pzData.h_op.end_time);

                ((TextView)layout.findViewById(R.id.tvTblWopFreeTime)).setText(pzData.w_op.end_time + " ~ 익일 " + pzData.w_op.start_time);
                ((TextView)layout.findViewById(R.id.tvTblSopFreeTime)).setText(pzData.s_op.end_time + " ~ 익일 " + pzData.s_op.start_time);
                ((TextView)layout.findViewById(R.id.tvTblHopFreeTime)).setText(pzData.h_op.end_time + " ~ 익일 " + pzData.h_op.start_time);
            }
        }

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        // return BasicModelObject.values().length;
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*
        BasicModelObject customPagerEnum = BasicModelObject.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
        */

        // return null;
        return tabTitles[position];
    }

}
