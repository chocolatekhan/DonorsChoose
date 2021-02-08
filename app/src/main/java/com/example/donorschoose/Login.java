package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

/**
 * Controller class for login page XML
 */
public class Login extends AppCompatActivity
{
    private FirebaseAuth mAuth;     // firebase authentication service
    private FirebaseFirestore db;   // firebase database

    /**
     * loads home page
     */
    public void goHome() { startActivity(new Intent(this, Home.class)); }

    /**
     * loads charity profile page for charity accounts
     * @param charityID is the respective charity's ID
     */
    public void goCharity(String charityID)
    {
        Intent charityProfile = new Intent(this, CharityProfile.class);
        charityProfile.putExtra("Charity ID", charityID);       // sending charity ID and user access level; needed in new activity
        charityProfile.putExtra("Access Level", "Edit");
        startActivity(charityProfile);
    }

    /**
     * loads registration page
     * @param view is the view calling the method using it's onClick method
     */
    public void goRegister(View view) { startActivity((new Intent(this, Registration.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); }

    /**
     * logs the user into their account and brings them to their home page
     * @param view is the view calling the method using it's onClick method
     */
    public void login(View view)
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
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())                            // successfully logged in
                    {
                        String userID = mAuth.getCurrentUser().getUid();    // get user ID
                        db.collection("users").document(userID).addSnapshotListener(Login.this, new EventListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error)
                            {
                                String userType = documentSnapshot.getString("type");   // use ID to get user type
                                if (userType.equals("donor"))           goHome();               // donors go to home page
                                else if (userType.equals("charity"))    goCharity(documentSnapshot.getId());    // charities go to their own profiles
                            }
                        });
                    }
                    else    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    // invalid login details
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);    // load layout
        mAuth = FirebaseAuth.getInstance();         // setting authentication service and database globally; needed elsewhere
        db = FirebaseFirestore.getInstance();
    }
}