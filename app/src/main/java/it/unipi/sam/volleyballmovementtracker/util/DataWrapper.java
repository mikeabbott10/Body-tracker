package it.unipi.sam.volleyballmovementtracker.util;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "data_table")
public class DataWrapper implements Serializable, Comparable<DataWrapper> {
    @PrimaryKey(autoGenerate=true)
    private int uid;

    private int data;
    private int index;

    public DataWrapper() {}
    @Ignore
    public DataWrapper(int data, int index) {
        this.data = data;
        this.index = index;
    }

    public int getData() {
        return data;
    }
    public void setData(int data) {
        this.data = data;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(DataWrapper dataWrapper) {
        if(dataWrapper==null)
            return 1;
        return index - dataWrapper.getIndex();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
