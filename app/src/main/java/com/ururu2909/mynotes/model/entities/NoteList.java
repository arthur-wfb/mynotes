package com.ururu2909.mynotes.model.entities;

public class NoteList {
    private int id;
    private String name;

    public NoteList(String name){
        this.name = name;
    }

    public NoteList(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
