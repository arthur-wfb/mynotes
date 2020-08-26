package com.ururu2909.mynotes.model.entities;

public class Step {
    private int id;
    private String title;
    private int isComplete;
    private int noteId;

    public Step(int id, String title, int isComplete, int noteId) {
        this.id = id;
        this.title = title;
        this.isComplete = isComplete;
        this.noteId = noteId;
    }

    public Step(String title, int isComplete, int noteId) {
        this.title = title;
        this.isComplete = isComplete;
        this.noteId = noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int isComplete() {
        return isComplete;
    }

    public int getNoteId() {
        return noteId;
    }
}
