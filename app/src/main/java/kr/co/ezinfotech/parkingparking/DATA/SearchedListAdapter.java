package kr.co.ezinfotech.parkingparking.DATA;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.R;

public class SearchedListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<SearchedItemData> m_oData = null;
    private int nListCnt = 0;

    public SearchedListAdapter(ArrayList<SearchedItemData> _oData)
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
            convertView = inflater.inflate(R.layout.searched_item, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textKeyword);
        TextView oTextDate = (TextView) convertView.findViewById(R.id.textAddress);

        oTextTitle.setText(m_oData.get(position).strKeyword);
        oTextDate.setText(m_oData.get(position).strAddress);
        return convertView;
    }
}
