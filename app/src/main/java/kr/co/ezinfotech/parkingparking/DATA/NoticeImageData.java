package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class NoticeImageData implements Parcelable {

    public String _id;
    public String img_file_name;
    public String display;
    public String start_date;
    public String end_date;
    public String link_url;

    public NoticeImageData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.img_file_name);
        dest.writeString(this.display);
        dest.writeString(this.start_date);
        dest.writeString(this.end_date);
        dest.writeString(this.link_url);
    }

    protected NoticeImageData(Parcel in) {
        this._id = in.readString();
        this.img_file_name = in.readString();
        this.display = in.readString();
        this.start_date = in.readString();
        this.end_date = in.readString();
        this.link_url = in.readString();
    }

    public static final Creator<NoticeImageData> CREATOR = new Creator<NoticeImageData>() {
        @Override
        public NoticeImageData createFromParcel(Parcel source) {
            return new NoticeImageData(source);
        }

        @Override
        public NoticeImageData[] newArray(int size) {
            return new NoticeImageData[size];
        }
    };
}
