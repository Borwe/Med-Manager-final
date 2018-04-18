package med.borwe.com.med_manager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import constants.ActivityConstants;
import data_holders.Medication;
import med.borwe.com.med_manager.dialog_activities.SearchDialog;
import med.borwe.com.med_manager.fragments.AddMedicationFragment;
import med.borwe.com.med_manager.fragments.MedicationsFragment;

public class MainActivity extends AppCompatActivity {
    MenuItem searchButton;//hide search button untill user is on ListMedication View

    // Setup spinner
    Spinner spinner;//handle tabs
    public static final int SEARCH_ACTIVITY=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Add Medication",
                        "List Medications",
                        "Current Search"
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                switch (position){
                    case 0:
                        searchButton.setVisible(false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, AddMedicationFragment.newInstance(null,null))
                                .commit();
                        break;
                    case 1:
                        searchButton.setVisible(true);
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().getFragments().get(0))
                                .replace(R.id.container, MedicationsFragment.newInstance(0))
                                .commitNowAllowingStateLoss();
                        break;
                    case 3:
                        searchButton.setVisible(true);
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().getFragments().get(0))
                                .replace(R.id.container, MedicationsFragment.newInstance(0))
                                .commitNowAllowingStateLoss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== MainActivity.SEARCH_ACTIVITY){

            int position=-2;
            try{
                position=data.getIntExtra("position",-2);
            }catch (Exception ex){

            }
            if(position<=-1){
                AlertDialog.Builder alert=new AlertDialog.Builder(this);
                alert.setTitle("Sorry");
                alert.setMessage("Sorry, no medication found with such a name");
                alert.setCancelable(false);
                alert.setPositiveButton(("Okay, I will try later"),null);
                alert.create().show();
            }else{
                Toast.makeText(this,"POSITION: "+position,Toast.LENGTH_SHORT).show();

                //Hide all activities untill you reach the selected one
                List<Medication> medications=
                        ((MedicationsFragment.MyRecyclerViewAdapter)MedicationsFragment
                                .recycler_view_medication.getAdapter()).getMedications();


                Medication medicationToSave=medications.get(position);
                medications.clear();
                medications.add(medicationToSave);

                ((MedicationsFragment.MyRecyclerViewAdapter)MedicationsFragment.recycler_view_medication.getAdapter())
                        .setMedications(medications);
                ((MedicationsFragment.MyRecyclerViewAdapter)MedicationsFragment.recycler_view_medication.getAdapter())
                        .notifyDataSetChanged();

                //set spinner to think you are now viewing search
                spinner.setSelection(2,true);
                //display message informing user to open page again to see all medications fully
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Information");
                alert.setMessage("We have filtered medications to bring your searched one to top\nTo see all" +
                        " medications again, just open the List Medications tab again.\n" +
                        "Thank you");
                alert.setPositiveButton("Okay, I have seen",null);
                alert.setCancelable(false);
                alert.create().show();



            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchButton=menu.findItem(R.id.medication_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if search button is clicked
        if(id==R.id.medication_search){
            Intent searchActivity=new Intent(MainActivity.this, SearchDialog.class);
            startActivityForResult(searchActivity,SEARCH_ACTIVITY);
        }

        //if user clicks on sync option
        if(id==R.id.medication_sync){
            ContentResolver cr = this.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
            values.put(CalendarContract.Calendars.VISIBLE, 1);
            cr.update(
                    ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, ActivityConstants.CALENDAR_ID_CONST),
                    values, null, null);
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }
}
