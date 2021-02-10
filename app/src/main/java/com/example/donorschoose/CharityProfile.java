package com.example.donorschoose;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for charity profile page XML
 */
public class CharityProfile extends AppCompatActivity {

    private FirebaseFirestore db;       // firebase database
    private int buttonFlag = 0;         // keeps track of whether page is editable or not
    DocumentSnapshot charityDocument;   // charity profile document from firebase
    String charityID;                   // id of this charity

    private Uri backgroundImageFile = null, activityImageFile = null;   // paths to local images for backgrounds
    ImageView backgroundImage;          // charity profile background image
    TextView activityImageDefaultText;  // new activity image selection view

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
        this.charityID = charityID;
        db.collection("charities").document(charityID).addSnapshotListener(CharityProfile.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                displayProfile(documentSnapshot);
                retrieveCharityActivity(charityID);
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

        ((TextView) findViewById(R.id.nameTextView)).setText(charityDocument.getString("name"));    // charity name
        ((TextView) findViewById(R.id.descriptionTextView)).setText(charityDocument.getString("description"));   // charity description

        ImageView imageView = (ImageView) findViewById(R.id.backgroundImage);   // charity background
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
     * Creates outer structure for new card to add charity activities
     * @return the create card
     */
    private CardView createCard()
    {
        int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()); // dp to px
        CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams cardViewLayout = new LinearLayout.LayoutParams(pixels, pixels);   // 200dp x 200dp size
        cardViewLayout.setMargins(10, 10, 10, 10);
        cardView.setLayoutParams(cardViewLayout);
        cardView.setRadius(15);
        cardView.setCardBackgroundColor(Color.rgb(102, 217, 238));  // background in case image fails to load
        return cardView;
    }

    /**
     * Creates text field to hold charity activity name
     * @param nameString is the charity activity name
     * @return the created text field
     */
    private TextView createName(String nameString)
    {
        TextView name = new TextView(getApplicationContext());
        name.setBackgroundColor(Color.BLACK);   // slightly black background to make text visible on top of background image
        name.getBackground().setAlpha(150);
        name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        name.setText(nameString);
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        name.setTextColor(Color.WHITE);
        name.setPadding(20,10,10,10);
        name.setLines(2);                                           // is exactly 2 lines; adds blank line if fewer
        return name;
    }

    /**
     * Creates text field to hold charity activity description
     * @param descriptionString is the charity activity description
     * @return the created text field
     */
    private TextView createDescription(String descriptionString)
    {
        TextView description = new TextView(getApplicationContext());
        description.setBackgroundColor(Color.BLACK);    // slightly black background to make text visible on top of background image
        description.getBackground().setAlpha(150);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        description.setText(descriptionString);
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        description.setTextColor(Color.WHITE);
        description.setPadding(20,10,10,10);
        description.setLines(3);                                        // is exactly 3 lines; blank lines added if fewer
        description.setEllipsize(TextUtils.TruncateAt.END);             // adds ellipses if longer than 3 lines
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
     * Start making a card for each charity activity and add all inner details
     * @param data holds the details of the activity
     */
    private void addCard(Map<String, Object> data)
    {
        findViewById(R.id.defaultText).setVisibility(View.GONE);    // remove the default no activity text field
        CardView cardView = createCard();                           // create outer structure for new card
        TextView name = createName(data.get("title").toString());   // get charity activity details from document and create text fields
        TextView description = createDescription(data.get("description").toString());

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());  // create new inner layout
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(name);                                             // add charity activity details to inner layout
        linearLayout.addView(description);
        linearLayout.setGravity(Gravity.BOTTOM);
        linearLayout = addBackground(linearLayout, data.get("image").toString());   // add background image to inner layout (preserves card corner radius this way)

        cardView.addView(linearLayout);     // add inner layout to card
        LinearLayout activityList = findViewById(R.id.activityList);
        activityList.addView(cardView);   // add card to outer layout
    }

