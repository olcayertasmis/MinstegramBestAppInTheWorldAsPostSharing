package com.redcoresoft.myinstagramclonejavaapp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.redcoresoft.myinstagramclonejavaapp.R;

public class AboutUsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton navigationDrawerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        auth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawerLayoutOnAboutUsActivity);
        navigationView = findViewById(R.id.navigationViewOnAboutUs);
        navigationDrawerButton = findViewById(R.id.navigationButtonOnAboutUs);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationButtonClick();
    }

    private void navigationButtonClick() {
        navigationDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                    drawerLayout.closeDrawer(GravityCompat.END);
                }else{
                    drawerLayout.openDrawer(GravityCompat.END);
                }

            }
        });
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId()==R.id.go_home){ //Feed Activity
            Intent intentToUpload = new Intent(AboutUsActivity.this,FeedActivity.class);
            startActivity(intentToUpload);
        }
        else if (menuItem.getItemId()==R.id.add_post){ //Upload Activity
            Intent intentToUpload = new Intent(AboutUsActivity.this,UploadActivity.class);
            startActivity(intentToUpload);}
        else if (menuItem.getItemId()==R.id.sign_out){ //Sign Out Activity
            auth.signOut();
            Intent intentToMain = new Intent(AboutUsActivity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }


        return false;
    }
}