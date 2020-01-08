package com.silas.meditacao.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.activity.LauncherActivity;
import com.silas.meditacao.adapters.MeditacaoDBAdapter;
import com.silas.meditacao.io.Preferences;
import com.silas.meditacao.models.Meditacao;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    public final String CHANNEL_ID = "com.silas.meditacao";
    private Context mContext;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("AlarmNotification", "recebeu");
        mContext = context;
        Meditacao meditacao = getTodayPreferedDevotional();
        if (meditacao != null) {
            String title = meditacao.getTitulo();
            String text = meditacao.getTextoBiblico();


            Intent resultIntent = new Intent(context, LauncherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(title)
                            .setContentText(text)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(text))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true)
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            Notification notification = mBuilder.build();
//            n.flags = Notification.FLAG_AUTO_CANCEL;

            // Sets an ID for the notification
            int mNotificationId = 1;
            // Gets an instance of the NotificationManager service
            NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(context);
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
//            if (mNotifyMgr != null) {
            createNotificationChannel();
            mNotifyMgr.notify(mNotificationId, notification);
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SchedulerReceiver.setAlarm(context, true);
//                Log.i("AlarmNotification", "new alarm scheduled");
            }
//            Log.i("AlarmNotification", "Era para notificar");
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }

    /*private Pair<String, String> getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 13) {
            return new Pair<>("Bom dia!", "Que tal começar o dia bem!");
        } else if (hour < 19) {
            return new Pair<>("Boa tarde!", "Que bom que você reservou um tempo para mim!");
        } else {
            return new Pair<>("Boa noite!", "Como foi seu dia? Agora vai ficar melhor!");
        }
    }*/

    private Meditacao getTodayPreferedDevotional() {
        Calendar today = Calendar.getInstance();
        MeditacaoDBAdapter meditacaoDBAdapter = new MeditacaoDBAdapter(mContext);
        return meditacaoDBAdapter.buscaMeditacao(today, getPreferedDevotionalType());
    }

    private int getPreferedDevotionalType() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(Preferences.TYPE_DEFAULT, "0")) + 1;
    }
}
