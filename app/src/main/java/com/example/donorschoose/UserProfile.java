package com.example.donorschoose;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void goHome(View view) { startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); }

    private void addRow(String donationDetail, String charity, String date)
    {
        TextView record= new TextView(getApplicationContext());
        String text = donationDetail + "\n" + charity + "\n" + date;
        record.setText(text);
        record.setTextSize(18);
        record.setLineSpacing(10.0f, 1.0f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,55,0,55);
        record.setLayoutParams(params);

        LinearLayout userProfile = (LinearLayout) findViewById(R.id.userProfile);
        userProfile.addView(record);

    }

    private void loadDonationHistory()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("donations").document(userID).addSnapshotListener(UserProfile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                ArrayList<String> charities = (ArrayList<String>) documentSnapshot.get("charity");
                ArrayList<String> dates = (ArrayList<String>) documentSnapshot.get("date");
                ArrayList<String> details = (ArrayList<String>) documentSnapshot.get("details");

                if (charities == null)  return;

                int donationCount = charities.size();
                for (int i=0; i<donationCount; i++) addRow(details.get(i), charities.get(i), dates.get(i));

            }
        });
    }

    private void loadUserContent()
    {
        TextView userEmail = (TextView) findViewById(R.id.userEmail);
        String email = userEmail.getText().toString();
        email += FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userEmail.setText(email);
        loadDonationHistory();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        loadUserContent();
    }
}