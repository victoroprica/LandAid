package com.example.landaid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.landaid.data.layer.DatabaseHelper;
import com.example.landaid.model.FieldMarker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener
{
    private GoogleMap map;

    private ArrayList<FieldMarker>  markerList;

    private boolean add = false;

    private DatabaseHelper databaseHelper;

    private long fieldID;

    private boolean canEdit;

    private Switch sw_editPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMarkerDragListener(this);
        map.getUiSettings().setZoomControlsEnabled(true);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        markerList = new ArrayList<>();

        LatLng start = new LatLng(44.564316, 27.5156186);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        sw_editPlot = findViewById(R.id.sw_editPlot);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                add = true;
                findViewById(R.id.btn_add).setEnabled(false);
            }
        });

        findViewById(R.id.btn_add).setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_marker_dialog_template, null);

                final EditText et_latitude = dialogView.findViewById(R.id.et_latitude);
                final EditText et_longitude = dialogView.findViewById(R.id.et_longitude);

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Input marker coordinates")
                        .setView(dialogView)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(getApplicationContext(), "Lat: " + et_latitude.getText().toString() + " LNG: " + et_longitude.getText().toString(), Toast.LENGTH_SHORT).show();

                                LatLng ll = new LatLng(Double.parseDouble(et_latitude.getText().toString()), Double.parseDouble(et_longitude.getText().toString()));

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

                                markerList.add(new FieldMarker(ll));

                                PopulatePlot(markerList);

                                if(markerList.size() >= 3)
                                {
                                    findViewById(R.id.btn_confirm).setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        })
                        .show();

                return true;
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearMap();
            }
        });

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final EditText et_name = new EditText(getApplicationContext());

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Field name")
                        .setView(et_name)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                fieldID = databaseHelper.InsertField(et_name.getText().toString());
                                int index = 0;

                                for (FieldMarker marker: markerList)
                                {
                                    marker.setFieldID(fieldID);
                                    marker.setPolygonIndex(index++);
                                    databaseHelper.InsertMarker(marker);
                                }

                                clearMap();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.btn_list).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MapsActivity.this, FieldListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                if(add)
                {
                    map.clear();

                    markerList.add(new FieldMarker(latLng));

                    for (FieldMarker fm: markerList)
                    {
                        map.addMarker(new MarkerOptions()
                                .position(fm.getLatLng())
                                .icon(BitmapDescriptorFromVector(getApplicationContext(), R.drawable.point_symbol, 2, 2)));
                    }

                    if(markerList.size() >= 3)
                    {
                        PolygonOptions po = BuildPolygonOptions();

                        for (FieldMarker fieldMarker: markerList)
                        {
                            po.add(fieldMarker.getLatLng());
                        }

                        map.addPolygon(po);

                        findViewById(R.id.btn_confirm).setVisibility(View.VISIBLE);
                    }

                    add = false;
                    findViewById(R.id.btn_add).setEnabled(true);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                final FieldMarker aux = (FieldMarker) marker.getTag();

                if(canEdit && !aux.isEdit())
                {
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Delete marker")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseHelper.DeleteMarker(aux.getID());
                                    for (FieldMarker fieldMarker: markerList)
                                    {
                                        if(fieldMarker.getPolygonIndex() > aux.getPolygonIndex()){
                                            databaseHelper.DecrementIndex(fieldMarker.getID(), fieldMarker.getPolygonIndex());
                                        }
                                    }

                                    InitializeMarkerList(fieldID);
                                    PopulatePlot(markerList);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                    return true;
                }

                return false;
            }
        });

        if (getIntent().hasExtra("toDraw"))
        {
            fieldID = getIntent().getExtras().getLong("toDraw");

            Cursor fieldMarkerCursor = databaseHelper.GetMarker_FieldId(fieldID);

            PolygonOptions po = BuildPolygonOptions();

            clearMap();

            sw_editPlot.setVisibility(View.VISIBLE);

            findViewById(R.id.sw_editPlot).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    canEdit = sw_editPlot.isChecked();

                    PopulatePlot(markerList);
                }
            });

            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

            while (fieldMarkerCursor.moveToNext())
            {
                long fieldMarkerID = fieldMarkerCursor.getLong(0);
                int polygonIndex = fieldMarkerCursor.getInt(2);
                LatLng latLng = new LatLng(fieldMarkerCursor.getDouble(3), fieldMarkerCursor.getDouble(4));

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(canEdit)
                        .icon(BitmapDescriptorFromVector(getApplicationContext(), R.drawable.point_symbol, 2, 2)));
                marker.setTag(new FieldMarker(fieldMarkerID, fieldID, polygonIndex, latLng, false));

                markerList.add(new FieldMarker(fieldMarkerID, fieldID, polygonIndex, marker.getPosition()));

                po.add(latLng);

                latLngBoundsBuilder.include(marker.getPosition());
            }

            map.addPolygon(po);

            LatLngBounds latLngBounds = latLngBoundsBuilder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, 300);
            map.animateCamera(cu);
        }
    }

    private void clearMap()
    {
        map.clear();
        findViewById(R.id.btn_add).setEnabled(true);
        markerList.clear();
        findViewById(R.id.btn_confirm).setVisibility(View.INVISIBLE);
        sw_editPlot.setChecked(false);
        canEdit = false;
        sw_editPlot.setVisibility(View.INVISIBLE);
    }

    private void InitializeMarkerList(long fieldID){

        Cursor fieldMarkerCursor = databaseHelper.GetMarker_FieldId(fieldID);
        markerList.clear();

        while (fieldMarkerCursor.moveToNext())
        {
            long fieldMarkerID = fieldMarkerCursor.getLong(0);
            int polygonIndex = fieldMarkerCursor.getInt(2);
            LatLng latLng = new LatLng(fieldMarkerCursor.getDouble(3), fieldMarkerCursor.getDouble(4));

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(canEdit)
                    .icon(BitmapDescriptorFromVector(getApplicationContext(), R.drawable.point_symbol, 2, 2)));
            marker.setTag(new FieldMarker(fieldMarkerID, fieldID, polygonIndex, latLng, false));

            markerList.add(new FieldMarker(fieldMarkerID, fieldID, polygonIndex, marker.getPosition()));
        }
    }

    private PolygonOptions BuildPolygonOptions() {
        return new PolygonOptions()
                .fillColor(Color.argb(128, 0, 186,0))
                .strokeWidth(1)
                .strokeColor(Color.GREEN);
    }

    private void PopulatePlot(ArrayList<FieldMarker> markerList)
    {
        map.clear();
        PolygonOptions po = BuildPolygonOptions();
        int index = 0;

        for (FieldMarker fieldMarker: markerList)
        {
            LatLng editLatLng;
            map.addMarker(new MarkerOptions().position(new LatLng(fieldMarker.getLatLng().latitude, fieldMarker.getLatLng().longitude))
                    .draggable(canEdit)
                    .icon(BitmapDescriptorFromVector(getApplicationContext(), R.drawable.point_symbol, 2, 2)))
                    .setTag(new FieldMarker(fieldMarker.getID(), fieldID, index, fieldMarker.getLatLng(), false));

            if(canEdit)
            {
                if(index < markerList.size() - 1)
                {
                    editLatLng = CalculateMidPoint(markerList.get(index), markerList.get(index + 1));

                }
                else
                {
                    editLatLng = CalculateMidPoint(markerList.get(index), markerList.get(0));
                }

                map.addMarker(new MarkerOptions()
                        .position(editLatLng)
                        .draggable(canEdit)
                        .icon(BitmapDescriptorFromVector(getApplicationContext(), R.drawable.plus_symbol, 1, 1)))
                        .setTag(new FieldMarker(fieldMarker.getID(), fieldMarker.getFieldID(), index, fieldMarker.getLatLng(), true));
                index++;
            }

            po.add(fieldMarker.getLatLng());
        }

        map.addPolygon(po);
    }

    private BitmapDescriptor BitmapDescriptorFromVector(Context context, int vectorResId, int widthMultiplier, int heightMultiplier) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() * widthMultiplier, vectorDrawable.getIntrinsicHeight() * heightMultiplier);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * widthMultiplier, vectorDrawable.getIntrinsicHeight() * heightMultiplier, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private LatLng CalculateMidPoint(FieldMarker marker1, FieldMarker marker2)
    {
        double lat = (marker1.getLatLng().latitude + marker2.getLatLng().latitude)/2;
        double lng = (marker1.getLatLng().longitude + marker2.getLatLng().longitude)/2;
        return new LatLng(lat, lng);
    }

    @Override
    public void onMarkerDragStart(Marker marker)
    {
    }

    @Override
    public void onMarkerDrag(Marker marker)
    {
    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        FieldMarker aux = (FieldMarker) marker.getTag();

        if(aux.isEdit())
        {
            for (FieldMarker fieldMarker: markerList)
            {
                if(fieldMarker.getPolygonIndex() > aux.getPolygonIndex()){
                    databaseHelper.IncrementIndex(fieldMarker.getID(), fieldMarker.getPolygonIndex());
                }
            }
            aux.setLatLng(marker.getPosition());
            aux.setPolygonIndex(aux.getPolygonIndex() + 1);
            databaseHelper.InsertMarker(aux);
            InitializeMarkerList(fieldID);
        }
        else
        {
            for (FieldMarker fieldMarker: markerList)
            {
                aux = (FieldMarker) marker.getTag();
                if (aux.getID() == fieldMarker.getID())
                {
                    fieldMarker.setLatLng(marker.getPosition());
                    databaseHelper.UpdateMarker(fieldMarker);
                }
            }
        }

        PopulatePlot(markerList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0)
        {
            if(resultCode == 0)
            {
            }
        }
    }
}
