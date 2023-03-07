package com.example.landaid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.landaid.R;
import com.example.landaid.model.Field;

import java.util.List;

public class FieldAdapter extends ArrayAdapter<Field> {
    public FieldAdapter(Context context, int resource, List<Field> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.field_template, null);
        }
        Field f=getItem(position);
        TextView tv_id= convertView.findViewById(R.id.tv_id);
        tv_id.setText("ID: " + f.getID());
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        tv_name.setText("Name: " + f.getName());

        return convertView;
    }
}
