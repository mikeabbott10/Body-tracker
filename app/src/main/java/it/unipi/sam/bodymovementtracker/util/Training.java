package it.unipi.sam.bodymovementtracker.util;

import android.os.Parcel;
import android.os.Parcelable;

public class Training implements Parcelable, Comparable<Training>{
    private String name;
    private String category;
    private String code;
    private int localImageCode;

    public Training(){}

    protected Training(Parcel in) {
        name = in.readString();
        category = in.readString();
        code = in.readString();
        localImageCode = in.readInt();
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLocalImageCode() {
        return localImageCode;
    }

    public void setLocalImageCode(int localImageCode) {
        this.localImageCode = localImageCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(category);
        parcel.writeString(code);
        parcel.writeInt(localImageCode);
    }

    @Override
    public int compareTo(Training training) {
        return this.name.compareTo(training.getName());
    }
}
