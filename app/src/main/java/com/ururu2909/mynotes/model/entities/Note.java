package com.ururu2909.mynotes.model.entities;

public class Note {
    private int id;
    private String title;
    private String explanationText;
    private int isComplete;
    private byte[] image;
    private String completionDate;
    private String notificationDate;
    private int noteListId;
    private int completedSteps;
    private int allSteps;

    public Note(int id, String title, String explanationText, int isComplete, byte[] image,
                String completionDate, String notificationDate, int completedSteps, int allSteps, int noteListId) {
        this.id = id;
        this.title = title;
        this.explanationText = explanationText;
        this.isComplete = isComplete;
        this.image = image;
        this.completionDate = completionDate;
        this.notificationDate = notificationDate;
        this.completedSteps = completedSteps;
        this.allSteps = allSteps;
        this.noteListId = noteListId;
    }

    public Note(String title, String explanationText, int isComplete, byte[] image, String completionDate,
                String notificationDate, int completedSteps, int allSteps, int noteListId) {
        this.title = title;
        this.explanationText = explanationText;
        this.isComplete = isComplete;
        this.image = image;
        this.completionDate = completionDate;
        this.notificationDate = notificationDate;
        this.completedSteps = completedSteps;
        this.allSteps = allSteps;
        this.noteListId = noteListId;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCompletedSteps(int completedSteps) {
        this.completedSteps = completedSteps;
    }

    public void setAllSteps(int allSteps) {
        this.allSteps = allSteps;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanationText() {
        return explanationText;
    }

    public int isComplete(){
        return isComplete;
    }

    public byte[] getImage() {
        return image;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public int getCompletedSteps() {
        return completedSteps;
    }

    public int getAllSteps() {
        return allSteps;
    }

    public int getNoteListId() {
        return noteListId;
    }
}
