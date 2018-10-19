package kr.co.ezinfotech.parkingparking.SIGN_UP;

import kr.co.ezinfotech.parkingparking.R;

public enum TermsDetailPrivateObject {
    TERMS_TWO(0, R.layout.terms_detail_private);

    private int mTitleResId;
    private int mLayoutResId;

    TermsDetailPrivateObject(int titleResId, int layoutResId) {
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
