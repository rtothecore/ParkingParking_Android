package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import kr.co.ezinfotech.parkingparking.R;

public enum FeeModelObject {
    FEE(0, R.layout.detail_fee_item);

    private int mTitleResId;
    private int mLayoutResId;

    FeeModelObject(int titleResId, int layoutResId) {
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
