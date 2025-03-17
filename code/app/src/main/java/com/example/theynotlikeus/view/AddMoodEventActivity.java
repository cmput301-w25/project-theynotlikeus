package com.example.theynotlikeus.view;
/*
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.controller.MoodController;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.function.Consumer;

public class AddMoodEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String username;
    private final int trigger_length_limit = 200;
    private MoodController moodController;

    private Button addImageButton;
    private ImageView imagePreview;
    private Uri imageUri; // Stores the selected image URI

    private StorageReference storageRef; // Firebase Storage reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");
        moodController = new MoodController();

        // Initialize Firebase Storage reference
        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        // UI Elements
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
        addImageButton = findViewById(R.id.button_select_photo);
        imagePreview = findViewById(R.id.imageview_mood_photo);
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        Switch publicButton = findViewById(R.id.switch_ActivityAddMoodEvent_privacy);
        Switch geolocation = findViewById(R.id.switch_ActivityAddMoodEvent_geolocation);

        // Mood Spinner
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Social Situation Spinner
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Image Selection Button
        addImageButton.setOnClickListener(v -> openImagePicker());

        // Save Button
        saveButton.setOnClickListener(v -> {
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
                return;
            }
            Mood mood = new Mood(moodState);

            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                try {
                    validateTrigger(trigger, trigger_length_limit);
                    mood.setTrigger(trigger);
                } catch (ArithmeticException ex) {
                    Toast.makeText(AddMoodEventActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_")
                );
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
                // Optionally handle invalid social situation.
            }

            mood.setUsername(username);

            mood.setPublic(publicButton.isChecked()); // If checked, it's public; otherwise, private


            if (imageUri != null) {
                uploadImage(mood);
            } else {
                saveMoodToDatabase(mood, null);
            }
        });

        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
    }

    // Open Image Picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri); // Show the selected image in the ImageView
        }
    }

    // Upload Image to Firebase Storage
    private void uploadImage(Mood mood) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveMoodToDatabase(mood, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
    }

    // Save Mood to Firestore
    private void saveMoodToDatabase(Mood mood, String imageUrl) {
        if (imageUrl != null) {
            mood.setPhotoUrl(imageUrl);
        }

        moodController.addMood(mood, () -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }), e -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

    static void validateTrigger(String trigger, int limit) {
        if (trigger.length() > limit) {
            throw new ArithmeticException("Trigger has too many characters!");
        }
    }
}




import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.model.Mood;
import com.example.theynotlikeus.controller.MoodController;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddMoodEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String username;
    private final int trigger_length_limit = 200;
    private MoodController moodController;

    private Button addImageButton;
    private ImageView imagePreview;
    private Uri imageUri; // Stores the selected image URI

    private StorageReference storageRef; // Firebase Storage reference

    // FusedLocationProviderClient for geolocation
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");
        moodController = new MoodController();

        // Initialize Firebase Storage reference
        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // UI Elements
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
        addImageButton = findViewById(R.id.button_select_photo);
        imagePreview = findViewById(R.id.imageview_mood_photo);
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        Switch publicButton = findViewById(R.id.switch_ActivityAddMoodEvent_privacy);
        Switch geolocation = findViewById(R.id.switch_ActivityAddMoodEvent_geolocation);

        // Mood Spinner
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner
        );
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Social Situation Spinner
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner
        );
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Image Selection Button
        addImageButton.setOnClickListener(v -> openImagePicker());

        // Save Button
        saveButton.setOnClickListener(v -> {
            // Retrieve the selected mood
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
                return;
            }
            Mood mood = new Mood(moodState);

            // Set trigger if provided
            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                try {
                    validateTrigger(trigger, trigger_length_limit);
                    mood.setTrigger(trigger);
                } catch (ArithmeticException ex) {
                    Toast.makeText(AddMoodEventActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Set social situation
            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_")
                );
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
                // Optionally handle invalid social situation.
            }

            mood.setUsername(username);
            mood.setPublic(publicButton.isChecked());

            // If geolocation switch is enabled, attempt to retrieve location
            if (geolocation.isChecked()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                    Toast.makeText(this, "Location permission required. Please try saving again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        mood.setLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(AddMoodEventActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                    }
                    // Proceed with saving mood event
                    processMoodSaving(mood);
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddMoodEventActivity.this, "Failed to retrieve location.", Toast.LENGTH_SHORT).show();
                    processMoodSaving(mood);
                });
            } else {
                // If geolocation is not enabled, proceed directly
                processMoodSaving(mood);
            }
        });

        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
    }

    // Helper method to process mood saving (with or without image)
    private void processMoodSaving(Mood mood) {
        if (imageUri != null) {
            uploadImage(mood);
        } else {
            saveMoodToDatabase(mood, null);
        }
    }

    // Open Image Picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri); // Show the selected image in the ImageView
        }
    }

    // Upload Image to Firebase Storage
    private void uploadImage(Mood mood) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveMoodToDatabase(mood, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
    }

    // Save Mood to Firestore
    private void saveMoodToDatabase(Mood mood, String imageUrl) {
        if (imageUrl != null) {
            mood.setPhotoUrl(imageUrl);
        }

        moodController.addMood(mood, () -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }), e -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

    // Validate the trigger length
    static void validateTrigger(String trigger, int limit) {
        if (trigger.length() > limit) {
            throw new ArithmeticException("Trigger has too many characters!");
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted. Please save the mood again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
*/


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class AddMoodEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String username;
    private final int trigger_length_limit = 200;
    private MoodController moodController;

    private Button addImageButton;
    private ImageView imagePreview;
    private Uri imageUri; // Stores the selected image URI

    private StorageReference storageRef; // Firebase Storage reference

    // FusedLocationProviderClient and location objects
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private android.location.Location currentLocation; // Updated location

    // ToggleButtons (using ToggleButton to mimic the built-in sample)
    private Switch togglePublic;
    private Switch toggleGeolocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");
        moodController = new MoodController();

        // Initialize Firebase Storage reference
        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a LocationRequest with high accuracy and set update intervals.
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);         // Every 5 seconds
        locationRequest.setFastestInterval(2000);    // At most every 2 seconds

        // Define the LocationCallback that receives location updates.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                // Process all received location updates.
                for (android.location.Location location : locationResult.getLocations()) {
                    // Use the location if it meets your accuracy criteria (e.g., within 50 meters)
                    if (location != null && location.getAccuracy() < 50) {
                        currentLocation = location;
                        // Once a sufficiently accurate location is received, stop updates.
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        requestingLocationUpdates = false;
                        break;
                    }
                }
            }
        };

        // UI Elements setup
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
        addImageButton = findViewById(R.id.button_select_photo);
        imagePreview = findViewById(R.id.imageview_mood_photo);
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        togglePublic = findViewById(R.id.switch_ActivityAddMoodEvent_privacy);
        toggleGeolocation = findViewById(R.id.switch_ActivityAddMoodEvent_geolocation);

        // Populate mood spinner
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner);
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Populate social situation spinner
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner);
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Set up image selection button.
        addImageButton.setOnClickListener(v -> openImagePicker());

        // Save Button: Create the Mood object, request location updates if enabled, then save.
        saveButton.setOnClickListener(v -> {
            // Create mood from selected spinner option
            String selectedMood = moodSpinner.getSelectedItem().toString();
            Mood.MoodState moodState;
            try {
                moodState = Mood.MoodState.valueOf(selectedMood.toUpperCase());
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid mood selection.", Toast.LENGTH_SHORT).show();
                return;
            }
            Mood mood = new Mood(moodState);

            // Validate and set trigger text if provided.
            String trigger = triggerEditText.getText().toString().trim();
            if (!trigger.isEmpty()) {
                try {
                    validateTrigger(trigger, trigger_length_limit);
                    mood.setTrigger(trigger);
                } catch (ArithmeticException ex) {
                    Toast.makeText(AddMoodEventActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Set social situation.
            String selectedSocial = socialSituationSpinner.getSelectedItem().toString();
            try {
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(
                        selectedSocial.toUpperCase().replace(" ", "_"));
                mood.setSocialSituation(socialSituation);
            } catch (IllegalArgumentException e) {
                // Optionally handle invalid social situation.
            }

            mood.setUsername(username);
            mood.setPublic(togglePublic.isChecked());

            // If geolocation is enabled, start location updates.
            if (toggleGeolocation.isChecked()) {
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
                // If we already have an updated location, use it.
                if (currentLocation != null) {
                    mood.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                    processMoodSaving(mood);
                } else {
                    Toast.makeText(this, "Acquiring location, please try again in a few seconds.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If geolocation is off, proceed normally.
                processMoodSaving(mood);
            }
        });

        findViewById(R.id.button_ActivityAddMoodEvent_backbutton).setOnClickListener(v -> finish());
    }

    // Helper method: Continue saving mood (with image upload if needed)
    private void processMoodSaving(Mood mood) {
        if (imageUri != null) {
            uploadImage(mood);
        } else {
            saveMoodToDatabase(mood, null);
        }
    }

    // Start requesting location updates.
    private void startLocationUpdates() {
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    // Stop location updates.
    private void stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdates = false;
        }
    }

    // Open image picker for selecting an image.
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    // Upload image to Firebase Storage and then save mood with the image URL.
    private void uploadImage(Mood mood) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveMoodToDatabase(mood, imageUrl);
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
    }

    // Save the mood to the database using MoodController.
    private void saveMoodToDatabase(Mood mood, String imageUrl) {
        if (imageUrl != null) {
            mood.setPhotoUrl(imageUrl);
        }
        moodController.addMood(mood, () -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }), e -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

    // Validate trigger length.
    public static void validateTrigger(String trigger, int limit) {
        if (trigger.length() > limit) {
            throw new ArithmeticException("Trigger has too many characters!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toggleGeolocation.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Handle runtime permission result for location access.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted. Please save the mood again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

