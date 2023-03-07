package com.example.landaid.model;

import com.google.android.gms.maps.model.LatLng;

public class FieldMarker {

    private long ID;

    private long FieldID;

    private int PolygonIndex;

    private LatLng LatLng;

    private boolean IsEdit;

    public FieldMarker(LatLng latLng) {
        LatLng = latLng;
    }

    public FieldMarker(long ID, long fieldID, int polygonIndex, LatLng latLng) {
        this.ID = ID;
        FieldID = fieldID;
        PolygonIndex = polygonIndex;
        LatLng = latLng;
    }

    public FieldMarker(long ID, long fieldID, int polygonIndex, LatLng latLng, boolean isEdit) {
        this.ID = ID;
        FieldID = fieldID;
        PolygonIndex = polygonIndex;
        LatLng = latLng;
        IsEdit = isEdit;
    }

    public FieldMarker(FieldMarker fieldMarker, boolean isEdit) {
        this.ID = fieldMarker.getID();
        FieldID = fieldMarker.getFieldID();
        PolygonIndex = fieldMarker.getPolygonIndex();
        LatLng = fieldMarker.getLatLng();
        IsEdit = isEdit;
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

    public int getPolygonIndex() {
        return PolygonIndex;
    }

    public void setPolygonIndex(int polygonIndex) {
        PolygonIndex = polygonIndex;
    }

    public LatLng getLatLng() {
        return LatLng;
    }

    public void setLatLng(LatLng latLng) {
        LatLng = latLng;
    }

    public boolean isEdit() {
        return IsEdit;
    }

    public void setEdit(boolean edit) {
        IsEdit = edit;
    }
}
