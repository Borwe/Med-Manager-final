package med.borwe.com.med_manager.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import data_holders.Medication;
import database.AppDatabase;
import med.borwe.com.med_manager.MainActivity;
import med.borwe.com.med_manager.R;
import med.borwe.com.med_manager.dialog_activities.MonthView;

public class MedicationsFragment extends Fragment {

    //hold recyclerView
    public static RecyclerView recycler_view_medication;

    //position of recclerview
    public static int recycler_position=0;

    public MedicationsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MedicationsFragment newInstance(int position) {
        MedicationsFragment fragment = new MedicationsFragment();
        recycler_position=position;
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
        View main_view=inflater.inflate(R.layout.fragment_medications, container, false);
        //get elements inside fragment_medications
        recycler_view_medication=main_view.findViewById(R.id.recycler_view_medication);

        //make recyclerview use linear layout manager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(main_view.getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view_medication.setLayoutManager(linearLayoutManager);


        //set adapter for recylcerview
        recycler_view_medication.setAdapter(new MyRecyclerViewAdapter(main_view.getContext()));
        return  main_view;
    }

    //Adapter to handle recyclerview
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{
        Context context;
        List<Medication> medications;

        public MyRecyclerViewAdapter(final Context context){
            this.context=context;

            //get medications from db
            Thread getmedications=new Thread(new Runnable() {
                @Override
                public void run() {
                    medications= AppDatabase.getAppDatabase(context).medicationDAO().getAll();
                }
            });
            getmedications.start();
            try {
                getmedications.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setMedications(List<Medication> medications) {
            this.medications = medications;
        }

        public List<Medication> getMedications() {
            return medications;
        }

        @NonNull
        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(context);
            //get itemholder view
            View itemHolder=inflater.inflate(R.layout.medication,parent,false);
            ViewHolder item=new ViewHolder(itemHolder);
            return item;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyRecyclerViewAdapter.ViewHolder holder, final int position) {
            //set medication name
            holder.medication_name.setText(medications.get(position).getMedication_name());
            holder.medication_description.setText(medications.get(position).getMedication_description());

            if(medications.get(position).isMedication_time_morning()==true){
                holder.morning.setChecked(true);
            }
            if(medications.get(position).isMedication_time_lunch()==true){
                holder.lunch.setChecked(true);
            }
            if(medications.get(position).isMedication_time_evening()==true){
                holder.evening.setChecked(true);
            }

            holder.medication_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Toast.makeText(holder.medication_layout.getContext(),
                            medications.get(position).getMedication_name()+" CLICKED LAYOUT",
                            Toast.LENGTH_SHORT).show();*/
                    Intent i=new Intent(holder.medication_layout.getContext(), MonthView.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("medication",medications.get(position));
                    i.putExtra("bundle",bundle);
                    startActivity(i);
                }
            });

            //handle deleting medication and medication info from database and phone calendar
            holder.medication_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get list of all calendar ids containing this medication
                    List<Long> med_ids=medications.get(position).getMedication_id_long_list();
                    //delete entries from calendar
                    for(long med_id:med_ids){
                        Uri deleteCalendarEntry=
                                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,med_id);

                        holder.medication_layout.getContext().getContentResolver().
                                delete(deleteCalendarEntry,null,null);
                    }

                    Thread dbSteps=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //delete medication from database
                            AppDatabase.getAppDatabase(holder.medication_layout.getContext())
                                    .medicationDAO().deleteMedication(medications.get(position));

                            //update recycler view
                            setMedications(AppDatabase.getAppDatabase(holder.medication_layout.getContext())
                                    .medicationDAO().getAll());
                        }
                    });
                    dbSteps.start();
                    try {
                        dbSteps.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MyRecyclerViewAdapter.this.notifyDataSetChanged();
                }
            });
        }



        @Override
        public int getItemCount() {
            return medications.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //views per item
            TextView medication_name;
            TextView medication_description;
            CheckBox morning;
            CheckBox lunch;
            CheckBox evening;

            //main layout per item
            CardView medication_layout;

            //For delete button
            Button medication_delete;

            public ViewHolder(final View itemView) {
                super(itemView);
                medication_layout=itemView.findViewById(R.id.medication_layout);
                medication_name=itemView.findViewById(R.id.medication_name);
                medication_description=itemView.findViewById(R.id.medication_description);
                medication_delete=itemView.findViewById(R.id.medication_delete_button);

                //get checkboxes views and make them not able to change views by user
                morning=itemView.findViewById(R.id.medication_time_morning);
                morning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked=!morning.isChecked();
                        morning.setChecked(checked);
                    }
                });
                lunch=itemView.findViewById(R.id.medication_time_lunch);
                lunch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked=!lunch.isChecked();
                        lunch.setChecked(checked);
                    }
                });
                evening=itemView.findViewById(R.id.medication_time_evening);
                evening.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked=!evening.isChecked();
                        evening.setChecked(checked);
                    }
                });
            }
        }
    }
}
