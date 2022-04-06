package com.redcoresoft.myinstagramclonejavaapp.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.redcoresoft.myinstagramclonejavaapp.R;
import com.redcoresoft.myinstagramclonejavaapp.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity implements LocationListener {

    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    Toolbar toolbar;


    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityUploadBinding binding;
    Button mapActivityButton;

    Button button_location;
    TextView textView_location;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpToolbar();

        registerLauncher();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();

        textView_location=findViewById(R.id.text_location);
        button_location=findViewById(R.id.button_location);
        //Runtime permissions


        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              if (ContextCompat.checkSelfPermission(UploadActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED) {// request permission
                  ActivityCompat.requestPermissions(UploadActivity.this,new String[]{
                          Manifest.permission.ACCESS_FINE_LOCATION
                  },100);
              }

              else getLocation();


            }
        });


    }



    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,UploadActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void btnShare(View view) {


        //UUID -> UNIVERSAL UNIQ ID
        UUID uuid = UUID.randomUUID();
        String imageName = "images/" + uuid + ".jpg";


        if (imageData != null) {
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Download Url to feed
                    StorageReference newReferenceForDownloadUrl = firebaseStorage.getReference(imageName);
                    newReferenceForDownloadUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrlForImage = uri.toString();
                            String comment = binding.txtsharedComment.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("useremail", email);
                            postData.put("downloadurlforimage", downloadUrlForImage);
                            postData.put("comment", comment);
                            postData.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }


    }

    public void onClickSelectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { // eğer izin yoksa
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) { // izin isteme bildirimi göstermelimiyim diye sorgu
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { // ask for permission

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();//LENGT INDEF. ile ne kadar süre boyunca gözüleceği belli oluyor bir işem olasıya kadar beklicek
            } else {// ask for permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

        } else {// izin verilmiş demektir
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }


    }

    private void registerLauncher() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.imgSharedImage.setImageURI(imageData);


                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(UploadActivity.this, "Permission Needed ! ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "Lokasyonunuz Alınıyor", Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(UploadActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setUpToolbar(){
        toolbar = findViewById(R.id.toolBarAtFeed);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}