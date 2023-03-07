package com.example.landaid.field.component;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.landaid.R;
import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.model.Procedure;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FieldProcedureFormActivity extends AppCompatActivity {

    private long fieldId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_procedure_form);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        final EditText et_name =  findViewById(R.id.et_name);
        final Button et_started =  findViewById(R.id.et_started);
        final Button et_ended =  findViewById(R.id.et_ended);

        final Calendar myCalendar = Calendar.getInstance();

        if(getIntent().hasExtra("procedureID"))
        {
            Cursor procedure = databaseHelper.GetProcedure_Id(getIntent().getExtras().getLong("procedureID"));

            if (procedure.moveToNext())
            {
                et_name.setText(procedure.getString(2));
                et_started.setText(procedure.getString(3));
                et_ended.setText(procedure.getString(4));
            }
        }

        final DatePickerDialog.OnDateSetListener dateStarted = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                et_started.setText(sdf.format(myCalendar.getTime()));

            }
        };

        final DatePickerDialog.OnDateSetListener dateEnded = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                et_ended.setText(sdf.format(myCalendar.getTime()));

            }
        };

        et_started.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("procedureID"))
                {
                    String[] d = et_started.getText().toString().split("/");
                    new DatePickerDialog(FieldProcedureFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateStarted, Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0])).show();
                }
                else
                {
                    new DatePickerDialog(FieldProcedureFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateStarted, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        et_ended.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("procedureID"))
                {
                    String[] d = et_ended.getText().toString().split("/");
                    new DatePickerDialog(FieldProcedureFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateEnded, Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[0])).show();
                }
                else
                {
                    new DatePickerDialog(FieldProcedureFormActivity.this, AlertDialog.THEME_HOLO_DARK, dateEnded, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        if(getIntent().hasExtra("fieldID")){
            fieldId = getIntent().getExtras().getLong("fieldID");

            findViewById(R.id.btn_add_procedure).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    String name = et_name.getText().toString();
                    String started = et_started.getText().toString();
                    String ended = et_ended.getText().toString();

                    if(getIntent().hasExtra("procedureID")){
                        long id = getIntent().getExtras().getLong("procedureID");
                        Procedure procedure = new Procedure(id, fieldId, name, started, ended);
                        databaseHelper.UpdateProcedure(procedure);
                    }
                    else{
                        databaseHelper.InsertProcedure(fieldId, name, started, ended);
                    }
                    finish();
                }
            });
        }
    }
}
