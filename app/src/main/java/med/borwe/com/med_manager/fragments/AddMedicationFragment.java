package med.borwe.com.med_manager.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import constants.MedicationAlarmSetter;
import data_holders.Medication;
import database.AppDatabase;
import med.borwe.com.med_manager.R;
import med.borwe.com.med_manager.dialog_activities.DescriptionDialogActivity;
import med.borwe.com.med_manager.dialog_activities.FrequencyDialogActivity;

//HOLD INPUT FIELD TO ADD MEDICATION PRESCRIPTION
public class AddMedicationFragment extends Fragment {

    //Hold data from edit_med_name EditText
    EditText edit_med_name;

    //Handle adding to db
    Button add_med_button;

    //Handle displaying and getting data back from DescriptionDialogActivity
    Button description_button;

    //Handle displaying and getting frequency data from FrequencyDialogActivity
    Button frequency_button;

    //start and end date textinputs
    EditText med_start_date,med_end_date;

    //hold requestcode for description activity
    public final static int DESCRIPTION_CODE=999;

    //hold requestcode for frequency activity
    public static final int FREQUENCY_CODE=998;

    //hold medication description
    String medication_description=null;

    //hold checkboxes of times to take pill
    HashMap<String,Boolean> time_checkboxes=null;

    //hold number of pills for current medication application
    int no_of_pills;

    //hold start date
    Calendar start_date=null;

    //hold end date
    Calendar end_date=null;

    public AddMedicationFragment() {
        // Required empty public constructor
    }

    public static AddMedicationFragment newInstance(String param1, String param2) {
        AddMedicationFragment fragment = new AddMedicationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View main_view=inflater.inflate(R.layout.fragment_add_medication, container, false);

        //map to edit_med_name
        edit_med_name=main_view.findViewById(R.id.edit_med_name);
        //map to add_med_button and setup listender
        add_med_button=main_view.findViewById(R.id.add_med_button);
        add_med_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start checking and adding medication
                new AddMedication().execute();
            }
        });

        //map description_button to Description button
        description_button=main_view.findViewById(R.id.description_button);
        description_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), DescriptionDialogActivity.class);
                startActivityForResult(i,DESCRIPTION_CODE);
            }
        });

        //map frequency_button to Frequency button
        frequency_button=main_view.findViewById(R.id.frequency_button);
        frequency_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), FrequencyDialogActivity.class);
                startActivityForResult(i,FREQUENCY_CODE);
            }
        });

        //map med_start_date and med_end_date to Start and End editText
        med_start_date=main_view.findViewById(R.id.med_start_date);
        med_end_date=main_view.findViewById(R.id.med_end_date);
        //handle when start date is clicked
        med_start_date.setFocusable(false);
        med_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePicker datePicker=new DatePicker(main_view.getContext());
                datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

                //add datepicker to dialog
                AlertDialog.Builder dateHolder=new AlertDialog.Builder(main_view.getContext());
                dateHolder.setView(datePicker);
                dateHolder.setPositiveButton("Set Starting Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        start_date=Calendar.getInstance();
                        start_date.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        med_start_date.setText(start_date.get(Calendar.DAY_OF_MONTH)+"/"+
                                (start_date.get(Calendar.MONTH)+1)+"/"+start_date.get(Calendar.YEAR));
                    }
                });
                dateHolder.create().show();
            }
        });

        //handle when end_date is clicked
        med_end_date.setFocusable(false);
        med_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle date picking
                final DatePicker datePicker=new DatePicker(main_view.getContext());
                if(start_date!=null){
                    datePicker.setMinDate(start_date.getTimeInMillis());
                }else{
                    datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
                }

                AlertDialog.Builder dateDialog=new AlertDialog.Builder(main_view.getContext());
                dateDialog.setView(datePicker);
                dateDialog.setPositiveButton("Set End Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        end_date=Calendar.getInstance();
                        end_date.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        med_end_date.setText(end_date.get(Calendar.DAY_OF_MONTH)+"/"+
                                (end_date.get(Calendar.MONTH)+1)+"/"+
                                end_date.get(Calendar.YEAR));
                    }
                });
                dateDialog.create().show();
            }
        });

        return main_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case DESCRIPTION_CODE:
                if(data!=null){
                    medication_description=data.getStringExtra("description");
                    //Toast.makeText(getContext(),medication_description,Toast.LENGTH_SHORT).show();
                }
                break;

            case FREQUENCY_CODE:
                if(data!=null){
                    //get hashmap
                    time_checkboxes=
                            (HashMap<String,Boolean>)data.getBundleExtra("hashmap").getSerializable("checkboxes");
                    no_of_pills=data.getIntExtra("pills_count",0);

                    //Toast.makeText(getContext(),time_checkboxes.toString()+" pills="+no_of_pills,Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class AddMedication extends AsyncTask<Void,Void,Void>{
        //hand showing progress dialog
        ProgressDialog dialog;

        //HOLD DB INFO
        String database;

        //handle spotting errors, true if error exists
        boolean error_occured=false;
        String error=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getContext());
            dialog.setTitle("Loading...");
            dialog.setMessage("Please wait..");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(edit_med_name.getText().toString().trim().isEmpty()==true){
                error_occured=true;
                error="Sorry, Please enter a Name for this Medication before adding it";
                return null;
            }

            //check if description was added
            if(medication_description==null || medication_description.trim().isEmpty()){
                error_occured=true;
                error="Sorry, Please make sure you have entered a description for this medication" +
                        " before submitting it";
                return null;
            }

            //check if no_of_pills frequency is greater than zero and time_checkboxes exists
            if(no_of_pills<=0 || time_checkboxes==null || time_checkboxes.isEmpty()){
                error_occured=true;
                error="Sorry, please input number of pills and times to take pills by clicking on" +
                        " frequency button and filling in the information";
                return null;
            }

            //check if start and ending dates have been set
            if(start_date==null || end_date==null){
                //if so then set error
                error_occured=true;
                error="Sorry, Please give starting and ending date of your medication before proceeding";
                return null;
            }

            //check if start and end date interval is positive
            if(start_date.after(end_date)){
                error_occured=true;
                error="Sorry, Please make sure your end date is after your start date.";
                return null;
            }

            //if finally reached here, create Medication Object and add it to database
            //start by getting values
            String med_name=edit_med_name.getText().toString();//name
            //medication_description == med description
            String start_date=med_start_date.getText().toString();//start date
            String end_date=med_end_date.getText().toString();//end date
            boolean morning=time_checkboxes.get("morning");
            boolean lunch=time_checkboxes.get("lunch");
            boolean evening=time_checkboxes.get("evening");
            //create object
            Medication medication=new Medication(med_name,medication_description,no_of_pills,start_date,end_date,morning,lunch,evening);

            //create medication alarms
            List<Long> med_ids=MedicationAlarmSetter.createMedicationAlarms(getContext(),medication);

            medication.setMedication_id_list(med_ids);

            //add object to database
            AppDatabase.getAppDatabase(getContext()).medicationDAO().addMedication(medication);
            //Log.d("MEDS",medication.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.cancel();
            //check if error occured
            if(error_occured==true){
                //true if error occured, so show error dialog...
                error_occured=false;
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setTitle("Error...");
                alert.setMessage(error);
                alert.setPositiveButton("Okay, let me retry",null);
                alert.setCancelable(false);
                alert.create().show();
            }else{
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setTitle("Well Dnoe");
                alert.setMessage("Your medication has been successfully updated, kindly wait for an alarm");
                alert.setNeutralButton("Yes",null);
                alert.setCancelable(false);
                alert.create().show();
            }
        }
    }
}
