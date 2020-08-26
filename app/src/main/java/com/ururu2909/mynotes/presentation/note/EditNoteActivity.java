package com.ururu2909.mynotes.presentation.note;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.database.NoteListService;
import com.ururu2909.mynotes.model.database.NoteService;
import com.ururu2909.mynotes.model.database.StepService;
import com.ururu2909.mynotes.model.entities.Note;
import com.ururu2909.mynotes.model.entities.NoteList;
import com.ururu2909.mynotes.model.entities.Step;
import com.ururu2909.mynotes.presentation.AlarmReceiver;
import com.ururu2909.mynotes.presentation.EnterTextDialogFragment;
import com.ururu2909.mynotes.presentation.TextListener;
import com.ururu2909.mynotes.presentation.mainscreen.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditNoteActivity extends BaseNoteActivity {

    ActionBar actionBar;
    Spinner noteListSpinner;
    ArrayAdapter<String> adapter;
    ArrayList<NoteList> noteLists;
    EditText noteTitle;
    EditText explanationText;
    ImageView noteImage;
    TextView completionDateText;
    TextView notificationDateText;
    Button completionDateButton;
    Button notificationDateButton;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    int noteListId;
    int noteId;
    String purpose;
    Bitmap noteImageBitmap;
    byte[] image;
    final int RESULT_LOAD_IMAGE = 1;
    final int PERMISSION_CODE = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Intent intent = getIntent();
        noteListId = intent.getIntExtra("note_list_id", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        final ScrollView scrollView = findViewById(R.id.scrollview);
        noteTitle = findViewById(R.id.noteTitle);
        explanationText = findViewById(R.id.explanationText);
        noteImage = findViewById(R.id.noteImage);
        completionDateText = findViewById(R.id.completionDateText);
        notificationDateText = findViewById(R.id.notificationDateText);
        completionDateButton = findViewById(R.id.completionDateButton);
        notificationDateButton = findViewById(R.id.notificationDateButton);
        noteListSpinner = findViewById(R.id.noteListSpinner);

        setDatePickerButtons(completionDateButton, notificationDateButton);
        setNoteStepsRecyclerView();

        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>());
        noteListSpinner.setAdapter(adapter);

        purpose = intent.getStringExtra("purpose");
        noteId = intent.getIntExtra("note_id", -1);
        new LoadNoteListTask().execute();

        if (purpose != null){
            if (purpose.equals(getString(R.string.create_purpose))){
                actionBar.setTitle(getString(R.string.new_note));
            } else if (purpose.equals(getString(R.string.edit_purpose))){
                actionBar.setTitle(getString(R.string.editing));
                new LoadNoteTask().execute(noteId);
                new LoadStepsTask().execute(noteId);
            }
        }

        ImageButton editImageButton = findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });

        Button addStepButton = findViewById(R.id.addStepButton);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                EnterTextDialogFragment dialogFragment = new EnterTextDialogFragment(new TextListener() {
                    @Override
                    public void onEntered(String text) {
                        Step step = new Step(text, 0, noteId);
                        new InsertStepTask().execute(step);
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "create step");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cancel:
                StepService.deleteExcessSteps();
                finish();
                break;
            case R.id.action_apply:
                if (noteTitle.getText().toString().equals("")){
                    Toast.makeText(this, R.string.enter_note_title, Toast.LENGTH_SHORT).show();
                } else {
                    Note note = new Note(
                            noteTitle.getText().toString(),
                            explanationText.getText().toString(),
                            0,
                            image,
                            completionDateText.getText().toString(),
                            notificationDateText.getText().toString(), 0, 0,
                            noteLists.get(noteListSpinner.getSelectedItemPosition()).getId()
                    );
                    if (purpose.equals(getString(R.string.create_purpose))){
                        new InsertNoteTask().execute(note);
                        Toast.makeText(this, R.string.note_created, Toast.LENGTH_SHORT).show();
                    } else if (purpose.equals(getString(R.string.edit_purpose)) && noteId != -1){
                        note.setId(noteId);
                        new UpdateNoteTask().execute(note);
                        if (!note.getNotificationDate().equals("")){
                            setNotification(note.getId(), note.getNotificationDate());
                        }
                        Toast.makeText(this, R.string.note_edited, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                break;
        }
        return true;
    }

    private void setNoteStepsRecyclerView(){
        recyclerView = findViewById(R.id.stepsRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stepsAdapter = new StepsAdapter(this);
        recyclerView.setAdapter(stepsAdapter);
    }

    private void setDatePickerButtons(Button completionDateButton, final Button notificationDateButton){
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener completionDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String dayOfMonthString = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String monthOfYearString = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);
                String date = dayOfMonthString + "." + monthOfYearString + "." + year;
                completionDateText.setText(date);
            }
        };

        final TimePickerDialog.OnTimeSetListener notoficationTimeListener =new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourOfDayString = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
                String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
                String time = " " + hourOfDayString + ":" + minuteString;
                notificationDateText.append(time);
            }
        };


        final DatePickerDialog.OnDateSetListener notificationDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String dayOfMonthString = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String monthOfYearString = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);
                String date = dayOfMonthString + "." + monthOfYearString + "." + year;
                notificationDateText.setText(date);
                new TimePickerDialog(EditNoteActivity.this, notoficationTimeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true)
                        .show();
            }
        };

        completionDateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditNoteActivity.this, completionDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        notificationDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditNoteActivity.this, notificationDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();

            }
        });
    }

    private void setNotification(int id, String stringDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).parse(stringDate);
            if (date != null){
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (System.currentTimeMillis() < calendar.getTimeInMillis()){
            Context context = getApplicationContext();
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("note_id", id);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager != null){
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        }
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private Bitmap reduceImage(Bitmap image){
        return Bitmap.createScaledBitmap(image, 500, 500, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            if (selectedImage != null){
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    noteImageBitmap = BitmapFactory.decodeFile(picturePath);
                    noteImageBitmap = reduceImage(noteImageBitmap);
                    noteImage.setImageBitmap(noteImageBitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }
        }

    }

    class LoadNoteListTask extends AsyncTask<Void, Void, ArrayList<NoteList>>{
        @Override
        protected ArrayList<NoteList> doInBackground(Void... voids) {
            return NoteListService.getNoteLists();
        }

        @Override
        protected void onPostExecute(ArrayList<NoteList> newNoteLists) {
            super.onPostExecute(newNoteLists);
            newNoteLists.add(0, new NoteList(MainActivity.NOTES_LIST_ID, "Не выбрано"));
            noteLists = newNoteLists;
            ArrayList<String> noteListLabels = new ArrayList<>();
            for (NoteList noteList : newNoteLists){
                noteListLabels.add(noteList.getName());
            }
            adapter.addAll(noteListLabels);
            for (int i=0; i<noteLists.size(); i++){
                if (noteLists.get(i).getId() == noteListId){
                    noteListSpinner.setSelection(i);
                }
            }
        }
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
            explanationText.setText(note.getExplanationText());
            completionDateText.setText(note.getCompletionDate());
            notificationDateText.setText(note.getNotificationDate());
            for (int i=0; i<noteLists.size(); i++){
                if (noteLists.get(i).getId() == noteListId){
                    noteListSpinner.setSelection(i);
                }
            }
            image = note.getImage();
            if (image != null){
                noteImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
        }
    }

    class InsertNoteTask extends AsyncTask<Note, Void, Void>{
        @Override
        protected Void doInBackground(Note... notes) {
            int id = (int) NoteService.insertNote(notes[0]);
            if (noteImageBitmap != null){
                NoteService.setNoteImage(id, noteImageBitmap);
            }
            if (!notes[0].getNotificationDate().equals("")){
                setNotification(id, notes[0].getNotificationDate());
            }
            StepService.setNoteIdForNewSteps(id);
            return null;
        }

    }

    class InsertStepTask extends AsyncTask<Step, Void, Integer>{

        @Override
        protected Integer doInBackground(Step... steps) {
            StepService.insertStep(steps[0]);
            return steps[0].getNoteId();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            new LoadStepsTask().execute(integer);
        }
    }

    class UpdateNoteTask extends AsyncTask<Note, Void, Void>{
        @Override
        protected Void doInBackground(Note... notes) {
            NoteService.updateNote(notes[0]);
            if (noteImageBitmap != null){
                NoteService.setNoteImage(notes[0].getId(), noteImageBitmap);
            }
            return null;
        }
    }
}
