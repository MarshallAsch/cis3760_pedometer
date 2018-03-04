/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.j4velin.pedometer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import de.j4velin.pedometer.ui.Activity_Main;
import de.j4velin.pedometer.util.Logger;
import de.j4velin.pedometer.util.Util;
import de.j4velin.pedometer.widget.WidgetUpdateService;


/*

updates step calculations to account for the ability to be paused

Idea:
 - keep track of the last step number so that it can check how many steps were taken since the last event

 int lastStep = 0;
 bool isPaused = false;

  int current = 0;


  event loop
  {
        current = event.getTotoalSteps()

        if (paused)
            last = current
            continue

       int numTaken = curent - lastStep

       db.save(numTaken, System.getCurrentTime))

       broadcast(dbUpdated)
  }





 */


/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener2 extends Service implements SensorEventListener {

    private final static int NOTIFICATION_ID = 1;
    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_HOUR;
    private final static int SAVE_OFFSET_STEPS = 500;



    public final static String ACTION_PAUSE = "ACTION_PAUSE";
    public final static String ACTION_RESUME = "ACTION_RESUME";
    public final static String ACTION_PAUSE_UI = "ACTION_PAUSE_UI";
    public final static String ACTION_RESUME_UI = "ACTION_RESUME_UI";
    public final static String ACTION_START = "ACTION_START";
    public final static String ACTION_UPDATE_NOTIFICATION = "ACTION_UPDATE_NOTIFICATION";

    public final static String REFRESH_TIME_EXTRA = "REFRESH_TIME_EXTRA";

    public final static String NUM_STEPS_EXTRA = "NUM_STEPS_EXTRA";
    public final static String STEP_TIME_EXTRA = "STEP_TIME_EXTRA";

    public final static String STEPS_BROADCAST = "de.j4velin.pedometer.STEPS_BROADCAST";



    private boolean reportingSlow = false;

    private static int steps;
    private static int lastSaveSteps;
    private static long lastSaveTime;
    private static int lastStep = 0;

    private long refreshTime = AlarmManager.INTERVAL_HOUR;

    private boolean paused = false;
    private static int pauseCount = 0;

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
        if (BuildConfig.DEBUG) Logger.log(sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Logger.log("probably not a real value: " + event.values[0]);
            return;
        } else {

            int cumulativeSteps = (int) event.values[0];
            long timeStamp = event.timestamp;

            if (paused) {
                lastStep = cumulativeSteps;
            }

            int numTaken = cumulativeSteps - lastStep;

            Database db = Database.getInstance(this);

            db.addEntry(numTaken, timeStamp);

            updateIfNecessary();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

            // send a message notifying any listeners that the number steps taken has changed
            Intent intent = new Intent(STEPS_BROADCAST);
            intent.putExtra(NUM_STEPS_EXTRA, numTaken);
            intent.putExtra(STEP_TIME_EXTRA, timeStamp);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void updateIfNecessary() {
        if (steps > lastSaveSteps + SAVE_OFFSET_STEPS ||
                (steps > 0 && System.currentTimeMillis() > lastSaveTime + SAVE_OFFSET_TIME)) {

            if (BuildConfig.DEBUG) Logger.log(
                    "saving steps: steps=" + steps + " lastSave=" + lastSaveSteps +
                            " lastSaveTime=" + new Date(lastSaveTime));

            Database db = Database.getInstance(this);

            if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {

                int pauseDifference = steps - pauseCount;

                db.insertNewDay(Util.getToday(), steps - pauseDifference);

                
                /// FIXME: 2018-02-14 I dont get what is going on with this pause count thing

                if (pauseDifference > 0) {
                    // update pauseCount for the new day

                    pauseCount = steps;

                    getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                            .putInt("pauseCount", steps).commit();
                }
            }

            db.saveCurrentSteps(steps);
            db.close();

            lastSaveSteps = steps;
            lastSaveTime = System.currentTimeMillis();
            updateNotificationState();

            // update the widget
            startService(new Intent(this, WidgetUpdateService.class));
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent == null) {
            Logger.log("The sensor Listener intent is NULL, stopping");
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction() != null ? intent.getAction() : "";


        if (action.equals(ACTION_PAUSE)) {
            // this will pause step counting, any steps that are detected here will be ignored
            paused = true;
        }
        else if (action.equals(ACTION_RESUME)) {
            // this will unpause the step counting, steps at this point can now be counted.
            // this command can also be used to start the service.
            // can include the refresh time.
            paused = false;

            refreshTime = intent.getLongExtra(REFRESH_TIME_EXTRA, AlarmManager.INTERVAL_HOUR);
        }
        else if (action.equals(ACTION_UPDATE_NOTIFICATION)) {
            // possibly also update any other subscribers to the step update.

            updateNotificationState();
        }
        else if (action.equals(ACTION_PAUSE_UI)){

            reportingSlow = true;
        }
        else if (action.equals(ACTION_RESUME_UI)){
            reportingSlow = false;

        }
        else {
            Logger.log("Unknown action: " + action);
        }

        updateIfNecessary();

        // restart service every hour to save the current step count
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC,
                        System.currentTimeMillis() + refreshTime, PendingIntent
                        .getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener2.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Logger.log("SensorListener onCreate");

        reRegisterSensor();
        updateNotificationState();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (BuildConfig.DEBUG) Logger.log("sensor service task removed");

        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorListener2.class), 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Logger.log("SensorListener onDestroy");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
    }

    private void updateNotificationState() {
        if (BuildConfig.DEBUG) Logger.log("SensorListener updateNotificationState");


        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (prefs.getBoolean("notification", true)) {

            int goal = prefs.getInt("goal", 10000);
            Database db = Database.getInstance(this);
            int today_offset = db.getSteps(Util.getToday());


            if (steps == 0)
                steps = db.getCurrentSteps(); // use saved value if we haven't anything better
            db.close();

            Notification.Builder notificationBuilder = new Notification.Builder(this);

            // this will set the progress bar in the notification
            if (steps > 0) {
                if (today_offset == Integer.MIN_VALUE) today_offset = -steps;

                String text;
                if (today_offset + steps >= goal) {
                    // show the number of steps taken
                    text = getString(R.string.goal_reached_notification,
                            NumberFormat.getInstance(Locale.getDefault()).format((today_offset + steps)));
                }
                else {
                    // show num steps until goal is reached
                    text = getString(R.string.notification_text,
                            NumberFormat.getInstance(Locale.getDefault()).format((goal - today_offset - steps)));

                }

                notificationBuilder.setProgress(goal, today_offset + steps, false)
                        .setContentText(text);

            } else { // still no step value?
                notificationBuilder
                        .setContentText(getString(R.string.your_progress_will_be_shown_here_soon));
            }

            // moved these out of the chunk below for readbility
            String title = paused ?  getString(R.string.ispaused): getString(R.string.notification_title);
            int icon = paused ? R.drawable.ic_resume : R.drawable.ic_pause;
            String title2 = paused ? getString(R.string.resume) : getString(R.string.pause);


            // add more things to the notification
            notificationBuilder
                    .setPriority(Notification.PRIORITY_MIN)
                    .setShowWhen(false)
                    .setContentTitle(title)
                    .setContentIntent(PendingIntent
                    .getActivity(this, 0, new Intent(this, Activity_Main.class),
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.drawable.ic_notification)
                    .addAction(icon,
                            title2,
                            PendingIntent.getService(this, 4, new Intent(this, SensorListener2.class)
                            .setAction(ACTION_PAUSE),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setOngoing(true);


            nm.notify(NOTIFICATION_ID, notificationBuilder.build());
        } else {
            nm.cancel(NOTIFICATION_ID);
        }
    }

    private void reRegisterSensor() {
        if (BuildConfig.DEBUG) Logger.log("re-register sensor listener");

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Logger.log("step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size());
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) return; // emulator
            Logger.log("default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }

        int sensDelay = reportingSlow ? SensorManager.SENSOR_DELAY_NORMAL : SensorManager.SENSOR_DELAY_UI;
        int batchTime = reportingSlow ? (int) (5 * MICROSECONDS_IN_ONE_MINUTE) : 0;

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), sensDelay, batchTime);
    }
}
