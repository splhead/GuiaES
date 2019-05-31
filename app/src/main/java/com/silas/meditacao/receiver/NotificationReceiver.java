package com.silas.meditacao.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.activity.LauncherActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.models.Meditacao;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "com.silas.meditacao";
    public static final String NOTIFICATION_TIME_KEY = "notification_time";
    private Context mContext;
    private SharedPreferences preferences;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);


        Meditacao meditacao = getTodayPreferredDevotional();
        if (meditacao != null) {
            String title = meditacao.getTitulo();
            String text = meditacao.getTextoBiblico();
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(title)
                            .setContentText(text)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(text));

            Intent resultIntent = new Intent(context, LauncherActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);

            Notification n = mBuilder.build();
            n.flags = Notification.FLAG_AUTO_CANCEL;

            // Sets an ID for the notification
            int mNotificationId = 1;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(mNotificationId, n);
                preferences.edit()
                        .putLong(NOTIFICATION_TIME_KEY, System.currentTimeMillis())
                        .apply();
            }
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }

    private Meditacao getTodayPreferredDevotional() {
        Calendar today = Calendar.getInstance();
        MeditacaoDBAdapter meditacaoDBAdapter = new MeditacaoDBAdapter(mContext);
        return meditacaoDBAdapter.buscaMeditacao(today, getPreferredDevotionalType());
    }

    private int getPreferredDevotionalType() {
        String type = preferences
                .getString(Preferences.TYPE_DEFAULT, "0");
        return type != null ? Integer.parseInt(type) + 1 : 1;
    }
}
