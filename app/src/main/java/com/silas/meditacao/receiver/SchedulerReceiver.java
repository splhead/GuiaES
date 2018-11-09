package com.silas.meditacao.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.silas.meditacao.io.TimePreference;
import com.silas.meditacao.io.TimePreferenceDialogFragmentCompat;

import java.util.Calendar;

import kotlin.Pair;

public class SchedulerReceiver extends BroadcastReceiver {
    public static final String ALARM_SHOT_ACTION = "com.silas.meditacao.ALARME_DISPARADO";
    public static final String SCHEDULER_ACTION = "com.silas.meditacao.AGENDADOR";

    public SchedulerReceiver() {
    }

    public static void setAlarm(Context context) {
        Intent myIntent = new Intent(ALARM_SHOT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        boolean alarmeAtivo = (pendingIntent != null);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int minutesPref = preferences.getInt(TimePreference.TIMEPREFERENCE_KEY, TimePreference.DEFAULT_VALUE);
        Pair<Integer, Integer> pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(minutesPref);

        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, pair.getFirst());
        c.set(Calendar.MINUTE, pair.getSecond());
        c.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            if (alarmeAtivo) {
                Log.i("Alarm", "canceling old alarm");
                alarmManager.cancel(pendingIntent);
            }
            Log.i("Alarm", "setting an alarm at " + pair.toString());
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);//set repeating every 24
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null
                && (action.equals("android.intent.action.BOOT_COMPLETED")
                || action.equals(SCHEDULER_ACTION))) setAlarm(context);
    }
}
