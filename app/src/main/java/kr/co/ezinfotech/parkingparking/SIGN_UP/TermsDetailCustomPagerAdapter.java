package kr.co.ezinfotech.parkingparking.SIGN_UP;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.BasicModelObject;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.FeeModelObject;
import kr.co.ezinfotech.parkingparking.DETAIL_TAB.OpModelObject;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.POPUP.DetailTransferActivity;
import kr.co.ezinfotech.parkingparking.R;

public class TermsDetailCustomPagerAdapter extends PagerAdapter {

    private Context mContext;

    private String[] tabTitles = new String[]{"서비스", "개인정보취급방침", "위치기반서비스"};

    public TermsDetailCustomPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout = null;

        if(0 == position) {
            /*
            TermsDetailServiceObject tdso = TermsDetailServiceObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(tdso.getLayoutResId(), collection, false);
            collection.addView(layout);
            */
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(R.layout.terms_detail_service, collection, false);
            WebView mWebView = layout.findViewById(R.id.wvTermsService);
            mWebView.loadUrl("http://www.ezinfotech.co.kr/ezparking/terms/service.html");
            collection.addView(layout);
        } else if(1 == position) {
            /*
            TermsDetailPrivateObject tdpo = TermsDetailPrivateObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(tdpo.getLayoutResId(), collection, false);
            collection.addView(layout);
            */
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(R.layout.terms_detail_private, collection, false);
            WebView mWebView = layout.findViewById(R.id.wvTermsPrivate);
            mWebView.loadUrl("http://www.ezinfotech.co.kr/ezparking/terms/private.html");
            collection.addView(layout);
        } else if(2 == position) {
            /*
            TermsDetailLocationObject tdlo = TermsDetailLocationObject.values()[0];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(tdlo.getLayoutResId(), collection, false);
            collection.addView(layout);
            */
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(R.layout.terms_detail_location, collection, false);
            WebView mWebView = layout.findViewById(R.id.wvTermsLocation);
            mWebView.loadUrl("http://www.ezinfotech.co.kr/ezparking/terms/location.html");
            collection.addView(layout);
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
        // return null;
        return tabTitles[position];
    }

}
