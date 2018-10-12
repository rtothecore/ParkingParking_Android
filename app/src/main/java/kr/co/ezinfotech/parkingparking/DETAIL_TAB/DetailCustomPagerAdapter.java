package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

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

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.POPUP.DetailTransferActivity;
import kr.co.ezinfotech.parkingparking.R;

public class DetailCustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private PZData pzData = new PZData();

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

            if(pzData.addr_road.equals("null")) {
                ((TextView) layout.findViewById(R.id.textViewParkingAddr)).setText("미등록");
            } else {
                ((TextView) layout.findViewById(R.id.textViewParkingAddr)).setText(pzData.addr_road);
            }
            if(pzData.tel.equals("null")) {
                ((TextView)layout.findViewById(R.id.textViewParkingTel)).setText("미등록");
            } else {
                ((TextView)layout.findViewById(R.id.textViewParkingTel)).setText(pzData.tel);
            }

            // Set click event - 전달
            LinearLayout transferLL = (LinearLayout) layout.findViewById(R.id.transferLL);
            transferLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "transferLL-OnClickListener:" + pzData.name + ", " + pzData.addr_road, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, DetailTransferActivity.class);
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
                    mContext.startActivity(intent);
                }
            });

            // 내비
            LinearLayout naviLL = (LinearLayout) layout.findViewById(R.id.naviLL);
            naviLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "naviLL-OnClickListener:" + pzData.loc.getLatitude() + ", " + pzData.loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    TmapManager.showRoute(pzData.name, (float)pzData.loc.getLatitude(), (float)pzData.loc.getLongitude());
                }
            });
        } else if(1 == position) {
            FeeModelObject feeModelObject = FeeModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(feeModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            ((TextView)layout.findViewById(R.id.tvTblBaseTimeFee)).setText(pzData.park_base.time + "분 " + pzData.park_base.fee + "원");
            ((TextView)layout.findViewById(R.id.tvTblAddTimeFee)).setText(pzData.add_term.time + "분당 " + pzData.add_term.fee + "원");
        } else if(2 == position) {
            OpModelObject opModelObject = OpModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(opModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            /*
            ((TextView)layout.findViewById(R.id.textViewParkingWopWStartTime)).setText(pzData.w_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopWEndTime)).setText(pzData.w_op.end_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopSStartTime)).setText(pzData.s_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopSEndTime)).setText(pzData.s_op.end_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopHStartTime)).setText(pzData.h_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopHEndTime)).setText(pzData.h_op.end_date);
            */
            ((TextView)layout.findViewById(R.id.tvTblWopTime)).setText(pzData.w_op.start_date + " ~ " + pzData.w_op.end_date);
            ((TextView)layout.findViewById(R.id.tvTblSopTime)).setText(pzData.s_op.start_date + " ~ " + pzData.s_op.end_date);
            ((TextView)layout.findViewById(R.id.tvTblHopTime)).setText(pzData.h_op.start_date + " ~ " + pzData.h_op.end_date);
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
        return null;
    }

}
