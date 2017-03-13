package de.exr.notificationrelay;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat.Builder;

/**
 * Created by felix on 05.07.15.
 */
public class RelayNotificationService extends IntentService {

    public RelayNotificationService() {
        super("RelayNotificationService");
    }

    public RelayNotificationService(String name) {
        super(name);
    }

    protected static void launchService(Context context) {
        if (context == null) return;
        context.startService(new Intent(context, RelayNotificationService.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Builder notification = NotificationReader.execDumpsysNotifications(this);
        if (notification!= null) {
            NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            notification.setSmallIcon(R.drawable.abc_ic_menu_share_mtrl_alpha);
            notification.setAutoCancel(true);
            notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            // Because the ID remains unchanged, the existing notification is updated.
            nManager.notify(2, notification.build());
        }
    }

}
