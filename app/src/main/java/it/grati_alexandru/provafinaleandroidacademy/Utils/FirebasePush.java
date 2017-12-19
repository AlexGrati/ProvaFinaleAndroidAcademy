package it.grati_alexandru.provafinaleandroidacademy.Utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.grati_alexandru.provafinaleandroidacademy.BottomNavigationActivity;
import it.grati_alexandru.provafinaleandroidacademy.MainActivity;
import it.grati_alexandru.provafinaleandroidacademy.Model.Courier;
import it.grati_alexandru.provafinaleandroidacademy.PackageActivity;
import it.grati_alexandru.provafinaleandroidacademy.R;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class FirebasePush extends Service {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String url = FirebaseRestRequests.BASE_URL;
    private DatabaseReference usersReference = database.getReferenceFromUrl(url);
    private SharedPreferences.Editor editor;
    private ChildEventListener handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        usersReference.removeEventListener(handler);
        Log.i("FIREBASE_SERVICE", "STOP DAY SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("FIREBASE_SERVICE", "START DAY SERVICE");
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("USER","");
        final String type = sharedPreferences.getString("TYPE","");
        url = url +"/Users/"+type+"/"+ username + "/Packages";
        usersReference = database.getReferenceFromUrl(url);
        editor = sharedPreferences.edit();

        handler = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("FIREBASE_SERVICE", "ADD: " + dataSnapshot.getKey());
                if(type.equals("Couriers") && dataSnapshot.exists()){
                    editor.putString("PACKAGE_ID",dataSnapshot.getKey());
                    editor.apply();
                    activePushValidation(""+dataSnapshot.getKey(), "Child Added", BottomNavigationActivity.class);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("FIREBASE_SERVICE", "CHANGE: " + dataSnapshot.getKey());
                if (type.equals("Clients") && dataSnapshot.exists()) {
                    editor.putString("PACKAGE_ID",dataSnapshot.getKey());
                    editor.apply();
                    activePushValidation(""+dataSnapshot.getKey(), "Child Modified", PackageActivity.class);
                }
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("FIREBASE_SERVICE", "REMOVE: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("FIREBASE_SERVICE", "MOVED: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        usersReference.addChildEventListener(handler);
    }

    public void activePushValidation(String commListener, String text, Class c) {
        Intent intent = new Intent(this,c);
        sendNotification(intent, text, commListener);
    }

    public void sendNotification(Intent intent, String title, String body) {
       Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri dsUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setAutoCancel(true);
        builder.setSound(dsUri);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(bitmap);
        builder.setShowWhen(true);
        builder.setContentIntent(activity);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}

