package kr.co.ezinfotech.parkingparking.DATA;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class PZData implements Parcelable {

    public String no;
    public String name;
    public String division;
    public String type;
    public String addr_road;
    public String addr_jibun;
    public String total_p;
    public String feed;
    public String buje;
    public String op_date;

    public PZTermData w_op;
    public PZTermData s_op;
    public PZTermData h_op;

    public String fee_info;

    public PZTFData park_base;
    public PZTFData add_term;
    public PZTFData one_day_park;

    public String month_fee;
    public String payment;
    public String remarks;
    public String manager;
    public String tel;
    public Location loc;       // 위,경도
    public String data_date;

    public PZData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.no);
        dest.writeString(this.name);
        dest.writeString(this.division);
        dest.writeString(this.type);
        dest.writeString(this.addr_road);
        dest.writeString(this.addr_jibun);
        dest.writeString(this.total_p);
        dest.writeString(this.feed);
        dest.writeString(this.buje);
        dest.writeString(this.op_date);

        dest.writeString(this.w_op.start_date);
        dest.writeString(this.w_op.end_date);
        dest.writeString(this.s_op.start_date);
        dest.writeString(this.s_op.end_date);
        dest.writeString(this.h_op.start_date);
        dest.writeString(this.h_op.end_date);

        dest.writeString(this.fee_info);

        dest.writeString(this.park_base.time);
        dest.writeString(this.park_base.fee);
        dest.writeString(this.add_term.time);
        dest.writeString(this.add_term.fee);
        dest.writeString(this.one_day_park.time);
        dest.writeString(this.one_day_park.fee);

        dest.writeString(this.month_fee);
        dest.writeString(this.payment);
        dest.writeString(this.remarks);
        dest.writeString(this.manager);
        dest.writeString(this.tel);
        dest.writeParcelable(this.loc, flags);
        dest.writeString(this.data_date);
    }

    protected PZData(Parcel in) {

        this.no = in.readString();
        this.name = in.readString();
        this.division = in.readString();
        this.type = in.readString();
        this.addr_road = in.readString();
        this.addr_jibun = in.readString();
        this.total_p = in.readString();
        this.feed = in.readString();
        this.buje = in.readString();
        this.op_date = in.readString();

        this.w_op = new PZTermData();
        this.w_op.start_date = in.readString();
        this.w_op.end_date = in.readString();
        this.s_op = new PZTermData();
        this.s_op.start_date = in.readString();
        this.s_op.start_date = in.readString();
        this.h_op = new PZTermData();
        this.h_op.start_date = in.readString();
        this.h_op.start_date = in.readString();

        this.fee_info = in.readString();

        this.park_base = new PZTFData();
        this.park_base.time = in.readString();
        this.park_base.fee = in.readString();
        this.add_term = new PZTFData();
        this.add_term.time = in.readString();
        this.add_term.fee = in.readString();
        this.one_day_park = new PZTFData();
        this.one_day_park.time = in.readString();
        this.one_day_park.fee = in.readString();

        this.month_fee = in.readString();
        this.payment = in.readString();
        this.remarks = in.readString();
        this.manager = in.readString();
        this.tel = in.readString();
        this.loc = in.readParcelable(Location.class.getClassLoader());
        this.data_date = in.readString();
    }

    public static final Parcelable.Creator<PZData> CREATOR = new Parcelable.Creator<PZData>() {
        @Override
        public PZData createFromParcel(Parcel source) {
            return new PZData(source);
        }

        @Override
        public PZData[] newArray(int size) {
            return new PZData[size];
        }
    };
}
