package com.ururu2909.mynotes.presentation.notelist;

import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.entities.Note;

import java.util.Arrays;


public class NotesAdapter extends ListAdapter<Note, NotesAdapter.NoteItemViewHolder> {

    private OnNoteListener mOnNoteListener;
    private OnNoteCheckedListener mOnNoteCheckedListener;

    NotesAdapter(OnNoteListener mOnNoteListener, OnNoteCheckedListener mOnNoteCheckedListener) {
        super(DIFF_CALLBACK);
        this.mOnNoteListener = mOnNoteListener;
        this.mOnNoteCheckedListener = mOnNoteCheckedListener;
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_note, parent, false);
        return new NoteItemViewHolder(view, mOnNoteListener, mOnNoteCheckedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteItemViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getExplanationText().equals(newItem.getExplanationText()) &&
                    Arrays.equals(oldItem.getImage(), newItem.getImage()) &&
                    oldItem.isComplete() == newItem.isComplete() &&
                    oldItem.getCompletionDate().equals(newItem.getCompletionDate()) &&
                    oldItem.getNotificationDate().equals(newItem.getNotificationDate()) &&
                    oldItem.getCompletedSteps() == newItem.getCompletedSteps() &&
                    oldItem.getAllSteps() == newItem.getAllSteps() &&
                    oldItem.getNoteListId() == newItem.getNoteListId();
        }
    };

    static class NoteItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        TextView title;
        ImageView noteImage;
        TextView completionDate;
        TextView progress;
        OnNoteListener onNoteListener;
        CheckBox checkBox;
        NotesAdapter.OnNoteCheckedListener onNoteCheckedListener;

        NoteItemViewHolder(View itemView, OnNoteListener onNoteListener,
                           OnNoteCheckedListener onNoteCheckedListener) {
            super(itemView);
            this.onNoteListener = onNoteListener;
            this.onNoteCheckedListener = onNoteCheckedListener;
            title = itemView.findViewById(R.id.titleInFragment);
            noteImage = itemView.findViewById(R.id.noteImageInFragment);
            completionDate = itemView.findViewById(R.id.dateInFragment);
            progress = itemView.findViewById(R.id.progress);
            checkBox = itemView.findViewById(R.id.checkBoxInFragment);
            checkBox.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);
        }

        void bindTo(Note note) {
            if (note.isComplete() == 1){
                title.setPaintFlags(title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                checkBox.setChecked(true);
            } else {
                title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                checkBox.setChecked(false);
            }
            title.setText(note.getTitle());
            completionDate.setText(note.getCompletionDate());
            String progressText = note.getCompletedSteps() + "/" + note.getAllSteps();
            progress.setText(progressText);
            byte[] image = note.getImage();
            if (image != null){
                noteImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                onNoteListener.onNoteClick(position);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                onNoteCheckedListener.OnNoteChecked(position, isChecked);
            }
        }
    }

    interface OnNoteListener{
        void onNoteClick(int position);
    }

    interface OnNoteCheckedListener{
        void OnNoteChecked(int position, boolean isChecked);
    }
}
