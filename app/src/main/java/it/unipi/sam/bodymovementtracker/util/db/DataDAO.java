package it.unipi.sam.bodymovementtracker.util.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.unipi.sam.bodymovementtracker.util.DataWrapper;

@Dao
public interface DataDAO {
    @Insert
    void insertAll(DataWrapper... dataWrappers);

    /*//custom delete
    @Query("DELETE FROM data_table WHERE id = :favoritesWrapperId")
    void delete(int favoritesWrapperId);*/

    @Query("DELETE FROM data_table WHERE timestamp < :mTimestamp")
    int deleteBefore(long mTimestamp);

    @Query("DELETE FROM data_table")
    void deleteAll();

    @Query("SELECT * FROM data_table ORDER BY timestamp DESC LIMIT 1")
    LiveData<List<DataWrapper>> getLast();

    @Query("SELECT * FROM data_table ORDER BY timestamp DESC")
    LiveData<List<DataWrapper>> getAll();

}
