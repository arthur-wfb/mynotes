package com.ururu2909.mynotes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.ururu2909.mynotes.model.database.DBHelper;
import com.ururu2909.mynotes.model.database.NoteListService;
import com.ururu2909.mynotes.model.database.NoteService;
import com.ururu2909.mynotes.model.database.StepService;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        new NoteListService(dbHelper);
        new NoteService(dbHelper);
        new StepService(dbHelper);

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
