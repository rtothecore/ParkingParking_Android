package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class UserData implements Parcelable {

    public String name;
    public String email;
    public String password;
    public String phone_no;
    public String car_no;
    public String car_type;
    public String level;
    public String tmp_pw_date;
    public String join_date;
    public String mod_date;
    public String pw_date;

    public UserData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.phone_no);
        dest.writeString(this.car_no);
        dest.writeString(this.car_type);
        dest.writeString(this.level);
        dest.writeString(this.tmp_pw_date);
        dest.writeString(this.join_date);
        dest.writeString(this.mod_date);
        dest.writeString(this.pw_date);

    }

    protected UserData(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.phone_no = in.readString();
        this.car_no = in.readString();
        this.car_type = in.readString();
        this.level = in.readString();
        this.tmp_pw_date = in.readString();
        this.join_date = in.readString();
        this.mod_date = in.readString();
        this.pw_date = in.readString();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel source) {
            return new UserData(source);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
}
