package com.ururu2909.mynotes.presentation.notelist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.database.NoteListService;
import com.ururu2909.mynotes.model.database.NoteService;
import com.ururu2909.mynotes.model.entities.Note;
import com.ururu2909.mynotes.presentation.EnterTextDialogFragment;
import com.ururu2909.mynotes.presentation.TextListener;
import com.ururu2909.mynotes.presentation.mainscreen.MainActivity;
import com.ururu2909.mynotes.presentation.note.EditNoteActivity;
import com.ururu2909.mynotes.presentation.note.NoteActivity;

import java.util.ArrayList;

public class NotesListActivity extends AppCompatActivity implements NotesAdapter.OnNoteListener,
                                                                    NotesAdapter.OnNoteCheckedListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotesAdapter notesAdapter;
    ArrayList<Note> notes;
    int noteListId;
    String noteListName;
    ActionBar actionbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        noteListId = getIntent().getIntExtra("note_list_id", -1);
        recyclerView = findViewById(R.id.notesRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new NotesAdapter(this, this);
        recyclerView.setAdapter(notesAdapter);

        FloatingActionButton addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditNoteActivity.class);
                intent.putExtra("note_list_id", noteListId);
                intent.putExtra("purpose", getString(R.string.create_purpose));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadNotesTask().execute(noteListId);
        switch (noteListId){
            case MainActivity.PLANED_LIST_ID:
                actionbar.setTitle(getString(R.string.planed));
                break;
            case MainActivity.NOTES_LIST_ID:
                actionbar.setTitle(getString(R.string.notes));
                break;
            default:
                new LoadNoteListName().execute(noteListId);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (noteListId != MainActivity.NOTES_LIST_ID && noteListId != MainActivity.PLANED_LIST_ID){
            getMenuInflater().inflate(R.menu.notes_list_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.sort_direction, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_rename:
                new EnterTextDialogFragment(new TextListener() {
                    @Override
                    public void onEntered(String text) {
                        new UpdateListNameTask().execute(text);
                        Toast.makeText(NotesListActivity.this, R.string.list_edited, Toast.LENGTH_SHORT)
                                .show();
                    }
                }).show(getSupportFragmentManager(), "rename notes list");
                break;
            case R.id.action_sort_direction:
                ArrayList<Note> newNotes = new ArrayList<>();
                for (int i = 0; i < notes.size(); i++) {
                    newNotes.add(notes.get(notes.size() - i - 1));
                }
                notesAdapter.submitList(newNotes);
                notes = newNotes;
                break;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_activity", getClass().getName());
        editor.putInt("note_list_id", noteListId);
        editor.apply();
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note_id", notes.get(position).getId());
        intent.putExtra("note_list_id", notes.get(position).getNoteListId());
        if (noteListName == null){
            noteListName = getString(noteListId == MainActivity.NOTES_LIST_ID ? R.string.notes : R.string.planed);
        }
        intent.putExtra("note_list_name", noteListName);
        startActivity(intent);
    }

    @Override
    public void OnNoteChecked(int position, boolean isChecked) {
        NoteService.updateNoteCompletion(notes.get(position).getId(), isChecked);
        new LoadNotesTask().execute(noteListId);
    }

    class LoadNotesTask extends AsyncTask<Integer, Void, ArrayList<Note>>{

        @Override
        protected ArrayList<Note> doInBackground(Integer... integers) {
            return NoteService.getNotesByListId(integers[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Note> newNotes) {
            super.onPostExecute(newNotes);
            notes = newNotes;
            notesAdapter.submitList(newNotes);
        }
    }

    class LoadNoteListName extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            return NoteListService.getNoteListName(integers[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (actionbar != null){
                actionbar.setTitle(s);
            }
            noteListName = s;
        }
    }

    class UpdateListNameTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            NoteListService.updateListName(noteListId, strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new LoadNoteListName().execute(noteListId);
        }
    }
}
