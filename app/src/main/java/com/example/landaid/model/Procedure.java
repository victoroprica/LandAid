package com.example.landaid.model;

public class Procedure {

    private long ID;

    private long FieldID;

    private String Name;

    private String DateStarted;

    private String DateEnded;

    public Procedure(long ID, long fieldID, String name, String dateStarted, String dateEnded) {
        this.ID = ID;
        FieldID = fieldID;
        Name = name;
        DateStarted = dateStarted;
        DateEnded = dateEnded;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getFieldID() {
        return FieldID;
    }

    public void setFieldID(long fieldID) {
        FieldID = fieldID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateStarted() {
        return DateStarted;
    }

    public void setDateStarted(String dateStarted) {
        DateStarted = dateStarted;
    }

    public String getDateEnded() {
        return DateEnded;
    }

    public void setDateEnded(String dateEnded) {
        DateEnded = dateEnded;
    }
}
