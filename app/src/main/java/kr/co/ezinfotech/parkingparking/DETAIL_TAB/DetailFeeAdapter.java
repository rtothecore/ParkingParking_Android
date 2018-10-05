package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.R;

public class DetailFeeAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private DetailFeeData m_oData = null;
    private int nListCnt = 0;

    public DetailFeeAdapter(DetailFeeData _oData)
    {
        m_oData = _oData;
        nListCnt = 1;
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.detail_fee_item, parent, false);
        }

        TextView oTextBaseTime = (TextView) convertView.findViewById(R.id.textViewParkingBaseTime);
        TextView oTextBaseFee = (TextView) convertView.findViewById(R.id.textViewParkingBaseFee);
        TextView oTextAddTermTime = (TextView) convertView.findViewById(R.id.textViewParkingAddTermTime);
        TextView oTextAddTermFee = (TextView) convertView.findViewById(R.id.textViewParkingAddTermFee);

        oTextBaseTime.setText(m_oData.strBaseTime);
        oTextBaseFee.setText(m_oData.strBaseFee);
        oTextAddTermTime.setText(m_oData.strTermTime);
        oTextAddTermFee.setText(m_oData.strTermFee);

        return convertView;
    }
}
