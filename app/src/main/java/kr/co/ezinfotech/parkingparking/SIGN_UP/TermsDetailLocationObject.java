package kr.co.ezinfotech.parkingparking.SIGN_UP;

import kr.co.ezinfotech.parkingparking.R;

public enum TermsDetailLocationObject {
    TERMS_THREE(0, R.layout.terms_detail_location);

    private int mTitleResId;
    private int mLayoutResId;

    TermsDetailLocationObject(int titleResId, int layoutResId) {
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
