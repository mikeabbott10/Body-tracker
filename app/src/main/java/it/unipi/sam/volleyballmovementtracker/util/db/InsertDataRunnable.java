package it.unipi.sam.volleyballmovementtracker.util.db;

import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;

public class InsertDataRunnable implements Runnable {
    private final AppDatabase mDb;
    private DataWrapper dataWrapper;

    public InsertDataRunnable(AppDatabase db, DataWrapper dataWrapper) {
        this.mDb = db;
        this.dataWrapper = dataWrapper;
    }

    @Override
    public void run() {
        // inserisco dati
        mDb.dataDAO().insertAll(dataWrapper);
    }
}