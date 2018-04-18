package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import data_holders.Medication;

@Dao
public interface MedicationDAO {

    //get all medications of user
    @Query("SELECT * FROM medication")
    List<Medication> getAll();

    //insert one medication
    @Insert
    void addMedication(Medication medication);

    //delete given medication
    @Delete
    void deleteMedication(Medication medication);
}
