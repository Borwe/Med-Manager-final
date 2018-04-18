package med.borwe.com.med_manager.dialog_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import constants.ActivityConstants;
import data_holders.Medication;
import med.borwe.com.med_manager.R;

public class MonthView extends AppCompatActivity {

    private CompactCalendarView calendarView;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMMM - YYYY", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);

        calendarView=findViewById(R.id.compactcalendar_view);
        calendarView.setUseThreeLetterAbbreviation(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                MonthView.this.setTitle(simpleDateFormat.format(firstDayOfNewMonth));
            }
        });

        //add all events
        calendarView.addEvents(eventsForMedication());

        MonthView.this.setTitle(simpleDateFormat.format(calendarView.getFirstDayOfCurrentMonth()));
    }

    private List<Event> eventsForMedication(){
        List<Event> medication_events=new ArrayList<>();

        //get medication for activity
        Intent i = MonthView.this.getIntent();
        Medication medication = (Medication) i.getBundleExtra("bundle").getSerializable("medication");

        //get starting and ending date of medication
        Calendar starting_date=medication.getStartingMedicationCalendarDate();
        Calendar ending_date=medication.getEndingMedicationCalendarDate();

        //loop through all dates from starting date to end date
        while(starting_date.after(ending_date)==false){

            //generates events for all days that occur in the morning
            Calendar date_to_us= (Calendar) starting_date.clone();
            if(medication.isMedication_time_morning()==true){
                Event event=generateEventByTimeOfDay(date_to_us,1);
                medication_events.add(event);
            }

            //generates events for all days that occur at lunch
            if(medication.isMedication_time_lunch()==true){
                Event event=generateEventByTimeOfDay(date_to_us,2);
                medication_events.add(event);
            }

            //generates events for all days that occur at supper
            if(medication.isMedication_time_evening()==true){
                Event event=generateEventByTimeOfDay(date_to_us,3);
                medication_events.add(event);
            }

            //rollup start date
            starting_date.roll(Calendar.DAY_OF_YEAR,true);
        }

        return  medication_events;
    }

    //time: 1=morning, 2=lunch, 3=supper
    private Event generateEventByTimeOfDay(Calendar date,int time){
        //set time to 8am if time==morning
        if(time==1){
            date.set(Calendar.HOUR,8);
            date.set(Calendar.MINUTE,0);
        }

        //set time to 1pm if time==lunch
        if(time==2){
            date.set(Calendar.HOUR,13);
            date.set(Calendar.MINUTE,0);
        }

        //set time to 1pm if time==lunch
        if(time==3){
            date.set(Calendar.HOUR,20);
            date.set(Calendar.MINUTE,0);
        }

        //create event
        Event event=new Event(Color.BLUE,date.getTimeInMillis());

        return  event;
    }
}
