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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.controller.TriggerWordsController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class AddMoodEventActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String username;
    private final int trigger_length_limit = 200;
    private MoodController moodController;
    private TriggerWordsController triggerWordsController;

    private Button addImageButton;
    private ImageView imagePreview;
    private Uri imageUri; // Stores the selected image URI

    private StorageReference storageRef; // Firebase Storage reference

    // FusedLocationProviderClient and location objects.
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private android.location.Location currentLocation; // Updated location

    // Toggle switches for public and geolocation.
    private Switch togglePublic;
    private Switch toggleGeolocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        username = getIntent().getStringExtra("username");
        moodController = new MoodController();
        triggerWordsController = new TriggerWordsController();

        // Initialize Firebase Storage reference.
        storageRef = FirebaseStorage.getInstance().getReference("mood_images");

        // Initialize FusedLocationProviderClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a LocationRequest with high accuracy.
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);         // Every 5 seconds.
        locationRequest.setFastestInterval(2000);    // At most every 2 seconds.

        // Define the LocationCallback.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null && location.getAccuracy() < 50) {
                        currentLocation = location;
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        requestingLocationUpdates = false;
                        break;
                    }
                }
            }
        };

        // UI Elements setup.
        Spinner moodSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_currentmood);
        EditText triggerEditText = findViewById(R.id.edittext_ActivityAddMoodEvent_trigger);
        Spinner socialSituationSpinner = findViewById(R.id.spinner_ActivityAddMoodEvent_socialsituation);
        addImageButton = findViewById(R.id.button_select_photo);
        imagePreview = findViewById(R.id.imageview_mood_photo);
        Button saveButton = findViewById(R.id.button_ActivityAddMoodEvent_save);
        togglePublic = findViewById(R.id.switch_ActivityAddMoodEvent_privacy);
        toggleGeolocation = findViewById(R.id.switch_ActivityAddMoodEvent_geolocation);

        // Populate mood spinner.
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this, R.array.moods, R.layout.add_mood_event_spinner);
        moodAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        moodSpinner.setAdapter(moodAdapter);

        // Populate social situation spinner.
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this, R.array.social_situations, R.layout.add_mood_event_spinner);
        socialAdapter.setDropDownViewResource(R.layout.add_mood_event_spinner);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Set up image selection button.
        addImageButton.setOnClickListener(v -> openImagePicker());

        // Save Button: Create the Mood object and then check trigger words before saving.
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

            // Get trigger text.
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

            // Check trigger words before saving.
            checkAndSaveMood(mood);
        });

        findViewById(R.id.imagebutton_ActivityViewComments_backbutton).setOnClickListener(v -> finish());
    }

    /**
     * Checks if the mood's trigger text contains any banned words.
     * If a banned word is found, marks the mood as pending review so that it is hidden from feeds until approved,
     * then saves the mood.
     * If no banned words are found, saves the mood normally.
     */
    private void checkAndSaveMood(Mood mood) {
        String trigger = mood.getTrigger();
        if (trigger == null || trigger.isEmpty()) {
            saveMoodNormally(mood);
            return;
        }
        triggerWordsController.getAllTriggerWords(triggerWordList -> {
            boolean containsBanned = false;
            for (com.example.theynotlikeus.model.TriggerWord bannedWord : triggerWordList) {
                // Use getWord() on each TriggerWord for comparison.
                if (trigger.toLowerCase().contains(bannedWord.getWord().toLowerCase())) {
                    containsBanned = true;
                    break;
                }
            }
            if (containsBanned) {
                mood.setPendingReview(true);
            }
            saveMoodNormally(mood);
        }, error -> Toast.makeText(AddMoodEventActivity.this,
                "Error checking trigger words: " + error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveMoodNormally(Mood mood) {
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
            if (currentLocation != null) {
                mood.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                processMoodSaving(mood);
            } else {
                Toast.makeText(this, "Acquiring location, please try again in a few seconds.", Toast.LENGTH_SHORT).show();
            }
        } else {
            processMoodSaving(mood);
        }
    }

    // Helper method: Continue saving mood (with image upload if needed).
    private void processMoodSaving(Mood mood) {
        if (imageUri != null) {
            uploadImage(mood);
        } else {
            saveMoodToDatabase(mood, null);
        }
    }

    private void startLocationUpdates() {
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            requestingLocationUpdates = false;
        }
    }

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

    private void uploadImage(Mood mood) {
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        Cursor returnCursor = getContentResolver().query(imageUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        long fileSizeInBytes = returnCursor.getLong(sizeIndex);
        returnCursor.close();

        SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        boolean isLimitOn = AdminActivity.isLimitEnabled(prefs);

        if (isLimitOn && fileSizeInBytes > 65536) {
            Toast.makeText(this, "Image too large. Must be under 65536 bytes.", Toast.LENGTH_SHORT).show();
            return;
        }

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveMoodToDatabase(mood, imageUrl);
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
    }

    private void saveMoodToDatabase(Mood mood, String imageUrl) {
        if (imageUrl != null) {
            mood.setPhotoUrl(imageUrl);
        }
        moodController.addMood(mood, () -> runOnUiThread(() -> {
            // Check pending review flag and display appropriate message.
            if (mood.isPendingReview()) {
                Toast.makeText(AddMoodEventActivity.this, "Mood pending admin review", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddMoodEventActivity.this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }), e -> runOnUiThread(() -> {
            Toast.makeText(AddMoodEventActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

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
