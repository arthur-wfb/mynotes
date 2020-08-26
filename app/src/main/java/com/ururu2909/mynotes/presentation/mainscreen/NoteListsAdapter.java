package com.ururu2909.mynotes.presentation.mainscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ururu2909.mynotes.R;
import com.ururu2909.mynotes.model.entities.NoteList;


public class NoteListsAdapter extends ListAdapter<NoteList, NoteListsAdapter.NoteListItemViewHolder> {

    private OnNoteListListener mOnNoteListListener;

    NoteListsAdapter(OnNoteListListener mOnNoteListListener) {
        super(DIFF_CALLBACK);
        this.mOnNoteListListener = mOnNoteListListener;
    }

    @NonNull
    @Override
    public NoteListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_note_list, parent, false);
        return new NoteListItemViewHolder(view, mOnNoteListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListItemViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    private static final DiffUtil.ItemCallback<NoteList> DIFF_CALLBACK = new DiffUtil.ItemCallback<NoteList>() {
        @Override
        public boolean areItemsTheSame(@NonNull NoteList oldItem, @NonNull NoteList newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NoteList oldItem, @NonNull NoteList newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    static class NoteListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        OnNoteListListener onNoteListListener;

        NoteListItemViewHolder(View itemView, OnNoteListListener onNoteListListener) {
            super(itemView);
            name = itemView.findViewById(R.id.noteListName);
            this.onNoteListListener = onNoteListListener;
            itemView.setOnClickListener(this);
        }

        void bindTo(NoteList noteList) {
            name.setText(noteList.getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                onNoteListListener.onNoteListClick(position);
            }
        }
    }

    interface OnNoteListListener{
        void onNoteListClick(int position);
    }
}
