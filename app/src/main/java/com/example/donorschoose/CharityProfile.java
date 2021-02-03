package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Controller class for charity profile page XML
 */
public class CharityProfile extends AppCompatActivity {

    private FirebaseFirestore db;       // firebase database
    private int buttonFlag = 0;         // keeps track of whether page is editable or not
    DocumentSnapshot charityDocument;   // charity profile document from firebase

    private Uri filePath;               // path to local image chosen as new charity background
    private final int PICK_IMAGE_REQUEST = 71; // image picker activity request code
    ImageView backgroundImage;          // charity profile background image

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_profile);  // load layout

        db = FirebaseFirestore.getInstance();               // retrieve database

        Bundle extras = getIntent().getExtras();            // get charity id and check if edit access is given
        retrieveCharityData(extras.getString("Charity ID"));
        if (extras.getString("Access Level").equals("Edit")) makeEditable();
    }

    /**
     * retrieves details of the charity
     * @param charityID is the ID of the current charity; used to retrieve the data
     */
    public void retrieveCharityData(String charityID)
    {
        db.collection("charities").document(charityID).addSnapshotListener(CharityProfile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                displayProfile(documentSnapshot);
            }
        });
    }

    /**
     * Displays details from charity document
     * @param document is the charity's document
     */
    public void displayProfile(DocumentSnapshot document)
    {
        charityDocument = document; // setting global variable since also used elsewhere

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView); // charity name
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);   // charity description
        ImageView imageView = (ImageView) findViewById(R.id.backgroundImage);   // charity background

        nameTextView.setText(charityDocument.getString("name"));
        descriptionTextView.setText(charityDocument.getString("description"));

        String downloadUrl = charityDocument.getString("background");   // downloading background image
        if (!downloadUrl.isEmpty())
        {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) { Picasso.with(getApplicationContext()).load(uri.toString()).into(imageView); }});
        }
    }

    /**
     * Makes charity profile page editable for users with Edit access
     */
    private void makeEditable()
    {
        ((Button) findViewById(R.id.homeButton)).setVisibility(View.GONE);     // hiding homepage and profile page icons from menu bar; Edit access users cannot use these
        ((Button) findViewById(R.id.profileButton)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.donateButton)).setText("Edit");                 // changing donate button to edit button
        buttonFlag = 1;                                                             // not actively editing but has edit access
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);           // setting imageView globally since needed elsewhere
    }

    /**
     * Changes method called on button click depending on buttonFlag state
     * @param view is the view calling the method using it's onClick method
     */
    public void buttonHandler(View view)
    {
        if (buttonFlag == 0)  donate();             // users with no edit access
        else if (buttonFlag == 1) editProfile();    // users with edit access but not actively editing
        else if (buttonFlag == 2) saveProfile();    // user in edit state
    }

    /**
     * Changes charity page view to edit state
     */
    private void editProfile()
    {
        ((Button) findViewById(R.id.donateButton)).setText("Save");     // change button text
        buttonFlag = 2;                                                 // currently editing

        TextView name = ((TextView) findViewById(R.id.nameTextView));
        TextView description = ((TextView) findViewById(R.id.descriptionTextView));

        name.setEnabled(true);                                          // name and description fields are now editable
        description.setEnabled(true);
        name.setBackgroundResource(android.R.drawable.editbox_background_normal);
        description.setBackgroundResource(android.R.drawable.editbox_background_normal);


        backgroundImage.setOnClickListener(new View.OnClickListener() { // listening for clicks on the background image; will open image picker
            @Override
            public void onClick(View v) {
                imageChange();
            }
        });
    }

    /**
     * Opens image picker
     */
    private void imageChange()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Sets selected local image to charity profile background
     * @param requestCode verifies that request is valid
     * @param resultCode verifies that result was succesfful
     * @param data is the image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) // if successfully retrieved image
        {
            filePath = data.getData();  // get image's file path; set gloablly since used elsewhere
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath); // set image to charity profile background
                backgroundImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves changes to charity profile by edit access user
     */
    private void saveProfile()
    {
        TextView name = ((TextView) findViewById(R.id.nameTextView));
        TextView description = ((TextView) findViewById(R.id.descriptionTextView));

        name.setEnabled(false);             // name and description fields are not uneditable
        description.setEnabled(false);
        name.setBackgroundResource(android.R.color.white);
        description.setBackgroundResource(android.R.color.white);

        backgroundImage.setOnClickListener(null);   // no longer listening for clicks on image
        updatePicture();                            // upload image to firebase

        charityDocument.getReference().update("name", name.getText().toString());       // updating name and description values in firebase
        charityDocument.getReference().update("description", description.getText().toString());

        makeEditable();                             // return to editable (but not actively editing) state
    }

    /**
     * Uploads new background image to firebase
     */
    private void updatePicture()
    {
        if (filePath != null)   // new image was actually picked
        {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();   // get charity ID
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + userID);  // path to storage location
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {   // place image in storage location
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    charityDocument.getReference().update("background", ref.toString());    // if successfully uploaded, change charity's background reference to new image
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CharityProfile.this, "Failed to save image.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Loads monetary donation page
     */
    public void donate()
    {
        Intent donatePage = new Intent(this, Donate.class);
        donatePage.putExtra("Charity ID", charityDocument.getId());     // sending charity ID and name to new page; needed there
        donatePage.putExtra("Charity Name", charityDocument.get("name").toString());
        startActivity(donatePage);
    }

    public void openMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(0);
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.VISIBLE);
    }

    public void closeMenu(View view)
    {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.animate().translationX(1000);
        LinearLayout menuBackground = findViewById(R.id.menuBackground);
        menuBackground.setVisibility(View.GONE);
    }

}