package com.example.landaid.data.layer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.landaid.model.Crop;
import com.example.landaid.model.FieldMarker;
import com.example.landaid.model.Procedure;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "LandAid.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Field (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT)");
        db.execSQL("CREATE TABLE Marker (ID INTEGER PRIMARY KEY AUTOINCREMENT, FieldID INTEGER, PolygonIndex INTEGER, Lat REAL, Lng REAL)");
        db.execSQL("CREATE TABLE Crop (ID INTEGER PRIMARY KEY AUTOINCREMENT, FieldID INTEGER, Name TEXT, Sow TEXT, HARVEST TEXT)");
        db.execSQL("CREATE TABLE Procedure (ID INTEGER PRIMARY KEY AUTOINCREMENT, FieldID INTEGER, Name TEXT, Started TEXT, Ended TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Field");
        db.execSQL("DROP TABLE IF EXISTS Marker");
        db.execSQL("DROP TABLE IF EXISTS Crop");
        db.execSQL("DROP TABLE IF EXISTS Procedure");
        onCreate(db);
    }

    public long InsertField(String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        return db.insert("Field", null, cv);
    }

    public Cursor GetField(){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT * FROM Field", null);
    }

    public void DeleteField (long fieldID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Field", "ID = " + fieldID, null);
    }

    public void InsertMarker(FieldMarker fieldMarker){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FieldID", fieldMarker.getFieldID());
        cv.put("PolygonIndex", fieldMarker.getPolygonIndex());
        cv.put("Lat", fieldMarker.getLatLng().latitude);
        cv.put("Lng", fieldMarker.getLatLng().longitude);
        db.insert("Marker", null, cv);
    }

    public Cursor GetMarker_FieldId (long fieldID){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT * FROM Marker WHERE FieldId = " + fieldID + " ORDER BY PolygonIndex ASC", null);
    }

    public void UpdateMarker (FieldMarker fieldMarker)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PolygonIndex", fieldMarker.getPolygonIndex());
        cv.put("Lat", fieldMarker.getLatLng().latitude);
        cv.put("Lng", fieldMarker.getLatLng().longitude);
        db.update("Marker", cv,"Id = " + fieldMarker.getID(), null);
    }

    public void IncrementIndex (long ID, int index)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PolygonIndex", index + 1);
        db.update("Marker", cv, "Id = " + ID, null);
    }

    public void DecrementIndex (long ID, int index)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PolygonIndex", index - 1);
        db.update("Marker", cv, "Id = " + ID, null);
    }

    public void DeleteMarker(long markerID)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Marker", "Id = " + markerID, null);
    }

    public void InsertCrop(long fieldId, String name, String sow, String harvest){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FieldID", fieldId);
        cv.put("Name", name);
        cv.put("Sow", sow);
        cv.put("Harvest", harvest);
        db.insert("Crop", null, cv);
    }

    public Cursor GetCrop_FieldId (long fieldID){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT Id, Name, Sow, Harvest FROM Crop WHERE FieldId = " + fieldID, null);
    }

    public Cursor GetCrop_Id (long cropID){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT Id, FieldID, Name, Sow, Harvest FROM Crop WHERE Id = " + cropID, null);
    }

    public void UpdateCrop (Crop crop){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", crop.getName());
        cv.put("Sow", crop.getDateSowed());
        cv.put("Harvest", crop.getDateHarvested());
        db.update("Crop", cv, "Id = " + crop.getID(), null);
    }

    public void DeleteCrop (long cropID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Crop", "Id = " + cropID, null);
    }

    public void InsertProcedure(long fieldId, String name, String started, String ended){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FieldID", fieldId);
        cv.put("Name", name);
        cv.put("Started", started);
        cv.put("Ended", ended);
        db.insert("Procedure", null, cv);
    }

    public Cursor GetProcedure_FieldId (long fieldID){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT ID, Name, Started, Ended FROM Procedure WHERE FieldId = " + fieldID, null);
    }

    public Cursor GetProcedure_Id (long procedureID){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("SELECT ID, FieldID, Name, Started, Ended FROM Procedure WHERE Id = " + procedureID, null);
    }

    public void UpdateProcedure (Procedure procedure){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", procedure.getName());
        cv.put("Started", procedure.getDateStarted());
        cv.put("Ended", procedure.getDateEnded());
        db.update("Procedure", cv, "Id = " + procedure.getID(), null);
    }

    public void DeleteProcedure (long procedureID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Procedure", "Id = " + procedureID, null);
    }
}
