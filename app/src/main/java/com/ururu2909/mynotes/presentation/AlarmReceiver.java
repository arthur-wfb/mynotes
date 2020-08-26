package com.ururu2909.mynotes.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.database.NoteService;
import com.ururu2909.mynotes.model.entities.Note;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int noteId = intent.getIntExtra("note_id", -1);
        Note note =NoteService.getNoteById(noteId);
        String title = note.getTitle();
        String explanationText = note.getExplanationText();
        byte[] image = note.getImage();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_1")
                .setSmallIcon(R.drawable.ic_foreground_launcher)
                .setContentTitle(title)
                .setContentText(explanationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (image != null){
            builder.setLargeIcon(BitmapFactory.decodeByteArray(image, 0, image.length));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(noteId, builder.build());
    }
}
