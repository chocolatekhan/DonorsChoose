package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Home extends AppCompatActivity
{
    private FirebaseFirestore db;
    SearchView searchBar;
    LinearLayout relativeLayout;


    private void addCard(QueryDocumentSnapshot document)
    {
        CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams cardViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayout.setMargins(10, 10, 10, 10);
        cardView.setLayoutParams(cardViewLayout);
        cardView.setRadius(15);
        cardView.setContentPadding(0, 300, 0, 0);
        cardView.setCardBackgroundColor(Color.rgb(102, 217, 238));
        cardView.setMaxCardElevation(30);
        cardView.setMaxCardElevation(6);

        TextView name = new TextView(getApplicationContext());
        name.setBackgroundColor(Color.BLACK);
        name.getBackground().setAlpha(100);
        name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        name.setText(document.getString("name"));
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        name.setTextColor(Color.WHITE);
        name.setPadding(20,10,10,10);
        name.setGravity(Gravity.LEFT);


        TextView description = new TextView(getApplicationContext());
        description.setBackgroundColor(Color.BLACK);
        description.getBackground().setAlpha(100);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        description.setText(document.getString("description"));
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        description.setTextColor(Color.WHITE);
        description.setPadding(20,10,10,10);
        description.setGravity(Gravity.LEFT);

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(name);
        linearLayout.addView(description);
        cardView.addView(linearLayout);
        relativeLayout.addView(cardView);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onQueryTextSubmit(String input) {

            relativeLayout.removeAllViews();

            final int[] resultFlag = {2};
            CollectionReference charities = db.collection("charities");
            Query queryNames = charities.orderBy("name").startAt(input).endAt(input + "\uf8ff");
            queryNames.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                        if (!task.getResult().isEmpty())
                            for (QueryDocumentSnapshot document : task.getResult())
                                addCard(document);
                        else    resultFlag[0]--;
                    else    Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            Query queryTags = charities.whereArrayContains("tags", input);
            queryTags.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                        if (!task.getResult().isEmpty())
                            for (QueryDocumentSnapshot document : task.getResult())
                                addCard(document);
                        else    resultFlag[0]--;
                    else    Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            if (resultFlag[0] == 0) Toast.makeText(Home.this, "No results", Toast.LENGTH_SHORT).show();

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) { return false; }
    };

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        searchBar = (SearchView) findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(queryTextListener);

        relativeLayout = (LinearLayout) findViewById(R.id.relativeLayout);
    }



}