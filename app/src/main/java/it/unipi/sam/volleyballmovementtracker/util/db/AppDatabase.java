package it.unipi.sam.volleyballmovementtracker.util.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;

@Database(entities = {DataWrapper.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract DataDAO dataDAO();

    // singleton pattern
    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance =
                    Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME)
                            .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}