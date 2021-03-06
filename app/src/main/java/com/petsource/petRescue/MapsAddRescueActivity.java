package com.petsource.petRescue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.petsource.R;
import com.petsource.model.Pet;
import com.petsource.model.Rescue;
import com.petsource.network.API;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsAddRescueActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Pet ChosePet;
    public static String desc;
    public static double lat;
    public static double lng;

    GoogleMapOptions options = new GoogleMapOptions();

    private Button btnNext;
    private TextView lblLocation;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_addrescue);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FRADMCN.TTF");

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setTypeface(typeface);

        lblLocation = (TextView) findViewById(R.id.lblLocation);
        lblLocation.setTypeface(typeface);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .mapToolbarEnabled(true)
                ;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
    }
    public void testToast(View view) {
        try {
            Location findme = mMap.getMyLocation();
            double latitude = findme.getLatitude();
            double longitude = findme.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
//            Toast.makeText(MapsActivity.this, "Lat : " + latitude + " | Long : " + longitude, Toast.LENGTH_SHORT).show();
            Call<Rescue> addRescue = API.Factory.getInstance().addRescue(
                    ChosePet.getId(),
                    ChosePet.getUserid(),
                    findme.getLatitude(),
                    findme.getLongitude(),
                    desc
            );
            addRescue.enqueue(new Callback<Rescue>() {
                @Override
                public void onResponse(Call<Rescue> call, Response<Rescue> response) {
                    Toast.makeText(MapsAddRescueActivity.this, "Thank you. You will be notified when someone is willing to adopt.", Toast.LENGTH_SHORT).show();
                    PetRescueActivity.petRescueActivity.finish();
                    AddRescueActivity.addRescueActivity.finish();
                    RescueDescActivity.rescueDescActivity.finish();
                    finish();
                }

                @Override
                public void onFailure(Call<Rescue> call, Throwable t) {
                    Toast.makeText(MapsAddRescueActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch(Exception e){
            Toast.makeText(MapsAddRescueActivity.this, "Please get your location first.", Toast.LENGTH_SHORT).show();
            Toast.makeText(MapsAddRescueActivity.this, "Thank you. You will be notified when someone is willing to adopt.", Toast.LENGTH_SHORT).show();
            PetRescueActivity.petRescueActivity.finish();
            AddRescueActivity.addRescueActivity.finish();
            RescueDescActivity.rescueDescActivity.finish();
            finish();
        }

    }

}
