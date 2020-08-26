package com.ururu2909.mynotes.presentation.note;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.entities.Step;


public class StepsAdapter extends ListAdapter<Step, StepsAdapter.StepItemViewHolder> {

    private OnStepCheckedListener mOnStepCheckedListener;

    StepsAdapter(OnStepCheckedListener mOnStepCheckedListener) {
        super(DIFF_CALLBACK);
        this.mOnStepCheckedListener = mOnStepCheckedListener;
    }

    @NonNull
    @Override
    public StepItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_step, parent, false);
        return new StepItemViewHolder(view, mOnStepCheckedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StepItemViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    private static final DiffUtil.ItemCallback<Step> DIFF_CALLBACK = new DiffUtil.ItemCallback<Step>() {
        @Override
        public boolean areItemsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Step oldItem, @NonNull Step newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.isComplete() == newItem.isComplete() &&
                    oldItem.getNoteId() == newItem.getNoteId();
        }
    };

    static class StepItemViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        TextView title;
        CheckBox checkBox;
        OnStepCheckedListener onStepCheckedListener;

        StepItemViewHolder(View itemView, OnStepCheckedListener onStepCheckedListener) {
            super(itemView);
            this.onStepCheckedListener = onStepCheckedListener;
            title = itemView.findViewById(R.id.stepTitle);
            checkBox = itemView.findViewById(R.id.stepCheckBox);
            checkBox.setOnCheckedChangeListener(this);
        }

        void bindTo(Step step) {
            if (step.isComplete() == 1){
                title.setPaintFlags(title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setChecked(true);
            } else {
                title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                checkBox.setChecked(false);
            }
            title.setText(step.getTitle());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                onStepCheckedListener.OnStepChecked(position, isChecked);
            }
        }
    }

    interface OnStepCheckedListener {
        void OnStepChecked(int position, boolean isChecked);
    }
}
