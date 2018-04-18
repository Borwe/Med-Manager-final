package med.borwe.com.med_manager.dialog_activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.EGLExt;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import data_holders.Medication;
import database.AppDatabase;
import med.borwe.com.med_manager.MainActivity;
import med.borwe.com.med_manager.R;

public class SearchDialog extends AppCompatActivity {

    AutoCompleteTextView search_input;
    Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog);
        setTitle("Enter Medication name");

        search_input=findViewById(R.id.search_input);
        search_button=findViewById(R.id.search_button);


        //get all med names from database
        List<String> medNames=getmedNames();

        //if list is empty, show prompt and exit this dialog
        if(medNames.size()<=0){
            endSearchCauseNothingToSearch();
        }

        final ArrayAdapter<String> searchMeds=new ArrayAdapter<>(this, android.R.layout.select_dialog_item,medNames);
        search_input.setAdapter(searchMeds);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchWithInput().execute();
            }
        });

    }

    class SearchWithInput extends AsyncTask<Void,Void,Void>{
        ProgressDialog dialog;

        //for holding position
        int position=-1;

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(SearchDialog.this);
            dialog.setMessage("Searching....");
            dialog.setCancelable(false);
            dialog.setTitle("Searching...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get text from search_input
            String input=search_input.getText().toString().trim().toLowerCase();

            //search database and see if it matches any, if so return position of medication
            //inside intent to return, if not, show error dialog and request user to search
            //again or else close search.
            final List<String> names=new ArrayList<>();
            Thread getData=new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Medication> medications=AppDatabase.getAppDatabase(SearchDialog.this).
                            medicationDAO().getAll();
                    for(Medication medication:medications){
                        names.add(medication.getMedication_name());
                    }
                }
            });
            getData.start();
            try {
                getData.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //check if input is a text inside names or contained in atleast one
            for(int i=0;i<names.size();i++){
                if(names.get(i).toLowerCase().equals(input)){
                    position=i;
                    break;
                }
            }
            //if not found, then now search if input is contained in any name
            if(position==-1){
                for(int i=0;i<names.size();i++){
                    if(names.get(i).toLowerCase().contains(input)){
                        position=i;
                        break;
                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
            dialog.cancel();

            //if something found, then setup intent with position data sending it back
            Intent intent=new Intent();
            intent.putExtra("position",position);
            setResult(MainActivity.SEARCH_ACTIVITY,intent);
            SearchDialog.this.finish();
        }
    }

    private List<String> getmedNames(){
        final List<String> names=new ArrayList<>();

        //transfer names from medications to names list

        Thread getData=new Thread(new Runnable() {
            @Override
            public void run() {
                List<Medication> medications=AppDatabase.getAppDatabase(SearchDialog.this).medicationDAO().getAll();
                for(Medication med:medications){
                    names.add(med.getMedication_name());
                }
            }
        });
        getData.start();
        try {
            getData.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  names;
    }

    private void endSearchCauseNothingToSearch(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("No Medications");
        alert.setMessage("Sorry, it appears you have no medications, so nothing to search");
        alert.setPositiveButton("Okay Thank You", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SearchDialog.this.finish();
            }
        });
        alert.setCancelable(false);
        alert.create().show();
    }
}
