package de.exr.notificationrelay;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat.Builder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by felix on 29.06.15.
 */
public class NotificationReader {

    protected final static String TICKER_TEXT = "Cross User Notification";

    private final static Matcher M_NOT_REC = Pattern.compile("^\\s+NotificationRecord.+").matcher("");
    private final static Matcher M_TIC_TEX = Pattern.compile("^\\s+tickerText=(.+)").matcher("");
    private final static Matcher M_UID = Pattern.compile("^\\s+uid=\\d+ userId=(\\d+)").matcher("");

    private final static Matcher M_CR_USR =
            Pattern.compile(TICKER_TEXT + " \\[(\\d+)\\]").matcher("");

    protected final static String ENABLED_TEST_TICKER_TEXT = "v9x 3lx4 x";
    protected final static int ENABLED_TEST_ID = 1;

    protected static void execSu(String cmd) {
        try {
            // root access
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static Builder execDumpsysNotifications(Context context) {
        try {
            Process process;
            if (MainActivity.IS_SYSTEM_APP) {
                process = Runtime.getRuntime().exec("/system/bin/dumpsys notification");
            } else {
                process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("dumpsys notification\n");
                os.writeBytes("exit\n");
                os.flush();
                os.close();
            }
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String out = "";
            String line = "";

            String tickerText = "";
            int userId = -1;
            int prevNotifications = 0;
            int notifications = 0;

            while( (line=bufferedReader.readLine()) != null)
            {
                if (M_NOT_REC.reset(line).matches()) {
                    // process prev. notification
                    if (userId > 0) {
                        out += "[" + userId + "] " + tickerText + " ";
                        notifications++;
                    }
                    userId = -1;
                } else if (M_TIC_TEX.reset(line).matches()) {
                    tickerText = M_TIC_TEX.group(1);
                    if (tickerText.equals("null")) {
                        tickerText = "";
                    } else if (M_CR_USR.reset(tickerText).matches()) {
                        prevNotifications = Integer.parseInt(M_CR_USR.group(1));
                    } else if (tickerText.equals(ENABLED_TEST_TICKER_TEXT)) {
                        userId = -1;
                        enabledTest(context);
                    }
                } else if (M_UID.reset(line).matches()) {
                    userId = Integer.parseInt(M_UID.group(1));
                }
            }
            if (notifications > prevNotifications) {
                Builder notification = new Builder(context);
                notification.setContentTitle(TICKER_TEXT);
                notification.setTicker(TICKER_TEXT + " [" + notifications +"]");
                notification.setContentText(out);
                return notification;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void enabledTest(Context context) {
        NotificationManager nManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        nManager.cancel(ENABLED_TEST_ID);
    }
}
