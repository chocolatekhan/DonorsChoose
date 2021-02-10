package com.example.donorschoose;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for monetary donation page XML
 */
public class Donate extends AppCompatActivity {

    String charityID;           // id of the charity being donated to
    static String charityName;  // name of the charity being donated to

    /**
     * loads user profile page
     * @param view is the view calling the method using it's onClick method
     */
    public void loadUser(View view) { startActivity(new Intent(this, UserProfile.class)); }

    /**
     * loads home page
     * @param view is the view calling the method using it's onClick method
     */
    public void goHome(View view) { startActivity(new Intent(this, Home.class)); }

    /**
     * signs out user and loads login page
     * @param view is the view calling the method using it's onClick method
     */
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);       // load layout
        Bundle extras = getIntent().getExtras();        // retrieve charity ID and name passed from previous activity
        charityID = extras.getString("Charity ID");
        charityName = extras.getString("Charity Name");
    }

    /**
     * loads non-monetary donation page
     * @param view is the view calling the method using it's onClick method
     */
    public void nonmonetaryOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), NonMonetaryDonation.class);
        intent.putExtra("Charity ID", charityID);   // send charity ID and name to new activity; needed there
        intent.putExtra("Charity Name", charityName);
        startActivity(intent);
    }

    /**
     * loads donation portal
     * @param view is the view calling the method using it's onClick method
     */
    public void donate(View view)
    {
        EditText donationField = findViewById(R.id.donationAmount);
        String donationAmount = donationField.getText().toString();        // amount to donate

        if (donationAmount.isEmpty())
        {
            donationField.setError("Please enter an amount!");
            donationField.requestFocus();
        }
        else
        {
            SSLCommerz donation = new SSLCommerz();                         // creating object for portal
            donation.makePayment(Donate.this, Integer.parseInt(donationAmount));      // use portal's method to start donation process
            goCharity();                                                    // return to charity profile page
        }
    }

    /**
     * Loads charity profile after donation is made
     */
    public void goCharity()
    {
        Intent charityProfile = new Intent(this, CharityProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        charityProfile.putExtra("Charity ID", charityID);
        charityProfile.putExtra("Access Level", "View");
        startActivity(charityProfile);
    }

    /**
     * Save transaction details once donation is complete
     * @param userID is the user's ID in firebase
     * @param amount is the donation amount
     * @param charities is the complete list of charities donated to
     * @param dates is the complete list of dates of donations
     * @param details is the complete list of donation details
     */
    private static void addTransactionData(String userID, String amount, List<String> charities, List<String> dates, List<String> details)
    {
        charities.add(charityName); // add the new charity to existing list
        dates.add((new SimpleDateFormat("MMMM dd, yyyy")).format(Calendar.getInstance().getTime()));    // add new donation date to existing list
        details.add("BDT " + amount);   // add new donation detail to existing list

        // update lists in firebase
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("charity", charities);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("date", dates);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("details", details);
    }

    /**
     * Performs activities related to successful donation
     * @param amount is the amount donated
     */
    public static void successfulTransaction(String amount)
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();   // get user from firebase
        FirebaseFirestore.getInstance().collection("donations").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) // if no previous donations
                {
                    createDoc(userID);          // create new document to store donations
                    addTransactionData(userID, amount, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());  // store donation details
                }
                else
                    addTransactionData(userID, amount, (List<String>) documentSnapshot.get("charity"), (List<String>) documentSnapshot.get("date"), (List<String>) documentSnapshot.get("details"));
                    // store donation details
            }
        });
    }

    /**
     * Creates a new donation history document for users with no previous donations
     * @param userID is the ID of the current user
     */
    private static void createDoc(String userID)
    {
        Map<String, Object> donation = new HashMap<>();     // creating document with requried lists
        donation.put("charity", new ArrayList<String>());
        donation.put("date", new ArrayList<String>());
        donation.put("details", new ArrayList<String>());

        FirebaseFirestore.getInstance().collection("donations").document(userID).set(donation); // adding document to firebase
    }

    /**
     * opens navigation menu
     * @param view is the view calling the method using it's onClick method
     */
    public void openMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(0);   // bring navigation menu into view
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.VISIBLE); // bring background into view
    }

    /**
     * closes navigation menu
     * @param view is the view calling the method using it's onClick method
     */
    public void closeMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(1000);    // hide navigation menu
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.GONE);        // hide background
    }
}