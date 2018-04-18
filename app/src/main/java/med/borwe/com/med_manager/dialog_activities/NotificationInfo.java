package med.borwe.com.med_manager.dialog_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import med.borwe.com.med_manager.R;

public class NotificationInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);
        setTitle("Medical Information");

        Intent data=getIntent();
        //get and set name of mdeication
        TextView notifi_info_name=findViewById(R.id.notifi_info_name);
        notifi_info_name.setText(data.getStringExtra("name"));

        //get and set number of pills
        TextView notifi_info_pills=findViewById(R.id.notifi_info_pills);
        notifi_info_pills.setText(data.getIntExtra("pills",0)+"");

        //get and set description
        TextView notifi_info_description=findViewById(R.id.notifi_info_description);
        notifi_info_description.setText(data.getStringExtra("description"));

        //set button to exit this activity when pressed
        Button notifi_exit_btn=findViewById(R.id.notifi_exit_btn);
        notifi_exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationInfo.this.finish();
            }
        });
    }
}
