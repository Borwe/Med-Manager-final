package med.borwe.com.med_manager.dialog_activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.HashMap;

import med.borwe.com.med_manager.R;
import med.borwe.com.med_manager.fragments.AddMedicationFragment;

public class FrequencyDialogActivity extends AppCompatActivity {

    //button to handle sending data
    Button frequency_add_button;

    //checkboxes for time to take pills
    CheckBox frequency_morning_check,frquency_lunch_check,frequency_evening_check;

    //edittext for number of pills
    EditText frequency_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency_dialog);
        setFinishOnTouchOutside(false);

        setTitle("Set Frequency Of Medication");

        //get elements
        frequency_add_button=findViewById(R.id.frequency_add_button);
        frequency_edit_text=findViewById(R.id.frequency_edit_text);
        frequency_morning_check=findViewById(R.id.frequency_morning_check);
        frquency_lunch_check=findViewById(R.id.frquency_lunch_check);
        frequency_evening_check=findViewById(R.id.frequency_evening_check);

        //add listener to frequency_add_button
        frequency_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetFrequency().execute();
            }
        });
    }

    class SetFrequency extends AsyncTask<Void,Void,Void>{
        //show progressdialog for slow phones
        ProgressDialog dialog;

        //String to hold number of pills to take
        String no_of_pills=null;
        int pills_chosen=0;

        //HashMap containing all data to sendback
        HashMap<String,Boolean> checkboxes;

        //handle errors
        boolean error_occured=false;

        //hold error message
        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(FrequencyDialogActivity.this);
            dialog.setTitle("Loading...");
            dialog.setMessage("Getting data");
            dialog.setCancelable(false);
            dialog.show();
            checkboxes=new HashMap<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            no_of_pills=frequency_edit_text.getText().toString();
            //check if frequency_edit_text is filled
            if(no_of_pills==null || no_of_pills.isEmpty()==true){
                //when not set error_occured true and error message
                error_occured=true;
                error="Please enter a pills number";
                return null;
            }

            //check if no_of_pills is zero or less, and throw error if so.
            if(Integer.parseInt(no_of_pills)<=0){
                error_occured=true;
                error="Please enter number of pills greater than 0";
                return null;
            }

            //check if all checkboxes are not checked, if so throw error
            if(frequency_evening_check.isChecked()==false &&
                    frequency_morning_check.isChecked()==false &&
                    frquency_lunch_check.isChecked()==false){
                //show error
                error_occured=true;
                error="Please select atleast one period from which to take your pills";
                return null;
            }

            //add checkboxes to hashmap status
            checkboxes.put("morning",frequency_morning_check.isChecked());
            checkboxes.put("lunch",frquency_lunch_check.isChecked());
            checkboxes.put("evening",frequency_evening_check.isChecked());

            //turn number of pills from string to integer
            pills_chosen=Integer.parseInt(no_of_pills);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.cancel();
            //check for errors
            if(error_occured==true){
                error_occured=false;
                AlertDialog.Builder alert=new AlertDialog.Builder(FrequencyDialogActivity.this);
                alert.setTitle("Sorry..");
                alert.setMessage(error);
                alert.setCancelable(false);
                alert.setPositiveButton("Okay, let me retry",null);
                alert.create().show();
            }else{
                //if no error occured
                Intent reply=new Intent();
                reply.putExtra("pills_count",pills_chosen);

                //Bundle to hold hashmap
                Bundle bundle=new Bundle();
                bundle.putSerializable("checkboxes",checkboxes);

                //add bundle to intent
                reply.putExtra("hashmap",bundle);

                setResult(AddMedicationFragment.FREQUENCY_CODE,reply);
                FrequencyDialogActivity.this.finish();
            }
        }
    }
}
