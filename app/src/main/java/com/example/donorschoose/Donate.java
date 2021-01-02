package com.example.donorschoose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Donate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
    }

    public  void nonmonetaryOnClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), NonMonetaryDonation.class);
        startActivity(intent);
    }

    public void donate(View view)
    {
        int donationAmount = Integer.parseInt(((EditText) findViewById(R.id.donationAmount)).getText().toString());
        SSLCommerz donation = new SSLCommerz();
        donation.makePayment(Donate.this, donationAmount);
    }
}