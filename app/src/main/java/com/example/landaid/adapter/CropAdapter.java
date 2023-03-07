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
import com.example.landaid.model.Crop;

import java.util.List;

public class CropAdapter extends ArrayAdapter<Crop> {

    public CropAdapter(Context context, int resource, List<Crop> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.crop_template, null);
        }
        Crop c=getItem(position);

        TextView tv_id= convertView.findViewById(R.id.tv_id);
        tv_id.setText(String.valueOf(c.getID()));

        TextView tv_crop_name = convertView.findViewById(R.id.tv_crop_name);
        tv_crop_name.setText(String.format("Name: %s", c.getName()));

        TextView tv_sow = convertView.findViewById(R.id.tv_sow);
        tv_sow.setText(String.format("Sowed at: %s", c.getDateSowed()));

        TextView tv_harvest = convertView.findViewById(R.id.tv_harvest);
        tv_harvest.setText(String.format("Harvested at: %s", c.getDateHarvested()));

        return convertView;
    }
}
