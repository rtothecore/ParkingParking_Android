package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
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
            ((TextView)layout.findViewById(R.id.textViewParkingAddr)).setText(pzData.addr_road);
            ((TextView)layout.findViewById(R.id.textViewParkingTel)).setText(pzData.tel);
        } else if(1 == position) {
            FeeModelObject feeModelObject = FeeModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(feeModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            ((TextView)layout.findViewById(R.id.textViewParkingBaseTime)).setText(pzData.park_base.time + "분 ");
            ((TextView)layout.findViewById(R.id.textViewParkingBaseFee)).setText(pzData.park_base.fee + "원");
            ((TextView)layout.findViewById(R.id.textViewParkingAddTermTime)).setText(pzData.add_term.time + "분 당 ");
            ((TextView)layout.findViewById(R.id.textViewParkingAddTermFee)).setText(pzData.add_term.fee + "원");
        } else if(2 == position) {
            OpModelObject opModelObject = OpModelObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(opModelObject.getLayoutResId(), collection, false);
            collection.addView(layout);
            ((TextView)layout.findViewById(R.id.textViewParkingWopWStartTime)).setText(pzData.w_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopWEndTime)).setText(pzData.w_op.end_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopSStartTime)).setText(pzData.s_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopSEndTime)).setText(pzData.s_op.end_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopHStartTime)).setText(pzData.h_op.start_date);
            ((TextView)layout.findViewById(R.id.textViewParkingWopHEndTime)).setText(pzData.h_op.end_date);
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
