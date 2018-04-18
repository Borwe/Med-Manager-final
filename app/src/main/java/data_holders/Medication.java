package data_holders;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity(tableName = "medication")
public class Medication implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int medication_id;

    @ColumnInfo(name = "medication_name")
    private String medication_name;

    @ColumnInfo(name = "medication_description")
    private String medication_description;

    @ColumnInfo(name = "medication_start_date")
    private String medication_start_date;

    @ColumnInfo(name="medication_pills")
    private int medication_pills_count;

    @ColumnInfo(name = "medication_end_date")
    private String medication_end_date;

    @ColumnInfo(name = "medication_time_morning")
    private boolean medication_time_morning;

    @ColumnInfo(name = "medication_time_lunch")
    private boolean medication_time_lunch;

    @ColumnInfo(name = "medication_time_evening")
    private boolean medication_time_evening;

    @ColumnInfo(name = "medication_id_list")
    private String medication_id_list;

    public Medication(String medication_name, String medication_description,int medication_pills_count,
                      String medication_start_date, String medication_end_date,
                      boolean medication_time_morning, boolean medication_time_lunch,
                      boolean medication_time_evening) {
        this.medication_name = medication_name;
        this.medication_description = medication_description;
        this.medication_start_date = medication_start_date;
        this.medication_end_date = medication_end_date;
        this.medication_time_morning = medication_time_morning;
        this.medication_time_lunch = medication_time_lunch;
        this.medication_time_evening = medication_time_evening;
        this.medication_pills_count=medication_pills_count;
    }

    //fake functions, no purpose, just for ROOM TO WORK
    public String getMedication_id_list() {
        return medication_id_list;
    }

    //fake functions, no purpose, just for ROOM TO WORK
    public void setMedication_id_list(String medication_id_list) {
        this.medication_id_list = medication_id_list;
    }

    //1,2,3,4,......, how a list should look like, transform it to an set of ints
    public List<Long> getMedication_id_long_list() {
        List<Long> med_ints=new ArrayList<>();
        for(String s:medication_id_list.split(",")){
            med_ints.add(Long.parseLong(s));
        }

        return med_ints;
    }

    //transform medication ints into string
    public void setMedication_id_list(List<Long> medication_id_list) {
        String list="";
        //tr
        for(long med_id:medication_id_list){
            list+=med_id+",";
        }
        this.medication_id_list = list;
    }

    public int getMedication_id() {
        return medication_id;
    }

    public void setMedication_id(int medication_id) {
        this.medication_id = medication_id;
    }

    public String getMedication_name() {
        return medication_name;
    }

    public void setMedication_name(String medication_name) {
        this.medication_name = medication_name;
    }

    public String getMedication_description() {
        return medication_description;
    }

    public void setMedication_description(String medication_description) {
        this.medication_description = medication_description;
    }

    public String getMedication_start_date() {
        return medication_start_date;
    }

    public void setMedication_start_date(String medication_start_date) {
        this.medication_start_date = medication_start_date;
    }

    public String getMedication_end_date() {
        return medication_end_date;
    }

    public void setMedication_end_date(String medication_end_date) {
        this.medication_end_date = medication_end_date;
    }

    public boolean isMedication_time_morning() {
        return medication_time_morning;
    }

    public void setMedication_time_morning(boolean medication_time_morning) {
        this.medication_time_morning = medication_time_morning;
    }

    public boolean isMedication_time_lunch() {
        return medication_time_lunch;
    }

    public void setMedication_time_lunch(boolean medication_time_lunch) {
        this.medication_time_lunch = medication_time_lunch;
    }

    public boolean isMedication_time_evening() {
        return medication_time_evening;
    }

    public void setMedication_time_evening(boolean medication_time_evening) {
        this.medication_time_evening = medication_time_evening;
    }

    public int getMedication_pills_count() {
        return medication_pills_count;
    }

    public void setMedication_pills_count(int medication_pills_count) {
        this.medication_pills_count = medication_pills_count;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "medication_id=" + medication_id +
                ", medication_name='" + medication_name + '\'' +
                ", medication_description='" + medication_description + '\'' +
                ", medication_start_date='" + medication_start_date + '\'' +
                ", medication_pills_count=" + medication_pills_count +
                ", medication_end_date='" + medication_end_date + '\'' +
                ", medication_time_morning=" + medication_time_morning +
                ", medication_time_lunch=" + medication_time_lunch +
                ", medication_time_evening=" + medication_time_evening +
                ", medication_id_list='" + medication_id_list + '\'' +
                '}';
    }

    //get Calendar containing starting date of its medication item
    public Calendar getStartingMedicationCalendarDate(){
        Calendar starting_date=Calendar.getInstance();


        //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
        starting_date.set(Integer.parseInt(medication_start_date.split("/")[2]),
                Integer.parseInt(medication_start_date.split("/")[1]) - 1,
                Integer.parseInt(medication_start_date.split("/")[0]), 8, 00, 00);

        return starting_date;
    }

    //getCalendar containing ending date of medication item
    public Calendar getEndingMedicationCalendarDate(){
        Calendar ending_date=Calendar.getInstance();

        //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
        ending_date.set(Integer.parseInt(medication_end_date.split("/")[2]),
                Integer.parseInt(medication_end_date.split("/")[1]) - 1,
                Integer.parseInt(medication_end_date.split("/")[0]), 8, 00, 00);

        return ending_date;
    }
}
