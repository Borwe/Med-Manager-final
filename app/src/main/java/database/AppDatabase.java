package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import data_holders.Medication;

@Database(entities = {Medication.class},version = 3)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public abstract MedicationDAO medicationDAO();

    public static synchronized AppDatabase getAppDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context,AppDatabase.class,"medication_database")
                    .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public static void destroyAppDatabase(){
        INSTANCE=null;
    }
}
