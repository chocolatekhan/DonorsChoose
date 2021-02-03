package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for registration page XML
 */
public class Registration extends AppCompatActivity
{

    private FirebaseAuth mAuth;     // firebase authentication service
    private FirebaseFirestore db;   // firebase database

    /**
     * loads home page
     */
    public void goHome() { startActivity(new Intent(this, Home.class)); }

    /**
     * loads login page
     * @param view is the view calling the method using it's onClick method
     */
    public void goLogin(View view) { startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); }

    /**
     * Registers the user
     * @param view is the view calling the method using it's onClick method
     */
    public void register(View view)
    {
        String email = ((EditText) findViewById(R.id.email)).getText().toString().trim();       // retrieve user authentication details from text fields
        String password = ((EditText) findViewById(R.id.password)).getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()  // add details to firebase
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())                            // registration successful
                {
                    String userType = "donor";                      // create a new document in the users collection
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("type", userType);

                    String userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userID);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid) { goHome(); } // go to home page
                    });
                }
                else    Toast.makeText(Registration.this, "Failed to register.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // load layout
        mAuth = FirebaseAuth.getInstance();             // store authentication service and database globally; needed elsewhere
        db = FirebaseFirestore.getInstance();
    }
}