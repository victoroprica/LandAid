package com.example.landaid.model;

public class Crop {

    private long ID;

    private long FieldID;

    private String Name;

    private String DateSowed;

    private String DateHarvested;

    public Crop(long ID, long fieldID, String name, String dateSowed, String dateHarvested) {
        this.ID = ID;
        FieldID = fieldID;
        Name = name;
        DateSowed = dateSowed;
        DateHarvested = dateHarvested;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateSowed() {
        return DateSowed;
    }

    public void setDateSowed(String dateSowed) {
        DateSowed = dateSowed;
    }

    public String getDateHarvested() {
        return DateHarvested;
    }

    public void setDateHarvested(String dateHarvested) {
        DateHarvested = dateHarvested;
    }

    public long getFieldID() {
        return FieldID;
    }

    public void setFieldID(long fieldID) {
        FieldID = fieldID;
    }
}
