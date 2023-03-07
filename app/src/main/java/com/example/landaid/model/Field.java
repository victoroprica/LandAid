package com.example.landaid.model;

public class Field {

    private long ID;

    private String Name;

    public Field(long ID, String name) {
        this.ID = ID;
        Name = name;
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
}
