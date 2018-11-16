package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class FavoriteData implements Parcelable {

    public String user_email;
    public String parking_no;
    public String date;

    public FavoriteData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.user_email);
        dest.writeString(this.parking_no);
        dest.writeString(this.date);
    }

    protected FavoriteData(Parcel in) {
        this.user_email = in.readString();
        this.parking_no = in.readString();
        this.date = in.readString();
    }

    public static final Creator<FavoriteData> CREATOR = new Creator<FavoriteData>() {
        @Override
        public FavoriteData createFromParcel(Parcel source) {
            return new FavoriteData(source);
        }

        @Override
        public FavoriteData[] newArray(int size) {
            return new FavoriteData[size];
        }
    };
}
