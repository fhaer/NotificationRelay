package de.exr.notificationrelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by felix on 05.07.15.
 */
public class RelayNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RelayNotificationService.launchService(context);
    }

}
