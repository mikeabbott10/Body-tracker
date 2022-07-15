package it.unipi.sam.volleyballmovementtracker.util.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;

@Dao
public interface DataDAO {
    @Insert
    void insertAll(DataWrapper... dataWrappers);

    /*//custom delete
    @Query("DELETE FROM data_table WHERE id = :favoritesWrapperId")
    void delete(int favoritesWrapperId);*/
    @Delete
    Integer delete(DataWrapper favoritesWrapper);

    @Query("DELETE FROM data_table")
    void deleteAll();

    @Query("SELECT * FROM data_table")
    LiveData<List<DataWrapper>> getAll();

}
