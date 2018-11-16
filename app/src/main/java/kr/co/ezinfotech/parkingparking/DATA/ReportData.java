package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class ReportData implements Parcelable {

    public String code;
    public String report_date;
    public String user_email;
    public String user_phone_no;
    public String parking_name;
    public String parking_lat;
    public String parking_lng;
    public String parking_tel;
    public String parking_fee_info;
    public String parking_etc_info;
    public String parking_pictureA;
    public String parking_pictureB;
    public String parking_pictureC;
    public String status;
    public String hold_reason;
    public String delete_status;
    public String delete_reason;

    public ReportData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.report_date);
        dest.writeString(this.user_email);
        dest.writeString(this.user_phone_no);
        dest.writeString(this.parking_name);
        dest.writeString(this.parking_lat);
        dest.writeString(this.parking_lng);
        dest.writeString(this.parking_tel);
        dest.writeString(this.parking_fee_info);
        dest.writeString(this.parking_etc_info);
        dest.writeString(this.parking_pictureA);
        dest.writeString(this.parking_pictureB);
        dest.writeString(this.parking_pictureC);
        dest.writeString(this.status);
        dest.writeString(this.hold_reason);
        dest.writeString(this.delete_status);
        dest.writeString(this.delete_reason);
    }

    protected ReportData(Parcel in) {
        this.code = in.readString();
        this.report_date = in.readString();
        this.user_email = in.readString();
        this.user_phone_no = in.readString();
        this.parking_name = in.readString();
        this.parking_lat = in.readString();
        this.parking_lng = in.readString();
        this.parking_tel = in.readString();
        this.parking_fee_info = in.readString();
        this.parking_etc_info = in.readString();
        this.parking_pictureA = in.readString();
        this.parking_pictureB = in.readString();
        this.parking_pictureC = in.readString();
        this.status = in.readString();
        this.hold_reason = in.readString();
        this.delete_status = in.readString();
        this.delete_reason = in.readString();
    }

    public static final Creator<ReportData> CREATOR = new Creator<ReportData>() {
        @Override
        public ReportData createFromParcel(Parcel source) {
            return new ReportData(source);
        }

        @Override
        public ReportData[] newArray(int size) {
            return new ReportData[size];
        }
    };
}
