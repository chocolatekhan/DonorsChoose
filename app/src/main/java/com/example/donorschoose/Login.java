package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Login extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public void goHome() { startActivity(new Intent(this, Home.class)); }
    public void goCharity(String charityID)
    {
        Intent charityProfile = new Intent(this, CharityProfile.class);
        charityProfile.putExtra("Charity ID", charityID);
        charityProfile.putExtra("Access Level", "Edit");
        startActivity(charityProfile);
    }
    public void goRegister(View view) { startActivity((new Intent(this, Registration.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); }

    public void login(View view)
    {
        String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    String userID = mAuth.getCurrentUser().getUid();
                    db.collection("users").document(userID).addSnapshotListener(Login.this, new EventListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error)
                        {
                            String userType = documentSnapshot.getString("type");
                            if (userType.equals("donor"))           goHome();
                            else if (userType.equals("charity"))    goCharity(documentSnapshot.getId());
                        }
                    });
                }
                else    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}