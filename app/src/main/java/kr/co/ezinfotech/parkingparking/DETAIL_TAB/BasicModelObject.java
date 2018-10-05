package kr.co.ezinfotech.parkingparking.DETAIL_TAB;

import kr.co.ezinfotech.parkingparking.R;

public enum BasicModelObject {

    /*
    BASIC(0, R.layout.detail_basic_item),
    FEE(1, R.layout.detail_fee_item),
    OP(2, R.layout.detail_op_item);
    */
    BASIC(0, R.layout.detail_basic_item);

    private int mTitleResId;
    private int mLayoutResId;

    BasicModelObject(int titleResId, int layoutResId) {
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
