package com.example.landaid.field.component;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.landaid.R;
import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.enums.CropType;
import com.example.landaid.model.Crop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FieldCropFormActivity extends AppCompatActivity {

    private long fieldId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_crop_form);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        final Spinner et_name =  findViewById(R.id.et_name);
        final EditText et_customType = findViewById(R.id.et_customType);
        final Button et_sow =  findViewById(R.id.et_sow);
        final Button et_harvest =  findViewById(R.id.et_harvest);

        final Calendar myCalendar = Calendar.getInstance();

        et_name.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CropType.values()));

        if(getIntent().hasExtra("cropID")){
            Cursor crop = databaseHelper.GetCrop_Id(getIntent().getExtras().getLong("cropID"));
            if (crop.moveToNext()){
                switch (crop.getString(2)){
                    case "Grau":
                        et_name.setSelection(0);
                        break;
                    case "Porumb":
                        et_name.setSelection(1);
                        break;
                    case "Rapita":
                        et_name.setSelection(2);
                        break;
                    case "FloareaSoarelui":
                        et_name.setSelection(3);
                        break;
                    case "Lucerna":
                        et_name.setSelection(4);
                        break;
                    case "Orz":
                        et_name.setSelection(5);
                        break;
                    default:
                        et_name.setSelection(6);
                        et_customType.setText(crop.getString(2));
                        break;
                }
                et_sow.setText(crop.getString(3));
                et_harvest.setText(crop.getString(4));
            }
        }

        et_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(et_name.getSelectedItem().toString() == "Altele")
                {
                    et_customType.setVisibility(View.VISIBLE);
                }
                else
                {
                    et_customType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DatePickerDialog.OnDateSetListener dateSow = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                et_sow.setText(sdf.format(myCalendar.getTime()));

            }
        };

        final DatePickerDialog.OnDateSetListener dateHarvest = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                et_harvest.setText(sdf.format(myCalendar.getTime()));

            }
        };

        et_sow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("cropID"))
                {
                    String[] d = et_sow.getText().toString().split("/");
                    new DatePickerDialog(FieldCropFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateSow, Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0])).show();
                }
                else
                {
                    new DatePickerDialog(FieldCropFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateSow, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });

        et_harvest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("cropID"))
                {
                    String[] d = et_harvest.getText().toString().split("/");
                    new DatePickerDialog(FieldCropFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateHarvest, Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0])).show();
                }
                else
                {
                    new DatePickerDialog(FieldCropFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateHarvest, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });


        if(getIntent().hasExtra("fieldID")){
            fieldId = getIntent().getExtras().getLong("fieldID");

            findViewById(R.id.btn_add_crop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name;
                    if(et_name.getSelectedItem().toString() == "Altele")
                    {
                        name = et_customType.getText().toString();
                    }
                    else
                    {
                        name = et_name.getSelectedItem().toString();
                    }

                    String sow = et_sow.getText().toString();
                    String harvest = et_harvest.getText().toString();

                    if(getIntent().hasExtra("cropID")){
                        long id = getIntent().getExtras().getLong("cropID");
                        Crop crop = new Crop(id, fieldId, name, sow, harvest);
                        databaseHelper.UpdateCrop(crop);
                    }
                    else{
                        databaseHelper.InsertCrop(fieldId, name, sow, harvest);
                    }
                    finish();
                }
            });
        }
    }
}
