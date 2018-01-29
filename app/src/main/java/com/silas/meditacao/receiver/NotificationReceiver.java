package com.silas.meditacao.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.silas.guiaes.activity.R;
import com.silas.meditacao.activity.MainActivity;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    public final String CHANNEL_ID = "com.silas.meditacao";
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String[] msg = getGreeting();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(msg[0])
                        .setContentText(msg[1]);

        Intent resultIntent = new Intent(context, MainActivity.class);

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
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notification : R.mipmap.ic_launcher;
    }

    private String[] getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 13) {
            return new String[]{"Bom dia!", "Que tal começar o dia bem!"};
        } else if (hour < 19) {
            return new String[]{"Boa tarde!", "Que bom que você reservou um tempo para mim!"};
        } else {
            return new String[]{"Boa noite!", "Como foi seu dia? Agora vai ficar melhor!"};
        }
    }
}
