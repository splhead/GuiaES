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
    public SchedulerReceiver() {
    }

    public static void setAlarm(Context context, Intent myIntent, AlarmManager alarmManager) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int minutesPref = preferences.getInt(TimePreference.TIMEPREFERENCE_KEY, TimePreference.DEFAULT_VALUE);
        Pair<Integer,Integer> pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(minutesPref);

        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, pair.getFirst());
        c.set(Calendar.MINUTE, pair.getSecond());
        c.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            Log.i("Alarm", "setting an alarm at " + pair.toString());
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);//set repeating every 24
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && (action.equals("com.silas.meditacao.AGENDADOR")
                || action.equals("android.intent.action.BOOT_COMPLETED"))) {
            Intent myIntent = new Intent("com.silas.meditacao.ALARME_DISPARADO");
//            PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
            PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
            boolean alarmeAtivo = (mPendingIntent != null);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (!alarmeAtivo) {
                //            Log.i("Alarm", "Novo alarm");
                setAlarm(context, myIntent, alarmManager);
            }
        }
    }
}
