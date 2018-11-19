package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class NoticeData implements Parcelable {

    public String subject;
    public String contents;
    public String date;

    public NoticeData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subject);
        dest.writeString(this.contents);
        dest.writeString(this.date);
    }

    protected NoticeData(Parcel in) {
        this.subject = in.readString();
        this.contents = in.readString();
        this.date = in.readString();
    }

    public static final Creator<NoticeData> CREATOR = new Creator<NoticeData>() {
        @Override
        public NoticeData createFromParcel(Parcel source) {
            return new NoticeData(source);
        }

        @Override
        public NoticeData[] newArray(int size) {
            return new NoticeData[size];
        }
    };
}
