package constants;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alarms.NotificationReciever;
import data_holders.Medication;

public class MedicationAlarmSetter {

    //List to hold all intents that are inside pendingIntents
    public static List<Intent> intents = new ArrayList<>();

    public static List<Long> createMedicationAlarms(Context context, Medication medication) {
        //clear list of intents on every call
        /*intents.clear();

        List<PendingIntent> pendingIntents=generatePendingIntentsForAlarms(context,medication);
        for(int i=0;i<pendingIntents.size();i++){
            createAndApplyMedicationWithAlarmManager(pendingIntents.get(i),intents.get(i),context);
        }
        Log.d("ALL_COUNT",pendingIntents.size()+"");

        //clear intents for next application
        intents.clear();*/

        return addMedicationToCalendar(medication,context);
    }

    private static List<Long> addMedicationToCalendar(Medication medication, Context context) {
        //get times to take medicine
        boolean morning = medication.isMedication_time_morning();
        boolean lunch = medication.isMedication_time_lunch();
        boolean evening = medication.isMedication_time_evening();

        //array to hold Ids for each calendar event for this medication
        List<Long> med_ids=new ArrayList<>();

        //add medications for morning in calendar
        if (morning == true) {
            //get start date
            Calendar start_date = Calendar.getInstance();
            String start_day = medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1]) - 1,
                    Integer.parseInt(start_day.split("/")[0]), 8, 00, 00);
            Log.d("START_DATE_MORNING", start_date.getTime().toString());

            //get end date
            Calendar end_date = Calendar.getInstance();
            String end_day = medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1]) - 1,
                    Integer.parseInt(end_day.split("/")[0]), 8, 00, 00);
            Log.d("ENDING_DATE_MORNING", end_date.getTime().toString());

            //create pending intents for all days in between
            while (start_date.after(end_date) == false) {
                Calendar date_to_use= (Calendar) start_date.clone();
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();

                contentValues.put(CalendarContract.Events.TITLE, medication.getMedication_name());
                contentValues.put(CalendarContract.Events.DESCRIPTION, "Take " +
                        medication.getMedication_pills_count()+" with description: \n"+
                        medication.getMedication_description());
                contentValues.put(CalendarContract.Events.DTSTART, date_to_use.getTimeInMillis());
                contentValues.put(CalendarContract.Events.DTEND, date_to_use.getTimeInMillis() + 1000 * 60 * 30);
                contentValues.put(CalendarContract.Events.CALENDAR_ID, ActivityConstants.CALENDAR_ID_CONST);
                contentValues.put(CalendarContract.Events.HAS_ALARM, 1);
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

                //add to calendar
                @SuppressLint("MissingPermission")
                Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);

                //add calendar id to med_id
                med_ids.add(Long.parseLong(uri.getLastPathSegment()));

                start_date.roll(Calendar.DAY_OF_YEAR,true);
                Log.d("DATE_FUCKER","FUCKING BULLSHIT MORNING");
            }
        }

        if(lunch==true){
            //get start date
            // TODO set with user time later
            Calendar start_date=Calendar.getInstance();
            String start_day=medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1])-1,
                    Integer.parseInt(start_day.split("/")[0]),13,00,00);
            Log.d("START_DATE_LUNCH",start_date.getTime().toString());

            //get end date
            Calendar end_date=Calendar.getInstance();
            String end_day=medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1])-1,
                    Integer.parseInt(end_day.split("/")[0]),13,00,00);
            Log.d("ENDING_DATE_LUNCH",end_date.getTime().toString());

            //create pending intents for all days in between
            while(start_date.after(end_date)==false){
                Calendar date_to_use= (Calendar) start_date.clone();
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();

                contentValues.put(CalendarContract.Events.TITLE, medication.getMedication_name());
                contentValues.put(CalendarContract.Events.DESCRIPTION, "Take " +
                        medication.getMedication_pills_count() + " of description:\n " +
                        medication.getMedication_description());
                contentValues.put(CalendarContract.Events.DTSTART, date_to_use.getTimeInMillis());
                contentValues.put(CalendarContract.Events.DTEND, date_to_use.getTimeInMillis() + 1000 * 60 * 30);
                contentValues.put(CalendarContract.Events.CALENDAR_ID, ActivityConstants.CALENDAR_ID_CONST);
                contentValues.put(CalendarContract.Events.HAS_ALARM, 1);
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

                //add to calendar
                @SuppressLint("MissingPermission")
                Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);

                //add calendar id to med_id
                med_ids.add(Long.parseLong(uri.getLastPathSegment()));

                start_date.roll(Calendar.DAY_OF_YEAR,true);
            }
        }

        if(evening==true){
            //get start date
            // TODO set with user time later
            Calendar start_date=Calendar.getInstance();
            String start_day=medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1])-1,
                    Integer.parseInt(start_day.split("/")[0]),20,00,00);
            Log.d("START_DATE_EVENING",start_date.getTime().toString());

            //get end date
            Calendar end_date=Calendar.getInstance();
            String end_day=medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1])-1,
                    Integer.parseInt(end_day.split("/")[0]),20,00,00);
            Log.d("ENDING_DATE_EVENING",end_date.getTime().toString());

            //create pending intents for all days in between
            while(start_date.after(end_date)==false){
                Calendar date_to_use= (Calendar) start_date.clone();
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();

                contentValues.put(CalendarContract.Events.TITLE, medication.getMedication_name());
                contentValues.put(CalendarContract.Events.DESCRIPTION, "Take " +
                        medication.getMedication_pills_count() + " of description:\n " +
                        medication.getMedication_description());
                contentValues.put(CalendarContract.Events.DTSTART, date_to_use.getTimeInMillis());
                contentValues.put(CalendarContract.Events.DTEND, date_to_use.getTimeInMillis() + 1000 * 60 * 30);
                contentValues.put(CalendarContract.Events.CALENDAR_ID, ActivityConstants.CALENDAR_ID_CONST);
                contentValues.put(CalendarContract.Events.HAS_ALARM, 1);
                contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

                //add to calendar
                @SuppressLint("MissingPermission")
                Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);

                //add calendar id to med_id
                med_ids.add(Long.parseLong(uri.getLastPathSegment()));

                start_date.roll(Calendar.DAY_OF_YEAR,true);
            }
        }
        return med_ids;
    }

    private static List<PendingIntent> generatePendingIntentsForAlarms(Context context,Medication medication){
        //List to hold pending intents
        List<PendingIntent> pendingIntents=new ArrayList<>();

        //get times to take medicine
        boolean morning=medication.isMedication_time_morning();
        boolean lunch=medication.isMedication_time_lunch();
        boolean evening=medication.isMedication_time_evening();

        if(morning==true){
            //get start date
            // TODO set with user time later
            Calendar start_date=Calendar.getInstance();
            String start_day=medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1])-1,
                    Integer.parseInt(start_day.split("/")[0]),8,00,00);
            Log.d("START_DATE_MORNING",start_date.getTime().toString());

            //get end date
            Calendar end_date=Calendar.getInstance();
            String end_day=medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1])-1,
                    Integer.parseInt(end_day.split("/")[0]),8,00,00);
            Log.d("ENDING_DATE_MORNING",end_date.getTime().toString());

            //create pending intents for all days in between
            while(start_date.after(end_date)==false){
                Intent notifyIntent=new Intent(context, NotificationReciever.class);
                notifyIntent.putExtra("name",medication.getMedication_name());
                notifyIntent.putExtra("description",medication.getMedication_description());
                Calendar date= (Calendar) start_date.clone();
                notifyIntent.putExtra("date",date);
                notifyIntent.putExtra("pills",medication.getMedication_pills_count());
                //add to intents
                intents.add(notifyIntent);
                //get request code for pendingIntent
                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,generateRequestCode(start_date,medication),
                        notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntents.add(pendingIntent);
                //break;
                start_date.roll(Calendar.DAY_OF_YEAR,true);
            }
        }

        if(lunch==true){
            //get start date
            // TODO set with user time later
            Calendar start_date=Calendar.getInstance();
            String start_day=medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1])-1,
                    Integer.parseInt(start_day.split("/")[0]),13,00,00);
            Log.d("START_DATE_LUNCH",start_date.getTime().toString());

            //get end date
            Calendar end_date=Calendar.getInstance();
            String end_day=medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1])-1,
                    Integer.parseInt(end_day.split("/")[0]),13,00,00);
            Log.d("ENDING_DATE_LUNCH",end_date.getTime().toString());

            //create pending intents for all days in between
            while(start_date.after(end_date)==false){
                Intent notifyIntent=new Intent(context, NotificationReciever.class);
                notifyIntent.putExtra("name",medication.getMedication_name());
                notifyIntent.putExtra("description",medication.getMedication_description());
                Calendar date= (Calendar) start_date.clone();
                notifyIntent.putExtra("date",date);
                notifyIntent.putExtra("pills",medication.getMedication_pills_count());
                //add to intents
                intents.add(notifyIntent);
                //get request code for pendingIntent
                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,generateRequestCode(start_date,medication),
                        notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntents.add(pendingIntent);
                start_date.roll(Calendar.DAY_OF_YEAR,true);
            }
        }

        if(evening==true){
            //get start date
            // TODO set with user time later
            Calendar start_date=Calendar.getInstance();
            String start_day=medication.getMedication_start_date(); //DATE FORMAT  ==  18/4/2018 == dd/MM/YYYY
            start_date.set(Integer.parseInt(start_day.split("/")[2]),
                    Integer.parseInt(start_day.split("/")[1])-1,
                    Integer.parseInt(start_day.split("/")[0]),20,00,00);
            Log.d("START_DATE_EVENING",start_date.getTime().toString());

            //get end date
            Calendar end_date=Calendar.getInstance();
            String end_day=medication.getMedication_end_date();
            end_date.set(Integer.parseInt(end_day.split("/")[2]),
                    Integer.parseInt(end_day.split("/")[1])-1,
                    Integer.parseInt(end_day.split("/")[0]),20,00,00);
            Log.d("ENDING_DATE_EVENING",end_date.getTime().toString());

            //create pending intents for all days in between
            while(start_date.after(end_date)==false){
                Intent notifyIntent=new Intent(context, NotificationReciever.class);
                notifyIntent.putExtra("name",medication.getMedication_name());
                notifyIntent.putExtra("description",medication.getMedication_description());
                Calendar date= (Calendar) start_date.clone();
                notifyIntent.putExtra("date",date);
                notifyIntent.putExtra("pills",medication.getMedication_pills_count());
                notifyIntent.putExtra("notify_id",generateRequestCode(start_date,medication));
                //add to intents
                intents.add(notifyIntent);
                //get request code for pendingIntent
                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,generateRequestCode(start_date,medication),
                        notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntents.add(pendingIntent);
                start_date.roll(Calendar.DAY_OF_YEAR,true);
            }
        }

        return  pendingIntents;
    }

    private static  boolean createAndApplyMedicationWithAlarmManager(PendingIntent pendingIntent,Intent intent,Context context){
        //test run to see if Alarm works, check in 5 seconds
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        /*Calendar test=Calendar.getInstance();
        test.add(Calendar.SECOND,5);*/
        //check if android version is bellow 19
        //GET DATE AND TIME TO SET ALARM
        Calendar date= (Calendar) intent.getExtras().get("date");
        Log.d("DATE_SET: ",date.getTime().toString());
        if(Build.VERSION.SDK_INT<19){
            alarmManager.set(AlarmManager.RTC_WAKEUP,date.getTimeInMillis(),pendingIntent);
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,date.getTimeInMillis(),pendingIntent);
        }
        Log.d("PENDING_RE","wait 5 seconds");
        return true;
    }

    private static int generateRequestCode(Calendar date,Medication medication){
        Map<Character,Integer> map=new HashMap<>();
        map.put('a', 1);map.put('b', 2);map.put('c', 3);map.put('d', 4);map.put('e', 5);
        map.put('f', 6);map.put('g', 7);map.put('h', 8);map.put('i', 9);map.put('j', 10);
        map.put('k', 11);map.put('l', 12);map.put('m', 13);map.put('n', 14);map.put('o', 15);
        map.put('p', 16);map.put('q', 17);map.put('r', 18);map.put('s', 19);map.put('t', 20);
        map.put('u', 21);map.put('v', 22);map.put('w', 23);map.put('x', 24);map.put('y', 25);
        map.put('z', 26);
        int intName=0;//hold medication name in form of text
        for(char c:medication.getMedication_name().toLowerCase().toCharArray()){
            if(map.get(c)!=null){
                intName=intName+map.get(c);
            }
        }

        String string=(date.get(Calendar.MONTH)+1)+""+date.get(Calendar.DAY_OF_MONTH)+""+date.get(Calendar.HOUR_OF_DAY)+""+intName;
        int requestCode=Integer.parseInt(string);
        Log.d("PENDING_REQ: ",requestCode+"");
        return requestCode;
    }
}
