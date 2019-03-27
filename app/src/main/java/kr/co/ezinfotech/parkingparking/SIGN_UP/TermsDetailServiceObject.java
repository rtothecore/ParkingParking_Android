package kr.co.ezinfotech.parkingparking.SIGN_UP;

import kr.co.ezinfotech.parkingparking.R;

public enum TermsDetailServiceObject {
    TERMS_ONE(0, R.layout.terms_detail_service);

    private int mTitleResId;
    private int mLayoutResId;

    TermsDetailServiceObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}