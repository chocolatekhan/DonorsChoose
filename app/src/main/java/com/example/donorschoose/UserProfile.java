package com.example.donorschoose;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserProfile extends AppCompatActivity {

    private void addRow(String donationDetail, String charity, String date)
    {
        TextView record= new TextView(getApplicationContext());
        String text = donationDetail + "\n" + charity + "\n" + date;
        record.setText(text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,10,0,10);
        record.setLayoutParams(params);

        LinearLayout userProfile = (LinearLayout) findViewById(R.id.userProfile);
        userProfile.addView(record);

    }

    private void loadDonationHistory()
    {
        for (int i=1; i<=20; i++)   addRow("Some Money", "Some Charity", "05 Janaury, 2020");
    }

    private void loadUserContent()
    {
        TextView userEmail = (TextView) findViewById(R.id.userEmail);
        String email = userEmail.getText().toString();
        email += "someEmail@gmail.com";
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