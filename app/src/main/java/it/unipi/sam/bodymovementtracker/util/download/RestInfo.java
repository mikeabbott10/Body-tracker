package it.unipi.sam.bodymovementtracker.util.download;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import it.unipi.sam.bodymovementtracker.util.Constants;

public class RestInfo implements Parcelable {
    private String trainingsUrl;
    private Map<String, String> keyWords;
    private Map<String, Map<String, Object>> lastModified;

    protected RestInfo(){}

    protected RestInfo(Parcel in) {
        trainingsUrl = in.readString();

        final int keyWordsMapLength = in.readInt(); // size of keywords map
        keyWords = new HashMap<>();
        for (int j = 0; j < keyWordsMapLength; j++) {
            keyWords.put(in.readString(), in.readString());
        }

        final int lastModifiedMapLength = in.readInt(); // size of lastModified map
        lastModified = new HashMap<>();
        for (int i = 0; i < lastModifiedMapLength; i++) {
            final String key = in.readString();
            Map<String, Object> ithMap = new HashMap<>();
            final int ithMapSize = in.readInt(); // size of i-th map
            for (int j = 0; j < ithMapSize; j++) {
                final String internalMapKey = in.readString();
                final int internalMapValueType = in.readInt();
                if(internalMapValueType == Constants.TIMESTAMP)
                    ithMap.put(internalMapKey, in.readLong());
                else if(internalMapValueType == Constants.PATH)
                    ithMap.put(internalMapKey, in.readString());
            }
            lastModified.put(key, ithMap);
        }
    }

    public static final Creator<RestInfo> CREATOR = new Creator<RestInfo>() {
        @Override
        public RestInfo createFromParcel(Parcel in) {
            return new RestInfo(in);
        }

        @Override
        public RestInfo[] newArray(int size) {
            return new RestInfo[size];
        }
    };

    public String getTrainingsUrl() {
        return trainingsUrl;
    }
    public void setTrainingsUrl(String trainingsUrl) {
        this.trainingsUrl = trainingsUrl;
    }

    public Map<String, String> getKeyWords() {
        return keyWords;
    }
    public void setKeyWords(Map<String, String> keyWords) {
        this.keyWords = keyWords;
    }

    public Map<String, Map<String, Object>> getLastModified() {
        return lastModified;
    }
    public void setLastModified(Map<String, Map<String, Object>> lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trainingsUrl);

        parcel.writeInt(keyWords.size()); // size of keyWords map
        for (Map.Entry<String, String> entry : keyWords.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }

        parcel.writeInt(lastModified.size()); // size of lastModified map
        for (Map.Entry<String, Map<String,Object>> entry : lastModified.entrySet()) {
            parcel.writeString(entry.getKey());
            Map<String,Object> ithMap = entry.getValue();
            parcel.writeInt(ithMap.size()); // size of i-th map
            for (Map.Entry<String, Object> en : ithMap.entrySet()) {
                parcel.writeString(en.getKey());
                if(en.getValue() instanceof String){
                    parcel.writeInt(Constants.PATH);
                    parcel.writeString((String) en.getValue());
                }else if(en.getValue() instanceof Long){
                    parcel.writeInt(Constants.TIMESTAMP);
                    parcel.writeLong((Long) en.getValue());
                }
            }
        }

    }
}
