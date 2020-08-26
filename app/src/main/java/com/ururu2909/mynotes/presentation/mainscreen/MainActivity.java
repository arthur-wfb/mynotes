package com.ururu2909.mynotes.presentation.mainscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.database.NoteListService;
import com.ururu2909.mynotes.model.entities.NoteList;
import com.ururu2909.mynotes.presentation.EnterTextDialogFragment;
import com.ururu2909.mynotes.presentation.TextListener;
import com.ururu2909.mynotes.presentation.note.EditNoteActivity;
import com.ururu2909.mynotes.presentation.notelist.NotesListActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteListsAdapter.OnNoteListListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NoteListsAdapter noteListsAdapter;
    ArrayList<NoteList> noteLists;
    final public static int PLANED_LIST_ID = -2;
    final public static int NOTES_LIST_ID = -3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.noteListsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        noteListsAdapter = new NoteListsAdapter(this);
        recyclerView.setAdapter(noteListsAdapter);
        new LoadNoteListsTask().execute();

        TextView plannedNotesList = findViewById(R.id.planed_list);
        plannedNotesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesListActivity.class);
                intent.putExtra("note_list_id", PLANED_LIST_ID);
                startActivity(intent);
            }
        });

        TextView notesList = findViewById(R.id.notesList);
        notesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesListActivity.class);
                intent.putExtra("note_list_id", NOTES_LIST_ID);
                startActivity(intent);
            }
        });

        FloatingActionButton addNoteListButton = findViewById(R.id.addListButton);
        addNoteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addListDialogFragment = new EnterTextDialogFragment(new TextListener() {
                    @Override
                    public void onEntered(String text) {
                        new InsertNoteListTask().execute(text);
                        Toast.makeText(MainActivity.this, R.string.list_created, Toast.LENGTH_SHORT).show();
                    }
                });
                addListDialogFragment.show(getSupportFragmentManager(), "create note list");
            }
        });

        FloatingActionButton addNoteButton = findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("note_list_id", NOTES_LIST_ID);
                intent.putExtra("purpose", getString(R.string.create_purpose));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_direction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort_direction) {
            ArrayList<NoteList> newNoteLists = new ArrayList<>();
            for (int i = 0; i < noteLists.size(); i++) {
                newNoteLists.add(noteLists.get(noteLists.size() - i - 1));
            }
            noteListsAdapter.submitList(newNoteLists);
            noteLists = newNoteLists;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_activity", getClass().getName());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onNoteListClick(int position) {
        Intent intent = new Intent(this, NotesListActivity.class);
        intent.putExtra("note_list_id", noteLists.get(position).getId());
        startActivity(intent);
    }

    class LoadNoteListsTask extends AsyncTask<Void, Void, ArrayList<NoteList>>{
        @Override
        protected ArrayList<NoteList> doInBackground(Void... voids) {
            return NoteListService.getNoteLists();
        }

        @Override
        protected void onPostExecute(ArrayList<NoteList> newNoteLists) {
            super.onPostExecute(newNoteLists);
            noteLists = newNoteLists;
            noteListsAdapter.submitList(newNoteLists);
        }
    }

    class InsertNoteListTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            NoteListService.insertNoteList(new NoteList(strings[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new LoadNoteListsTask().execute();
        }
    }
}
