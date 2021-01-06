package com.example.donorschoose;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class Donate extends AppCompatActivity {

    String charityID;
    static String charityName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Bundle extras = getIntent().getExtras();
        charityID = extras.getString("Charity ID");
        charityName = extras.getString("Charity Name");
    }

    public  void nonmonetaryOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), NonMonetaryDonation.class);
        intent.putExtra("Charity ID", charityID);
        intent.putExtra("Charity Name", charityName);
        startActivity(intent);
    }

    public void donate(View view)
    {
        int donationAmount = Integer.parseInt(((EditText) findViewById(R.id.donationAmount)).getText().toString());
        SSLCommerz donation = new SSLCommerz();
        donation.makePayment(Donate.this, donationAmount);
        goCharity();
    }

    public void goCharity()
    {
        Intent charityProfile = new Intent(this, CharityProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        charityProfile.putExtra("Charity ID", charityID);
        charityProfile.putExtra("Access Level", "View");
        startActivity(charityProfile);
    }

    private static void addTransactionData(String userID, String amount, List<String> charities, List<String> dates, List<String> details)
    {
        charities.add(charityName);
        dates.add((new SimpleDateFormat("MMMM dd, yyyy")).format(Calendar.getInstance().getTime()));
        details.add("BDT " + amount);

        FirebaseFirestore.getInstance().collection("donations").document(userID).update("charity", charities);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("date", dates);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("details", details);
    }

    public static void successfulTransaction(String amount)
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("donations").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                addTransactionData(userID, amount, (List<String>) documentSnapshot.get("charity"), (List<String>) documentSnapshot.get("date"), (List<String>) documentSnapshot.get("details"));
            }
        });
    }

    public static void failedTransaction()
    {

    }
}