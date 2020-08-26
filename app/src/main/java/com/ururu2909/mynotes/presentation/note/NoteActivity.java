package com.ururu2909.mynotes.presentation.note;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.database.NoteService;
import com.ururu2909.mynotes.model.entities.Note;

public class NoteActivity extends BaseNoteActivity implements CompoundButton.OnCheckedChangeListener {

    int noteId;
    int noteListId;
    ImageView noteImage;
    TextView noteTitle;
    CheckBox checkBox;
    TextView explanationText;
    TextView completionDateText;
    TextView notificationDateText;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Intent intent = getIntent();
        noteId = intent.getIntExtra("note_id", -1);
        noteListId = intent.getIntExtra("note_list_id", -1);
        String noteListName = intent.getStringExtra("note_list_name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.setTitle(noteListName);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        noteImage = findViewById(R.id.noteImage);
        noteTitle = findViewById(R.id.noteTitle);
        checkBox = findViewById(R.id.checkBox);
        explanationText = findViewById(R.id.explanationText);
        completionDateText = findViewById(R.id.completionDateText);
        notificationDateText = findViewById(R.id.notificationDateText);

        checkBox.setOnCheckedChangeListener(this);

        setNoteStepsRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadStepsTask().execute(noteId);
        new LoadNoteTask().execute(noteId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_delete:
                new DeleteNoteTask().execute(noteId);
                finish();
                break;
            case R.id.action_edit:
                intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra("note_id", noteId);
                intent.putExtra("note_list_id", noteListId);
                intent.putExtra("purpose", getString(R.string.edit_purpose));
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        new UpdateNoteCompletionTask().execute(isChecked);
    }

    private void setNoteStepsRecyclerView(){
        recyclerView = findViewById(R.id.stepsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stepsAdapter = new StepsAdapter(this);
        recyclerView.setAdapter(stepsAdapter);
    }

    class LoadNoteTask extends AsyncTask<Integer, Void, Note>{

        @Override
        protected Note doInBackground(Integer... integers) {
            return NoteService.getNoteById(integers[0]);
        }

        @Override
        protected void onPostExecute(Note note) {
            super.onPostExecute(note);
            noteTitle.setText(note.getTitle());
            if (note.isComplete() == 1){
                noteTitle.setPaintFlags(noteTitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setChecked(true);
            } else {
                noteTitle.setPaintFlags(noteTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                checkBox.setChecked(false);
            }
            explanationText.setText(note.getExplanationText());
            completionDateText.setText(note.getCompletionDate());
            notificationDateText.setText(note.getNotificationDate());
            byte[] image = note.getImage();
            if (image != null){
                noteImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }

            if (explanationText.getText().toString().equals("")){
                explanationText.setVisibility(View.GONE);
            } else {
                explanationText.setVisibility(View.VISIBLE);
            }
            if (completionDateText.getText().toString().equals("")){
                completionDateText.setVisibility(View.GONE);
            } else {
                completionDateText.setVisibility(View.VISIBLE);
            }
            if (notificationDateText.getText().toString().equals("")){
                notificationDateText.setVisibility(View.GONE);
            } else {
                notificationDateText.setVisibility(View.VISIBLE);
            }
        }
    }

    class UpdateNoteCompletionTask extends AsyncTask<Boolean, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            NoteService.updateNoteCompletion(noteId, booleans[0]);
            return booleans[0];
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b){
                noteTitle.setPaintFlags(noteTitle.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setChecked(true);
            } else {
                noteTitle.setPaintFlags(noteTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                checkBox.setChecked(false);
            }
        }
    }

    class DeleteNoteTask extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            NoteService.deleteNoteById(integers[0]);
            return null;
        }
    }
}
