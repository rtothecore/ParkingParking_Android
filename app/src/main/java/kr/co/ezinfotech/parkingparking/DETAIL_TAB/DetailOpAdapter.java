package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.R;

public class DetailOpAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private DetailOpData m_oData = null;
    private int nListCnt = 0;

    public DetailOpAdapter(DetailOpData _oData)
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
            convertView = inflater.inflate(R.layout.detail_op_item, parent, false);
        }

        TextView oTextWStartTime = (TextView) convertView.findViewById(R.id.textViewParkingWopWStartTime);
        TextView oTextWEndTime = (TextView) convertView.findViewById(R.id.textViewParkingWopWEndTime);
        TextView oTextSStartTime = (TextView) convertView.findViewById(R.id.textViewParkingWopSStartTime);
        TextView oTextSEndTime = (TextView) convertView.findViewById(R.id.textViewParkingWopSEndTime);
        TextView oTextHStartTime = (TextView) convertView.findViewById(R.id.textViewParkingWopHStartTime);
        TextView oTextHEndTime = (TextView) convertView.findViewById(R.id.textViewParkingWopHEndTime);

        oTextWStartTime.setText(m_oData.strWopStartTime);
        oTextWEndTime.setText(m_oData.strWopEndTime);
        oTextSStartTime.setText(m_oData.strSopStartTime);
        oTextSEndTime.setText(m_oData.strSopEndTime);
        oTextHStartTime.setText(m_oData.strHopStartTime);
        oTextHEndTime.setText(m_oData.strHopEndTime);

        return convertView;
    }
}