    /**
     * Retrieves all charity activites for given charity.
     * @param charityID is the id of the current charity
     */
    public void retrieveCharityActivity(String charityID)
    {
        db.collection("charities").document(charityID).collection("charityActivity").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                    if (!task.getResult().isEmpty())
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals("blank"))  // for every document other than the default blank one needed to create the collection
                                addCard(document.getData());        // add new card for each activity
                        }
                    else    Toast.makeText(CharityProfile.this, "Unable to retrieve activities", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Makes charity profile page editable for users with Edit access
     */
    private void makeEditable()
    {
        ((Button) findViewById(R.id.homeButton)).setVisibility(View.GONE);      // hiding homepage and profile page icons from menu bar; Edit access users cannot use these
        ((Button) findViewById(R.id.profileButton)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.donateButton)).setText("Edit");             // changing donate button to edit button
        buttonFlag = 1;                                                         // not actively editing but has edit access
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);       // setting imageView globally since needed elsewhere
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
                activityImageFile = null;       // remove any activity image file; may cause problems if not null
                imageChange(0);
            }
        });

        findViewById(R.id.defaultText).setVisibility(View.GONE);            // remove default no activity text
        findViewById(R.id.addActivityButton).setVisibility(View.VISIBLE);   // make add new activity button visible
    }

    /**
     * Creates the dialog to add new activities
     * @param view is the view calling the method using its onClick method
     */
    public void createPopupWindow(View view)
    {
        activityImageFile = null;   // remove any pre-existing activity image file

        // Main layout
        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 50, 50, 50);
        linearLayout.setLayoutParams(mainLayoutParams);

        // Main text boxes
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, 0, 0, 20);

        EditText editName = new EditText(this);
        editName.setHint("New Activity Title");
        editName.setLayoutParams(textParams);

        EditText editDescription = new EditText(this);
        editDescription.setHint("New Activity Description");
        editDescription.setLayoutParams(textParams);

        // Image selection field layout; causes problems with width if removed
        LinearLayout imageSelectLayout = new LinearLayout(this);
        imageSelectLayout.setOrientation(LinearLayout.HORIZONTAL);
        imageSelectLayout.setGravity(Gravity.CENTER);

        // text box to select activity image
        LinearLayout.LayoutParams imagePrevParams = new LinearLayout.LayoutParams(400, 400);
        imagePrevParams.setMargins(0, 20, 0, 0);

        activityImageDefaultText = new TextView(this);
        activityImageDefaultText.setText("Select\nBackground\nImage");
        activityImageDefaultText.setBackgroundColor(0xFF69CAED);
        activityImageDefaultText.setTextColor(getResources().getColor(R.color.white));
        activityImageDefaultText.setGravity(Gravity.CENTER);
        activityImageDefaultText.setLayoutParams(imagePrevParams);

        imageSelectLayout.addView(activityImageDefaultText);

        // Save button
        LinearLayout.LayoutParams saveButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        saveButtonParams.setMargins(200, 100, 200, 0);
        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setHeight(50);
        saveButton.setBackgroundColor(0xFF69CAED);
        saveButton.setTextSize(18);
        saveButton.setTextColor(getResources().getColor(R.color.white));
        saveButton.setTypeface(ResourcesCompat.getFont(this, R.font.salsa));
        saveButton.setLayoutParams(saveButtonParams);

        // adding all the parts to the main layout
        linearLayout.addView(editName);
        linearLayout.addView(editDescription);
        linearLayout.addView(imageSelectLayout);
        linearLayout.addView(saveButton);

        // adding main layout to scrollView just in case
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(linearLayout);

        // Adding scrollView to card to enable corner radius
        CardView cardView = new CardView(this);
        cardView.addView(scrollView);
        cardView.setRadius(20);

        // Adding card to outer layout to center horizontally
        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setGravity(Gravity.CENTER);
        outerLayout.addView(cardView);
        outerLayout.setPadding(20, 20, 20, 20);

        // creating actual dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(outerLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this allows card radius to become noticeable

        // onClickListener for select activity image 'button'
        activityImageDefaultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { imageChange(1); }
        });

        // onClickListener for saving the added activity
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activityName = editName.getText().toString();
                String activityDescription = editDescription.getText().toString();

                if (activityName.isEmpty())
                {
                    editName.setError("Activity title cannot be empty!");
                    editName.requestFocus();
                }
                else if (activityName.length() > 30)
                {
                    editName.setError("Activity name cannot be more than 30 characters long!");
                    editName.requestFocus();
                }
                else if (activityDescription.isEmpty())
                {
                    editDescription.setError("Activity description cannot be empty!");
                    editDescription.requestFocus();
                }
                else
                {
                    dialog.dismiss();
                    addNewActivity(activityName, activityDescription);
                }
            }
        });
    }

    /**
     * Adds a new activity to firebase
     * @param name is the name of the activity
     * @param description is the description of the activity
     */
    private void addNewActivity(String name, String description)
    {
        String newID = db.collection("charity").document(charityID).collection("charityActivity").document().getId();
        // generates auto ID

        String imageRef = updatePicture(newID); // upload image to firebase storage; get back file reference

        Map<String, Object> newActivity = new HashMap<>();  // create activity document
        newActivity.put("title", name);
        newActivity.put("description", description);
        if (imageRef != null)       // there was no image
            newActivity.put("image", imageRef);
        else                        // there was an image
            newActivity.put("image", "");

        // add document to database
        db.collection("charities").document(charityID).collection("charityActivity").document(newID).set(newActivity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { // once document is successfully added
                        new Handler().postDelayed(new Runnable() {  // wait 1 second; image does not load otherwise (weird?)
                            public void run() {
                                addCard(newActivity);   // add the new activity to the in-app list
                            }
                        }, 1000);
                    }
                });
    }

    /**
     * Opens image picker
     */
    private void imageChange(int requestCode)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    /**
     * Sets selected local image to charity profile background
     * @param requestCode verifies which function made the request
     * @param resultCode verifies that result was successful
     * @param data is the image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null && data.getData() != null)  // if successfully retrieved image
        {
            if (requestCode == 0)   // charity background image being changed
            {
                backgroundImageFile = data.getData();
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), backgroundImageFile); // set image to charity profile background
                    backgroundImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(CharityProfile.this, "Invalid file", Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == 1)  // new activity image being added
            {
                activityImageFile = data.getData();
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), activityImageFile);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                    bitmapDrawable.setGravity(Gravity.CENTER);
                    activityImageDefaultText.setBackground(bitmapDrawable);
                    activityImageDefaultText.setText("");
                } catch (IOException e) {
                    Toast.makeText(CharityProfile.this, "Invalid file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Saves changes to charity profile by edit access user
     */
    private void saveProfile()
    {
        activityImageFile = null;   // may cause problems with charity background image changes otherwise
        TextView name = findViewById(R.id.nameTextView);
        TextView description = findViewById(R.id.descriptionTextView);

        if (name.getText().toString().isEmpty())
        {
            name.setError("Please enter a name");
            name.requestFocus();
        }
        else if (description.getText().toString().isEmpty())
        {
            description.setError("Please enter a description");
            description.requestFocus();
        }
        else
        {
            name.setEnabled(false);             // name and description fields are not uneditable
            description.setEnabled(false);
            name.setBackgroundResource(android.R.color.white);
            description.setBackgroundResource(android.R.color.white);

            backgroundImage.setOnClickListener(null);           // no longer listening for clicks on image
            String imageRef = updatePicture(null);     // upload image to firebase storage and get reference
            if (imageRef != null)                               // if there was a change to the background image
                charityDocument.getReference().update("background", imageRef);

            charityDocument.getReference().update("name", name.getText().toString());       // updating name and description values in firebase
            charityDocument.getReference().update("description", description.getText().toString());

            findViewById(R.id.addActivityButton).setVisibility(View.GONE);  // hide the add activity button
            if (((LinearLayout) findViewById(R.id.activityList)).getChildCount() == 1)  // if there are still no activities
                findViewById(R.id.defaultText).setVisibility(View.VISIBLE); // unhide the no activity text

            makeEditable();                             // return to editable (but not actively editing) state
        }
    }

    /**
     * Uploads new background image to firebase
     */
    private String updatePicture(String activityID)
    {
        StorageReference ref;
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();   // get charity ID
        Uri filePath = null;

        if (backgroundImageFile != null && activityID == null)  // charity background image was changed
        {
            filePath = backgroundImageFile;
            backgroundImageFile = null; // to avoid problems
        }
        else if (activityImageFile != null && activityID != null)   // new activity image was added
        {
            filePath = activityImageFile;
            activityImageFile = null;   // to avoid problems
        }

        if (filePath != null)   // new image was actually picked
        {

            if (activityID == null) // charity background image was changed
                ref = FirebaseStorage.getInstance().getReference().child("images/" + userID);  // path to storage location
            else                    // new activity image was added
                ref = FirebaseStorage.getInstance().getReference().child("activityImages/" + userID + "/" + activityID);

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {   // place image in storage location
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CharityProfile.this, "Failed to save image.", Toast.LENGTH_SHORT).show();
                }
            });
            return ref.toString();  // return a reference to the stored image to the calling function
        }
        return null;
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