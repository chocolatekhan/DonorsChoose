package com.example.donorschoose;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Controller class for user profile page XML
 */
public class UserProfile extends AppCompatActivity {

    /**
     * signs out the user and loads the login page
     * @param view is the view calling the method using it's onClick method
     */
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    /**
     * loads the home page
     * @param view is the view calling the method using it's onClick method
     */
    public void goHome(View view) { startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); }

    /**
     * Adds a row to the donation history
     * @param donationDetail is the details of the donation being added
     * @param charity is the name of the charity
     * @param date is the donation date
     */
    private void addRow(String donationDetail, String charity, String date)
    {
        TextView record = new TextView(getApplicationContext());        // create text view with required details
        String text = donationDetail + "\n" + charity + "\n" + date;
        record.setText(text);
        record.setTextSize(18);
        record.setLineSpacing(10.0f, 1.0f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,55,0,55);
        record.setLayoutParams(params);

        LinearLayout userProfile = (LinearLayout) findViewById(R.id.userProfile);   // add text view to list
        userProfile.addView(record);

    }

    /**
     * Retrieves donation history one by one and sends them to be added to displayed list
     */
    private void loadDonationHistory()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();   // get current user's ID
        FirebaseFirestore.getInstance().collection("donations").document(userID).addSnapshotListener(UserProfile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {  // successfully retrieve donation history document for user

                ArrayList<String> charities = (ArrayList<String>) documentSnapshot.get("charity");  // retrieve list of charities donated to
                ArrayList<String> dates = (ArrayList<String>) documentSnapshot.get("date");         // retrieve list of dates donated on
                ArrayList<String> details = (ArrayList<String>) documentSnapshot.get("details");    // retrieve list of donation details

                if (charities == null)  return; // no previous donation; list remains empty

                int donationCount = charities.size();
                for (int i=0; i<donationCount; i++) addRow(details.get(i), charities.get(i), dates.get(i)); // for each donation, add a row

            }
        });
    }

    /**
     * loads details about the user
     */
    private void loadUserContent()
    {
        TextView userEmail = (TextView) findViewById(R.id.userEmail);   // adds email to text view
        String email = userEmail.getText().toString();
        email += FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userEmail.setText(email);
        loadDonationHistory();  // starts loading donation history
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // load layout
        loadUserContent();  // starts loading user details
    }

    public void openMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(0);
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.VISIBLE);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setBackgroundColor(Color.parseColor("#63BABABA"));
    }

    public void closeMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(1000);
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setBackgroundColor(Color.parseColor("#ffffff"));
    }
}