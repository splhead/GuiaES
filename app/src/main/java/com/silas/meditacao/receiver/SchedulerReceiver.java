package com.silas.meditacao.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.silas.meditacao.io.TimePreference;
import com.silas.meditacao.io.TimePreferenceDialogFragmentCompat;

import java.util.Calendar;

import kotlin.Pair;

public class SchedulerReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM_SHOT = "com.silas.meditacao.ALARME_DISPARADO";
    public static final String ACTION_SCHEDULER = "com.silas.meditacao.AGENDADOR";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    public SchedulerReceiver() {
    }

    public static void setAlarm(Context context) {
        Intent myIntent = new Intent(ACTION_ALARM_SHOT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        boolean activeAlarm = (pendingIntent != null);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int minutesPref = preferences.getInt(TimePreference.TIMEPREFERENCE_KEY, TimePreference.DEFAULT_VALUE);
        Pair<Integer, Integer> pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(minutesPref);

        Calendar c = Calendar.getInstance();
        int hours = pair.getFirst();
        int minutes = pair.getSecond();

        long lastNotificationTime = preferences.getLong(NotificationReceiver.NOTIFICATION_TIME_KEY, 0L);

        if ((hours < c.get(Calendar.HOUR_OF_DAY) && DateUtils.isToday(lastNotificationTime)))
            c.add(Calendar.DATE, 1);

        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            if (activeAlarm) {
                Log.i("Alarm", "canceling old alarm");
                alarmManager.cancel(pendingIntent);
            }
            Log.i("Alarm", "setting an alarm at " + pair.toString()
                    + "\n" + c.toString());
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);//set repeating every 24
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null
                && (action.equals(ACTION_BOOT_COMPLETED)
                || action.equals(ACTION_SCHEDULER))) setAlarm(context);
    }
}
