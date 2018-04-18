package med.borwe.com.med_manager.dialog_activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import med.borwe.com.med_manager.R;
import med.borwe.com.med_manager.fragments.AddMedicationFragment;

public class DescriptionDialogActivity extends AppCompatActivity {

    //Button to handle on clicking add description
    Button add_description_button;

    //EditText to hold description
    EditText add_description_edit_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_dialog);
        setTitle("Add Description");
        setFinishOnTouchOutside(false);

        //get add_description_edit_text and add_description_button from layout
        add_description_edit_text=findViewById(R.id.add_description_edit_text);
        add_description_button=findViewById(R.id.add_description_button);
        add_description_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetDescription().execute();
            }
        });
    }

    class GetDescription extends AsyncTask<Void,Void,Void>{
        //progress dialog for slow phones
        ProgressDialog dialog;

        //check if error occured
        boolean error_occured=false;
        String error=null;

        //hold text from add_description_edit_text
        String text_input=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(DescriptionDialogActivity.this);
            dialog.setTitle("Loading..");
            dialog.setMessage("Checking message");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            text_input=add_description_edit_text.getText().toString().trim();
            if(text_input== null || text_input.isEmpty()==true){
                error_occured=true;
                error="Please input something in description";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.cancel();

            //if user didn't enter something
            if(error_occured==true){
                //show alert dialog
                AlertDialog.Builder alert=new AlertDialog.Builder(DescriptionDialogActivity.this);
                alert.setTitle("Sorry...");
                alert.setMessage(error);
                alert.setCancelable(false);
                alert.setPositiveButton("Okay, let me retry",null);
                alert.create().show();
            }else{
                Intent returnIntent=new Intent();
                returnIntent.putExtra("description",text_input);
                setResult(AddMedicationFragment.DESCRIPTION_CODE,returnIntent);
                DescriptionDialogActivity.this.finish();
            }
        }
    }
}
