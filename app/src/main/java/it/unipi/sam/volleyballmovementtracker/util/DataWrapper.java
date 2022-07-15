package it.unipi.sam.volleyballmovementtracker.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "data_table")
public class DataWrapper implements Serializable, Comparable<DataWrapper>, Parcelable {
    @PrimaryKey(autoGenerate=true)
    private int uid;

    private double data;
    private long timestamp;

    public DataWrapper() {}
    @Ignore
    public DataWrapper(double data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    protected DataWrapper(Parcel in) {
        uid = in.readInt();
        data = in.readDouble();
        timestamp = in.readLong();
    }

    public static final Creator<DataWrapper> CREATOR = new Creator<DataWrapper>() {
        @Override
        public DataWrapper createFromParcel(Parcel in) {
            return new DataWrapper(in);
        }

        @Override
        public DataWrapper[] newArray(int size) {
            return new DataWrapper[size];
        }
    };

    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getData() {
        return data;
    }
    public void setData(double data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(DataWrapper dataWrapper) {
        if(dataWrapper==null)
            return 1;
        return (int) (timestamp - dataWrapper.getTimestamp());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeDouble(data);
        parcel.writeLong(timestamp);
    }
}
