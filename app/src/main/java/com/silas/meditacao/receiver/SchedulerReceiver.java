package com.silas.meditacao.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.silas.meditacao.io.TimePreference;
import com.silas.meditacao.io.TimePreferenceDialogFragmentCompat;

import java.util.Calendar;

import kotlin.Pair;

public class SchedulerReceiver extends BroadcastReceiver {
    //    public static final String ALARM_SHOT_ACTION = "com.silas.meditacao.ALARME_DISPARADO";
    public static final String SCHEDULER_ACTION = "com.silas.meditacao.AGENDADOR";
    private static Calendar now = Calendar.getInstance();

    public SchedulerReceiver() {
    }

    public static void setAlarm(Context context) {
        setAlarm(context, false);
    }

    public static void setAlarm(Context context, boolean nextDay) {
        Intent myIntent = new Intent(context, NotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        boolean alarmeAtivo = (pendingIntent != null);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (alarmeAtivo) {
//                Log.i("Alarm", "canceling old alarm");
                alarmManager.cancel(pendingIntent);
            }

            long time = getTimeToNotification(context, nextDay);
//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(now.getTimeInMillis(),
//                        pendingIntent), pendingIntent);
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//                Log.i("Alarm", "setou o alarm no android 9");
            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                        AlarmManager.INTERVAL_DAY, pendingIntent);//set repeating every 24
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null
                && (action.equals("android.intent.action.BOOT_COMPLETED")
                || action.equals(SCHEDULER_ACTION))) setAlarm(context);
    }

    public static Long getTimeToNotification(Context context, boolean nextDay) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int minutesPref = preferences.getInt(TimePreference.TIMEPREFERENCE_KEY, TimePreference.DEFAULT_VALUE);
        Pair<Integer, Integer> pair = TimePreferenceDialogFragmentCompat.hoursAndMinutes(minutesPref);
//        Log.i("Alarm", "setting an alarm at " + pair.toString());


        if (nextDay) {
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        now.set(Calendar.HOUR_OF_DAY, pair.getFirst());
        now.set(Calendar.MINUTE, pair.getSecond());
        now.set(Calendar.SECOND, 0);

        return now.getTimeInMillis();
    }
}
