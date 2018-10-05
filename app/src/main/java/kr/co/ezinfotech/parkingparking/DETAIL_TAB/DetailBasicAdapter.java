package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.R;

public class DetailBasicAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private DetailBasicData m_oData = null;
    private int nListCnt = 0;

    public DetailBasicAdapter(DetailBasicData _oData)
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
            convertView = inflater.inflate(R.layout.detail_basic_item, parent, false);
        }

        TextView oTextAddress = (TextView) convertView.findViewById(R.id.textViewParkingAddr);
        TextView oTextTel = (TextView) convertView.findViewById(R.id.textViewParkingTel);

        oTextAddress.setText(m_oData.strAddress);
        oTextTel.setText(m_oData.strTel);

        return convertView;
    }
}
