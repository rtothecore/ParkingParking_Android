package kr.co.ezinfotech.parkingparking.DATA;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class SmsAuthData implements Parcelable {

    public String phone_no;
    public String auth_code;
    public String auth_date;

    public SmsAuthData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone_no);
        dest.writeString(this.auth_code);
        dest.writeString(this.auth_date);
    }

    protected SmsAuthData(Parcel in) {
        this.phone_no = in.readString();
        this.auth_code = in.readString();
        this.auth_date = in.readString();
    }

    public static final Creator<SmsAuthData> CREATOR = new Creator<SmsAuthData>() {
        @Override
        public SmsAuthData createFromParcel(Parcel source) {
            return new SmsAuthData(source);
        }

        @Override
        public SmsAuthData[] newArray(int size) {
            return new SmsAuthData[size];
        }
    };
}
