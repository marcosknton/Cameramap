package com.example.michus.cameramap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MapView map;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;


    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private FirebaseListAdapter<Imagen> mAdapter;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        map = (MapView) view.findViewById(R.id.mapView);
        mapController = map.getController();
        initializeMap();
        setZoom();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("imagen");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                for(DataSnapshot Dataimagen:snapshot.getChildren()){
                    Imagen imagen=Dataimagen.getValue(Imagen.class);
                    GeoPoint estationpoint = new GeoPoint(imagen.getLatitude(), imagen.getLongitude());
                    Marker startMaker = new Marker(map);
                    startMaker.setPosition(estationpoint);
                    startMaker.setTitle(imagen.getAdress());
                    startMaker.setSnippet(imagen.fecha_hora(imagen.getRutaimagen()));
                    startMaker.setIcon(getResources().getDrawable(R.drawable.love));
                    map.getOverlays().add(startMaker);
                }
                lista(myRef);
                map.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button buttoncamera = (Button) view.findViewById(R.id.Bcamara);
        buttoncamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent(myRef);
                    }
                });
        return view;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(DatabaseReference myRef) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            Double latitude;
            Double longitude;
            String adress;
            Log.i("++++++++++++++++", "primer if");
            try {
                photoFile = createImageFile();


            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                String ruta = photoFile.getAbsolutePath();
                Gps gps = new Gps(this.getContext());

                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    adress = getCurrentLocation(latitude, longitude);

                    Imagen imagen = new Imagen(ruta, latitude, longitude, adress);
                    myRef.push().setValue(imagen);
                    Log.i("------------------", imagen.getRutaimagen());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                else {
                    Log.i("++++++++++++++++", "no funciona el gps");
                }

            }
            else {
                Log.i("++++++++++++++++", "ruta vacia");
            }
        }
    }
    public String getCurrentLocation(Double latitud,Double longitud) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        String mAddresses = null;
        try {
        // Get address from location
        addresses = geocoder.getFromLocation(latitud, longitud, 1);
        } catch (IOException e) {
           e.printStackTrace();
        }
        // Get address...
        if (addresses != null && addresses.size() > 0) {
        mAddresses = addresses.get(0).getAddressLine(0)     /* address line */
        +" " + addresses.get(0).getCountryCode()   /* code country example: ES, US, etc */
        + " " + addresses.get(0).getLocality();     /* state */
         }
        Log.d("Location", mAddresses);
        return mAddresses;
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }


    private void setZoom() {
        mapController.setZoom(14);
        Gps gps = new Gps(this.getContext());
        GeoPoint startPoint = new GeoPoint(gps.getLatitude(),gps.getLongitude());
        mapController.setCenter(startPoint);
    }



    void lista(final DatabaseReference myRef2){

        List<Overlay> overlays=map.getOverlays();
        for(final Overlay overlay:overlays){
            if (overlay instanceof Marker) {
                Log.i("-----------", overlay.getClass().toString());
                ((Marker) overlay).setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        final Intent intent=new Intent(getContext(),detalle.class);
                        intent.putExtra("direccion",((Marker) overlay).getTitle()+"\n"+((Marker) overlay).getSnippet());
                        final String[] ruta = {""};

                        myRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot snapshot) {

                                for(DataSnapshot Dataimagen:snapshot.getChildren()){
                                    final Imagen imagen=Dataimagen.getValue(Imagen.class);
                                    if (imagen.getRutaimagen().contains(((Marker) overlay).getSnippet())){
                                        if (imagen.getAdress().equals(((Marker) overlay).getTitle())) {
                                            ruta[0] = imagen.getRutaimagen();
                                        }
                                    }
                                }
                                intent.putExtra("ruta",ruta[0]);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        return true;
                    }
                });
            }
        }



    }





}