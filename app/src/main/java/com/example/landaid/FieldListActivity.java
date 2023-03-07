package com.example.landaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.landaid.adapter.FieldAdapter;
import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.field.component.ComponentActivity;
import com.example.landaid.model.Field;

import java.util.ArrayList;

public class FieldListActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView lv_fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv_fields = findViewById(R.id.lv_fields);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        refreshList();

    }

    private void refreshList(){
        ArrayList<Field> list = new ArrayList<>();

        Cursor cursor = databaseHelper.GetField();

        while (cursor.moveToNext()){
            list.add(new Field(cursor.getLong(0), cursor.getString(1)));
        }

        final ArrayAdapter<Field> adapter = new FieldAdapter(getApplicationContext(), R.layout.field_template, list);
        lv_fields.setAdapter(adapter);

        lv_fields.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FieldListActivity.this, ComponentActivity.class);

                long val = adapter.getItem(position).getID();
                intent.putExtra("toComponents", val);
                startActivityForResult(intent, 0);
            }
        });

        lv_fields.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long fieldID = adapter.getItem(position).getID();

                new AlertDialog.Builder(FieldListActivity.this)
                        .setTitle("Delete field?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                Cursor crops = databaseHelper.GetCrop_FieldId(fieldID);

                                while (crops.moveToNext())
                                {
                                    databaseHelper.DeleteCrop(crops.getLong(0));
                                }

                                Cursor markers = databaseHelper.GetMarker_FieldId(fieldID);
                                while (markers.moveToNext())
                                {
                                    databaseHelper.DeleteMarker(markers.getLong(0));
                                }

                                databaseHelper.DeleteField(fieldID);

                                refreshList();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        }).show();

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode == 0)
        {
            if(resultCode == 0)
            {
                setResult(0);
                finish();
            }
        }
    }
}
