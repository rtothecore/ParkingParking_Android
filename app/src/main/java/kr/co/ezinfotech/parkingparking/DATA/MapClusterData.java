package kr.co.ezinfotech.parkingparking.DATA;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkim on 2018-04-04.
 */

public class MapClusterData implements Parcelable {

    public String no;
    public String lat;
    public String lng;
    public String radius;
    public String clusterCount;

    public MapClusterData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.no);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.radius);
        dest.writeString(this.clusterCount);
    }

    protected MapClusterData(Parcel in) {
        this.no = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.radius = in.readString();
        this.clusterCount = in.readString();
    }

    public static final Creator<MapClusterData> CREATOR = new Creator<MapClusterData>() {
        @Override
        public MapClusterData createFromParcel(Parcel source) {
            return new MapClusterData(source);
        }

        @Override
        public MapClusterData[] newArray(int size) {
            return new MapClusterData[size];
        }
    };
}
