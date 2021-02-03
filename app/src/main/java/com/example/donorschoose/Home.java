package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller class for home page XML
 */
public class Home extends AppCompatActivity
{
    private FirebaseFirestore db;   // firebase database
    SearchView searchBar;
    LinearLayout relativeLayout;    // layout containing search results + suggested queries

    /**
     * loads user profile page
     * @param view is the view calling the method using it's onClick method
     */
    public void loadUser(View view) { startActivity(new Intent(this, UserProfile.class)); }

    /**
     * loads the charity profile page for a specific charity
     * @param charityID
     */
    private void loadCharity(String charityID)
    {
        Intent charityProfile = new Intent(this, CharityProfile.class);
        charityProfile.putExtra("Charity ID", charityID);       // sends ID of the charity and access level of user to new activity
        charityProfile.putExtra("Access Level", "View");
        startActivity(charityProfile);
    }

    /**
     * Creates outer structure for new card to add to results
     * @return the created card
     */
    private CardView createCard()
    {
        CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams cardViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayout.setMargins(10, 50, 10, 50);
        cardView.setLayoutParams(cardViewLayout);
        cardView.setRadius(15);
        cardView.setMaxCardElevation(30);
        cardView.setMaxCardElevation(6);
        cardView.setCardBackgroundColor(Color.rgb(102, 217, 238));  // background in case image fails to load

        return cardView;
    }

    /**
     * Creates text field to hold charity name
     * @param nameString is the charity name
     * @return the created text field
     */
    private TextView createName(String nameString)
    {
        TextView name = new TextView(getApplicationContext());
        name.setBackgroundColor(Color.BLACK);   // slightly black background to make text visible on top of background image
        name.getBackground().setAlpha(150);
        name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        name.setText(nameString);
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        name.setTextColor(Color.WHITE);
        name.setPadding(20,10,10,10);
        name.setGravity(Gravity.LEFT);
        return name;
    }

    /**
     * Creates text field to hold charity description
     * @param descriptionString is the charity description
     * @return the created text field
     */
    private TextView createDescription(String descriptionString)
    {
        TextView description = new TextView(getApplicationContext());
        description.setBackgroundColor(Color.BLACK);    // slightly black background to make text visible on top of background image
        description.getBackground().setAlpha(150);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        description.setText(descriptionString);
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        description.setTextColor(Color.WHITE);
        description.setPadding(20,10,10,10);
        description.setGravity(Gravity.LEFT);
        return description;
    }

    /**
     * Adds background image to inner layout holding content of card
     * @param linearLayout  is the inner layout
     * @param downloadUrl   is the URL for the background image
     * @return the edited inner layout
     */
    private LinearLayout addBackground(LinearLayout linearLayout, String downloadUrl)
    {
        if (!downloadUrl.isEmpty()) // if URL is empty, background image cannot be loaded
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl); // retrieve reference to image from firebase
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext()).load(uri.toString())
                            .resize(linearLayout.getWidth(), linearLayout.getHeight()).into(new Target() {  // set image to appropriate size
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap));         // set image as inner layout background
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {}

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {}
                    });
            }});
        }
        return linearLayout;
    }

    /**
     * Start making a card for each result and add all inner details
     * @param document holds the details of the result
     */
    private void addCard(QueryDocumentSnapshot document)
    {
        CardView cardView = createCard();   // create outer structure for new card
        TextView name = createName(document.getString("name"));     // get charity details from document and create text fields
        TextView description = createDescription(document.getString("description"));


        cardView.setOnClickListener(new View.OnClickListener() {        // clicking on card loads respective charity profile page
            public void onClick(View v) {
                loadCharity(document.getId());
            }
        });

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());  // create new inner layout
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(name);                                             // add charity details to inner layout
        linearLayout.addView(description);
        linearLayout.setPadding(0, 300, 0, 0);
        linearLayout = addBackground(linearLayout, document.getString("background"));   // add background image to inner layout (preserves card corner radius this way)

        cardView.addView(linearLayout);     // add inner layout to card
        relativeLayout.addView(cardView);   // add card to outer layout
    }

    /**
     * Listens for new queries and makes changes accordingly
     */
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onQueryTextSubmit(String input) {    // query is submitted

            FlexboxLayout flexboxLayout = findViewById(R.id.categoryHints);         // hide suggested queries
            if (flexboxLayout.getVisibility() == View.VISIBLE)  flexboxLayout.setVisibility(View.GONE);
            relativeLayout.removeViews(1, relativeLayout.getChildCount() - 1);  // remove any previous results but not suggested queries (would make it impossible to bring back)

            final int[] resultFlag = {2};   // keeps track of whether there are any search results

            CollectionReference charities = db.collection("charities");                 // get charity collection
            Query queryNames = charities.orderBy("name").startAt(input).endAt(input + "\uf8ff");    // search for charity name
            queryNames.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                        if (!task.getResult().isEmpty())
                            for (QueryDocumentSnapshot document : task.getResult())
                                addCard(document);                                                  // add new card for each result
                        else    resultFlag[0]--;    // no results from amongst charity names
                    else    Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            Query queryTags = charities.whereArrayContains("tags", input);                      // check tags for each charity
            queryTags.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                        if (!task.getResult().isEmpty())
                            for (QueryDocumentSnapshot document : task.getResult())
                                addCard(document);                                                  // add new card for each result
                        else    resultFlag[0]--;    // no results from amongst tags
                    else    Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            if (resultFlag[0] == 0) Toast.makeText(Home.this, "No results", Toast.LENGTH_SHORT).show();
            // toast shown if no results found in charity names or in charity tags

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {  // runs on any changes at all

            if (newText.isEmpty())  // if query field is empty
            {
                relativeLayout.removeViews(1, relativeLayout.getChildCount() - 1);  // remove any previous results but not suggested queries (would make it impossible to bring back)
                FlexboxLayout flexboxLayout = findViewById(R.id.categoryHints); // bring back suggested queries
                flexboxLayout.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };

    /**
     * signs out user and loads login page
     * @param view is the view calling the method using it's onClick method
     */
    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity((new Intent(this, Login.class)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    /**
     * Runs the respective suggested query
     * @param view is the view calling the method using it's onClick method
     */
    public void suggestedQuery(View view)
    {
        CardView cardView = (CardView) view;    // gets the card that was clicked
        CharSequence query = ((TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1)).getText();  // gets text from text field inside card
        searchBar.setQuery(query, true);    // runs query with text
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); //load layout

        db = FirebaseFirestore.getInstance();                   // set elements globally since required elsewhere
        searchBar = (SearchView) findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(queryTextListener);
        relativeLayout = (LinearLayout) findViewById(R.id.relativeLayout);
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