package kr.co.ezinfotech.parkingparking.LIST_TAB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DATA.SearchedItemData;
import kr.co.ezinfotech.parkingparking.R;

public class DistanceOrderListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<ListItemData> m_oData = null;
    private int nListCnt = 0;

    public DistanceOrderListAdapter(ArrayList<ListItemData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
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
            convertView = inflater.inflate(R.layout.distance_order_list_item, parent, false);
        }

        TextView oTextName = (TextView) convertView.findViewById(R.id.DOListName);
        TextView oTextAddress = (TextView) convertView.findViewById(R.id.DOListAddress);
        TextView oTextTel = (TextView) convertView.findViewById(R.id.DOListTel);
        TextView oTextDistance = (TextView) convertView.findViewById(R.id.DOListDistance);

        oTextName.setText(m_oData.get(position).strName);
        oTextAddress.setText(m_oData.get(position).strAddress);
        oTextTel.setText(m_oData.get(position).strTel);
        oTextDistance.setText(m_oData.get(position).strDistance);

        return convertView;
    }
}
