package com.ururu2909.mynotes.presentation.note;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.ururu2909.mynotes.model.database.StepService;
import com.ururu2909.mynotes.model.entities.Step;

import java.util.ArrayList;

public class BaseNoteActivity extends AppCompatActivity implements StepsAdapter.OnStepCheckedListener {

    StepsAdapter stepsAdapter;
    ArrayList<Step> steps;

    @Override
    public void OnStepChecked(int position, boolean isChecked) {
        StepService.updateStepCompletion(steps.get(position).getId(), isChecked);
        new LoadStepsTask().execute(steps.get(position).getNoteId());
    }

    class LoadStepsTask extends AsyncTask<Integer, Void, ArrayList<Step>> {

        @Override
        protected ArrayList<Step> doInBackground(Integer... integers) {
            return StepService.getStepsByNoteId(integers[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Step> newSteps) {
            super.onPostExecute(newSteps);
            steps = newSteps;
            stepsAdapter.submitList(newSteps);
        }
    }
}
