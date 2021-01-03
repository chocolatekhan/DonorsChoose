package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CharityProfile extends AppCompatActivity {

    private FirebaseFirestore db;
    String charityName;
    String charityDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_profile);

        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        retrieveCharityData(extras.getString("Charity Name"));
    }

    public void retrieveCharityData(String name)
    {
        charityName = name;
        CollectionReference charities = db.collection("charities");
        Query q = charities.whereEqualTo("name", charityName);

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                    if (!task.getResult().isEmpty())
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            displayProfile(document);
                        }
            }
        });

    }

    public void displayProfile(QueryDocumentSnapshot document)
    {
        charityDescription = document.getString("description");

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        nameTextView.setText(charityName + "");
        descriptionTextView.setText(charityDescription + "");

        String downloadUrl = document.getString("background");
        if (!downloadUrl.isEmpty())
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext()).load(uri.toString()).into(imageView);}
            });
        }
    }

    public void donateOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Donate.class);
        startActivity(intent);
    }


}