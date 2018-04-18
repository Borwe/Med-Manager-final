package alarms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import constants.ActivityConstants;
import med.borwe.com.med_manager.R;
import med.borwe.com.med_manager.dialog_activities.NotificationInfo;

public class NotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        //hold channel info
        final int CHANNEL=9000;

        //intent to launch dialog showing notifcations
        Intent launcher=new Intent(context, NotificationInfo.class);
        launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //put data into pending intent from current intent
        launcher.putExtra("name",intent.getStringExtra("name"));
        launcher.putExtra("description",intent.getStringExtra("description"));
        launcher.putExtra("pills",intent.getIntExtra("pills",0));

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,launcher,PendingIntent.FLAG_UPDATE_CURRENT);

        String main_text="Please take "+intent.getIntExtra("pills",0)+"" +
                " pills of "+intent.getStringExtra("name")+"with description "+intent.getStringExtra("description");
        Toast.makeText(context,intent.getStringExtra("name"),Toast.LENGTH_LONG).show();
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context,ActivityConstants.NOTIFICATION_CHANNEL);
        notificationBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
        notificationBuilder.setContentTitle("Take "+intent.getIntExtra("pills",0)+" of "+intent.getStringExtra("name"));
        notificationBuilder.setContentText(intent.getStringExtra(main_text));
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(main_text));
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int)Calendar.getInstance().getTimeInMillis(),notificationBuilder.build());
    }
}
