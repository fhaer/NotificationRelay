package de.exr.notificationrelay;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    protected final static boolean IS_SYSTEM_APP = true;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
    }

    public void buttonClicked(View v) {
        if(v.getId() == R.id.buttonEnable) {
            enable();
        } else if(v.getId() == R.id.buttonDisable) {
            disable();
        } else if(v.getId() == R.id.buttonTest) {
            sendTestNotification();
        }
    }

    private void enable() {
        cancelAlarm();
        if (!IS_SYSTEM_APP)
            prepareRoot();
        setAlarm();
        sendTestNotification();
    }

    private void sendTestNotification() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("NotificationRelay");
        notification.setContentText("Test Message");
        notification.setTicker(NotificationReader.ENABLED_TEST_TICKER_TEXT);
        NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notification.setSmallIcon(R.drawable.abc_ic_menu_share_mtrl_alpha);
        notification.setAutoCancel(false);
        // Because the ID remains unchanged, the existing notification is updated.
        nManager.notify(NotificationReader.ENABLED_TEST_ID, notification.build());
    }

    private void disable() {
        cancelAlarm();
        Toast.makeText(this, "NotificationRelay disabled", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        Context context = this;
        Intent myIntent = new Intent(context, RelayNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 2); // first time
        long frequency= Integer.parseInt(editText.getText().toString()) * 1000; // in ms
        // inexact alarm: Your alarm's first trigger will not be before the requested time,
        // but it might not occur for almost a full interval after that time.
        // RTC: wakeup device if asleep
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, RelayNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private static void prepareRoot() {
        NotificationReader.execSu("echo -n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings)
        //    return true;
        return super.onOptionsItemSelected(item);
    }
}
