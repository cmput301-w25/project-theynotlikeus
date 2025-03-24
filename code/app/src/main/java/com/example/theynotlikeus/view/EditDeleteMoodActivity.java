package com.example.theynotlikeus.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class EditDeleteMoodActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private MoodController moodController;
    private String moodId;
    private Mood moodToEdit;
    private final int trigger_length_limit = 200;

    // UI elements.
    private Spinner moodSpinner;
    private EditText triggerEditText;
    private Spinner socialSituationSpinner;
    private Switch geolocationSwitch;
    private Switch privacySwitch;
    private Button selectImageButton;
    private ImageButton deleteButton;
    private ImageButton backButton;
    private Button saveButton;

    // Image handling.
    private Uri imageUri;
    private StorageReference storageRef;

    private ImageView setImage;

    // Geolocation fields.
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private android.location.Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_mood);

        // Initialize Firebase Storage reference.
        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        // Bind UI elements.
        moodSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_currentMoodspinner);
        triggerEditText = findViewById(R.id.editText_DeleteEditMoodActivity_triggerInput);
        socialSituationSpinner = findViewById(R.id.spinner_DeleteEditMoodActivity_socialsituation);
        geolocationSwitch = findViewById(R.id.switch_DeleteEditMoodActivity_geoSwitch);
        privacySwitch = findViewById(R.id.switch_ActivityEditDeleteMood_privacy);
        deleteButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_delete);
        backButton = findViewById(R.id.imageButton_DeleteEditMoodActivity_back);
        saveButton = findViewById(R.id.button_DeleteEditMoodActivity_save);
        selectImageButton = findViewById(R.id.button_DeleteEditMoodActivity_selectImage);
        setImage = findViewById(R.id.imageview_DeleteEditMoodActivity_photo);

        // Initialize the MoodController.
        moodController = new MoodController();

        // Initialize geolocation components.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);         // 5 seconds
        locationRequest.setFastestInterval(2000);    // 2 seconds
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null && location.getAccuracy() < 50) {
                        currentLocation = location;
                        // Once a good location is found, stop updates.
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        requestingLocationUpdates = false;
                        break;
                    }
                }
            }
        };

        // Retrieve the Mood object (if passed) or moodId.
        Mood passedMood = (Mood) getIntent().getSerializableExtra("mood");
        if (passedMood != null) {
            moodToEdit = passedMood;
            moodId = moodToEdit.getDocId();
        } else {
            moodId = getIntent().getStringExtra("moodId");
        }

        // Setup mood spinner.
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Setup social situation spinner.
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // If we already have a Mood object, update UI immediately.
        if (moodToEdit != null) {
            moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
            triggerEditText.setText(moodToEdit.getTrigger());
            if (moodToEdit.getSocialSituation() != null) {
                socialSituationSpinner.setSelection(getSocialSituationIndex(moodToEdit.getSocialSituation()));
            }
            // Load the previous image if available.
            if (moodToEdit.getPhotoUrl() != null && !moodToEdit.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(moodToEdit.getPhotoUrl())
                        .into(setImage);
            }
            // Set privacy switch accordingly.
            privacySwitch.setChecked(moodToEdit.isPublic());
        } else if (moodId != null) {
            // Otherwise, load mood data from the database.
            loadMoodData();
        }

        // Set up image selection.
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Save button: Update mood details.
        saveButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection", Toast.LENGTH_SHORT).show();
                return;
            }
            moodToEdit.setMoodState(moodState);

            // Update trigger.
            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                if (trigger.length() > trigger_length_limit) {
                    Toast.makeText(EditDeleteMoodActivity.this,
                            "Trigger has too many characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                moodToEdit.setTrigger(trigger);
            }

            // Update social situation.
            String socialText = socialSituationSpinner.getSelectedItem().toString().trim();
            if (!socialText.isEmpty()) {
                try {
                    Mood.SocialSituation socialSituation =
                            Mood.SocialSituation.valueOf(socialText.toUpperCase().replace(" ", "_"));
                    moodToEdit.setSocialSituation(socialSituation);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Invalid social situation", Toast.LENGTH_SHORT).show();
                }
            }

            // Update privacy.
            moodToEdit.setPublic(privacySwitch.isChecked());

            // Update location if geolocation toggle is enabled.
            if (geolocationSwitch.isChecked()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                    Toast.makeText(this, "Location permission required. Please try saving again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startLocationUpdates();
                if (currentLocation != null) {
                    moodToEdit.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                } else {
                    Toast.makeText(this, "Acquiring location, please try again in a few seconds.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // If a new image was selected, upload it; otherwise, update mood directly.
            if (imageUri != null) {
                uploadImageAndSaveMood(moodToEdit);
            } else {
                updateMoodInDatabase(moodToEdit);
            }
        });

        // Delete button: Remove the mood.
        deleteButton.setOnClickListener(v -> {
            if (moodToEdit == null) {
                Toast.makeText(this, "Mood not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            moodController.deleteMood(moodToEdit.getDocId(), () -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Mood deleted successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditDeleteMoodActivity.this, MainActivity.class);
                intent.putExtra("fragmentToLoad", "HomeMyMoodsFrag");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, e -> {
                Toast.makeText(EditDeleteMoodActivity.this,
                        "Error deleting mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        // Back button: Finish activity.
        backButton.setOnClickListener(v -> finish());
    }

    // Opens the image picker.
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Update the image button to preview the selected image.
            setImage.setImageURI(imageUri);
        }
    }

    // Uploads the selected image and then updates the mood.
    private void uploadImageAndSaveMood(Mood mood) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        // Get the file size.
        Cursor returnCursor = getContentResolver().query(imageUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        long fileSizeInBytes = returnCursor.getLong(sizeIndex);
        returnCursor.close();

        // Check admin preference for image size limit.
        SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        boolean isLimitOn = AdminActivity.isLimitEnabled(prefs);
        if (isLimitOn && fileSizeInBytes > 65536) {
            Toast.makeText(this, "Image too large. Must be under 65536 bytes.", Toast.LENGTH_SHORT).show();
            return;
        }

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            mood.setPhotoUrl(imageUrl);
                            updateMoodInDatabase(mood);
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
    }

    // Updates the mood record in the database.
    private void updateMoodInDatabase(Mood mood) {
        moodController.updateMood(mood, () -> {
            Toast.makeText(EditDeleteMoodActivity.this,
                    "Mood updated successfully!", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("mood", (Serializable) mood);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, e -> {
            Toast.makeText(EditDeleteMoodActivity.this,
                    "Error updating mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Loads the mood data from Firestore.
    private void loadMoodData() {
        moodController.getMood(moodId, mood -> {
            moodToEdit = mood;
            if (moodToEdit != null) {
                moodSpinner.setSelection(getMoodStateIndex(moodToEdit.getMoodState()));
                triggerEditText.setText(moodToEdit.getTrigger());
                if (moodToEdit.getSocialSituation() != null) {
                    socialSituationSpinner.setSelection(getSocialSituationIndex(moodToEdit.getSocialSituation()));
                }
                // Load the existing image if available.
                if (moodToEdit.getPhotoUrl() != null && !moodToEdit.getPhotoUrl().isEmpty()) {
                    Glide.with(EditDeleteMoodActivity.this)
                            .load(moodToEdit.getPhotoUrl())
                            .into(setImage);
                }
                privacySwitch.setChecked(moodToEdit.isPublic());
            }
        }, e -> {
            Toast.makeText(EditDeleteMoodActivity.this,
                    "Error loading mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Starts location updates.
    private void startLocationUpdates() {
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    // Stops location updates.
    private void stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdates = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If geolocation is enabled, start location updates.
        if (geolocationSwitch.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Handle location permission results.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted. Please save the mood again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Returns the spinner index for a given social situation.
    private int getSocialSituationIndex(Mood.SocialSituation socialSituation) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) socialSituationSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(
                    socialSituation.toString().replace("_", " "))) {
                return i;
            }
        }
        return 0;
    }

    // Returns the spinner index for a given mood state.
    private int getMoodStateIndex(Mood.MoodState moodState) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) moodSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(moodState.toString())) {
                return i;
            }
        }
        return 0;
    }
}
