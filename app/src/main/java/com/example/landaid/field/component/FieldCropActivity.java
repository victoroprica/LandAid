package com.example.landaid.field.component;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.landaid.MapsActivity;
import com.example.landaid.R;
import com.example.landaid.adapter.CropAdapter;
import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.model.Crop;

import java.util.ArrayList;

public class FieldCropActivity extends AppCompatActivity {

    private ListView lv_crops;
    private DatabaseHelper databaseHelper;
    private long fieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_crop);

        lv_crops = findViewById(R.id.lv_crops);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        fieldId = getIntent().getExtras().getLong("toComponents");

        refreshList();

        findViewById(R.id.btn_to_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FieldCropActivity.this, FieldCropFormActivity.class);
                intent.putExtra("fieldID", fieldId);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_showField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FieldCropActivity.this, MapsActivity.class);
                intent.putExtra("toDraw", fieldId);
                startActivity(intent);
                setResult(0);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    private void refreshList(){
        Cursor cursor = databaseHelper.GetCrop_FieldId(fieldId);

        ArrayList<Crop> list = new ArrayList<>();

        while (cursor.moveToNext()){
            list.add(new Crop(cursor.getLong(0), 1, cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }

        final ArrayAdapter<Crop> adapter = new CropAdapter(getApplicationContext(), R.layout.crop_template, list);
        lv_crops.setAdapter(adapter);

        lv_crops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FieldCropActivity.this, FieldCropFormActivity.class);
                intent.putExtra("fieldID", fieldId);
                long cropID = adapter.getItem(position).getID();
                intent.putExtra("cropID", cropID);
                startActivity(intent);
            }
        });

        lv_crops.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long cropID = adapter.getItem(position).getID();

                new AlertDialog.Builder(FieldCropActivity.this)
                        .setTitle("Delete crop?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHelper.DeleteCrop(cropID);
                                refreshList();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

                return true;
            }
        });
    }
}
