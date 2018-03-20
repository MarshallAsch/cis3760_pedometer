package de.j4velin.pedometer.ui;

/**
 * Created by Joshua on 3/13/2018.
 */

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.support.v4.app.NotificationCompat;
        import android.support.v7.app.ActionBarActivity;
        import android.view.View;
        import android.app.NotificationChannel;
        import android.content.Intent;
        import android.content.Context;
        import de.j4velin.pedometer.R;

        

public class MyNotification extends AppCompatActivity {

        NotificationCompat.Builder notification;
        private static final int uniqueID = 45612;
        //public static final String CHANNEL_ID = "my_ch_id";
        //NotificationChannel mChannel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.fragment_overview);
                //mChannel = new NotificationChannel(CHANNEL_ID, "myChannel", NotificationManager.IMPORTANCE_LOW);

                notification = new NotificationCompat.Builder(this);
                notification.setAutoCancel(true);
        }


        public void sendNotification(View view) {
                notification.setSmallIcon(R.drawable.ic_launcher);
                notification.setTicker("This is the ticker");
                notification. setWhen(System.currentTimeMillis());
                notification.setContentTitle("Here is the title");
                notification.setContentText("I am the body text of your notification");

                Intent intent = new Intent(this, de.j4velin.pedometer.ui.Fragment_Overview.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);

                //Builds notification and issues it
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(uniqueID, notification.build());
        }
}
