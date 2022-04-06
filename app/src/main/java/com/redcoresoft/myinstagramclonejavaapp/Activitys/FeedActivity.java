package com.redcoresoft.myinstagramclonejavaapp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.redcoresoft.myinstagramclonejavaapp.Adapter.PostAdapter;
import com.redcoresoft.myinstagramclonejavaapp.Model.Post;
import com.redcoresoft.myinstagramclonejavaapp.R;
import com.redcoresoft.myinstagramclonejavaapp.databinding.ActivityFeedAvtivityBinding;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    Toolbar toolbar;



    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedAvtivityBinding binding;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedAvtivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpToolbar();

        postArrayList = new ArrayList<>();

        firebaseFirestore =FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.add_post){ //Upload Activity
            Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);
        }else if (item.getItemId()==R.id.sign_out){ //Sign Out Activity

            auth.signOut();

            Intent intentToMain = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {

        //bu da farklı bir yöntem
        // DocumentReference documentReference = firebaseFirestore.collection("Posts").document("DökümanId'si belki kendimiz id veririz document(id) zorunlu değil");
        // CollectionReference documentReference = firebaseFirestore.collection("Posts"); bu şekilde de oluşturabilirim

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if (value != null) {

                    //value.getDocuments();// its sending to me a list

                    for (DocumentSnapshot documentSnapshot : value.getDocuments() ){

                        Map<String,Object> data = documentSnapshot.getData();


                        //Casting
                        String userEmail = (String) data.get("useremail");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadurlforimage");

                        Post post = new Post(userEmail,downloadUrl,comment);
                        postArrayList.add(post);

                    }

                    postAdapter.notifyDataSetChanged();

                }

            }
        });

    }

    public void setUpToolbar(){
        toolbar = findViewById(R.id.toolBarAtFeed);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }
}