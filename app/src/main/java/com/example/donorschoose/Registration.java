package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        String email = emailField.getText().toString().trim();       // user email
        String password = passwordField.getText().toString().trim(); // user password

        if (email.isEmpty())
        {
            emailField.setError("Email cannot be empty!");
            emailField.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())  // input does not match pattern for email address
        {
            emailField.setError("Please enter a valid email!");
            emailField.requestFocus();
        }
        else if (password.isEmpty())
        {
            passwordField.setError("Password cannot be empty!");
            passwordField.requestFocus();
        }
        else if (password.length() < 6)             // requirement from firebase authentication service
        {
            passwordField.setError("Password must be at least 6 characters!");
            passwordField.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()  // add details to firebase
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())                            // registration successful
                    {
                        String userType = "donor";                      // create a new document in the users collection
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("type", userType);

                        String userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                goHome();
                            } // go to home page
                        });
                    } else
                        Toast.makeText(Registration.this, "Failed to register.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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