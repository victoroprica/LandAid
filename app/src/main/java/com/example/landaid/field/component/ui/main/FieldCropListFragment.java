package com.example.landaid.field.component.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.landaid.MapsActivity;
import com.example.landaid.R;
import com.example.landaid.adapter.CropAdapter;
import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.field.component.FieldCropFormActivity;
import com.example.landaid.model.Crop;

import java.util.ArrayList;

public class FieldCropListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView lv_crops;
    private DatabaseHelper databaseHelper;
    private long fieldId;

    public static FieldCropListFragment newInstance(int index) {
        FieldCropListFragment fragment = new FieldCropListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crop_list, container, false);

        lv_crops = root.findViewById(R.id.lv_crops);

        databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        fieldId = getActivity().getIntent().getExtras().getLong("toComponents");

        refreshList();

        root.findViewById(R.id.btn_to_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FieldCropFormActivity.class);
                intent.putExtra("fieldID", fieldId);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_showField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("toDraw", fieldId);
                startActivity(intent);
                getActivity().setResult(0);
                getActivity().finish();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    private void refreshList() {
        Cursor cursor = databaseHelper.GetCrop_FieldId(fieldId);

        ArrayList<Crop> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            list.add(new Crop(cursor.getLong(0), 1, cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }

        final ArrayAdapter<Crop> adapter = new CropAdapter(getActivity().getApplicationContext(), R.layout.crop_template, list);
        lv_crops.setAdapter(adapter);

        lv_crops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FieldCropFormActivity.class);
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

                new AlertDialog.Builder(getActivity())
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