package com.silas.meditacao.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SchedulerReceiver extends BroadcastReceiver {
    public SchedulerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent("ALARME_DISPARADO");

        boolean alarmeAtivo = (PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_NO_CREATE) != null);


        if (!alarmeAtivo) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

            Calendar c = Calendar.getInstance();

            c.set(Calendar.HOUR_OF_DAY, 6);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);//set repeating every 24
//            Log.i("Alarm", "Novo alarm");
        } /*else {
//            Log.i("Alarm", "alarm ja ativado");
        }*/
    }
}
