package com.example.donorschoose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NonMonetaryDonation extends AppCompatActivity {

    String charityName;
    String charityID;

    public void loadUser(View view) { startActivity(new Intent(this, UserProfile.class)); }
    public void goHome(View view) { startActivity(new Intent(this, Home.class)); }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonmonetary_donation);
        Bundle extras = getIntent().getExtras();
        charityID = extras.getString("Charity ID");
        charityName = extras.getString("Charity Name");
    }

    public void sendData(View view)
    {
        getDonationData();
    }

    private void createDoc(String userID)
    {
        Map<String, Object> donation = new HashMap<>();
        donation.put("charity", new ArrayList<String>());
        donation.put("date", new ArrayList<String>());
        donation.put("details", new ArrayList<String>());

        FirebaseFirestore.getInstance().collection("donations").document(userID).set(donation);
    }

    private void getDonationData()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("donations").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (!documentSnapshot.exists())
                {
                    createDoc(userID);
                    addDonationData(userID, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
                }
                else
                    addDonationData(userID, (List<String>) documentSnapshot.get("charity"), (List<String>) documentSnapshot.get("date"), (List<String>) documentSnapshot.get("details"));
            }
        });
    }

    private void addDonationData(String userID, List<String> charities, List<String> dates, List<String> details)
    {
        charities.add(charityName);
        dates.add((new SimpleDateFormat("MMMM dd, yyyy")).format(Calendar.getInstance().getTime()));
        details.add(((TextView) findViewById(R.id.donationData)).getText().toString());

        FirebaseFirestore.getInstance().collection("donations").document(userID).update("charity", charities);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("date", dates);
        FirebaseFirestore.getInstance().collection("donations").document(userID).update("details", details);

        goCharity();
    }

    public void goCharity()
    {
        Intent charityProfile = new Intent(this, CharityProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        charityProfile.putExtra("Charity ID", charityID);
        charityProfile.putExtra("Access Level", "View");
        startActivity(charityProfile);
    }
}