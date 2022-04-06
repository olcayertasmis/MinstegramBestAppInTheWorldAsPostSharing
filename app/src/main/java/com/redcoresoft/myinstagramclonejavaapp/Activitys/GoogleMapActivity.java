package com.redcoresoft.myinstagramclonejavaapp.Activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.redcoresoft.myinstagramclonejavaapp.R;
import com.redcoresoft.myinstagramclonejavaapp.databinding.ActivityGoogleMapBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    boolean isPermissionGranter;
    GoogleMap googleMap;
    ImageView imageViewSearch;
    EditText inputLocation;

    private ActivityGoogleMapBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean trackBoolean;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageViewSearch=findViewById(R.id.imageViewSearch);
        inputLocation=findViewById(R.id.inputLocation);

        trackBoolean = false;

        checkPermission();

        if (isPermissionGranter) {
            if (checkGooglePlayServices()) {

                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.contaner, supportMapFragment).commit();
                supportMapFragment.getMapAsync(this);
            } else {
                Toast.makeText(GoogleMapActivity.this, "Google Play Services NOT Available", Toast.LENGTH_SHORT).show();
            }
        }

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location=inputLocation.getText().toString();
                if(location==null){
                    Toast.makeText(GoogleMapActivity.this, "Type location name", Toast.LENGTH_SHORT).show();
                }else
                    {

                        Geocoder geocoder=new Geocoder(GoogleMapActivity.this, Locale.getDefault());
                        try {
                            List<Address>listAddress=geocoder.getFromLocationName(location,1);
                            if (listAddress.size()>0) {
                                LatLng latLng=new LatLng(listAddress.get(0).getLatitude(),listAddress.get(0).getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.title("My Search Location");
                                markerOptions.position(latLng);
                                googleMap.addMarker(markerOptions);
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                                googleMap.animateCamera(cameraUpdate);


                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(GoogleMapActivity.this);
        if (result == ConnectionResult.SUCCESS) {

            return true;

        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(GoogleMapActivity.this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(GoogleMapActivity.this, "User Cancelled Dialoge", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();

        }
        return false;
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                isPermissionGranter = true;
                Toast.makeText(GoogleMapActivity.this, "Permission Granter", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void CheckGps(){

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
      // LatLng latLng = new LatLng(0, 0);
      // MarkerOptions markerOptions = new MarkerOptions();
      // markerOptions.title("My Position");
      // markerOptions.position(latLng);
      // googleMap.addMarker(markerOptions);
      // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
      // googleMap.animateCamera(cameraUpdate);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean",false);

                if(!trackBoolean) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                }
            }
        };




        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
       googleMap.getUiSettings().setRotateGesturesEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
            }
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                CheckGps();
                return true;
            }
        });
    }


    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }
}