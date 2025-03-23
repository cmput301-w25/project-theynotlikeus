package com.example.theynotlikeus.view;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CommunityMapActivity";
    private GoogleMap mMap;
    private MoodController moodController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current user's location (passed from previous screen)
    private double currentLat;
    private double currentLng;
    // Current user's identifier (needed for fetching friend list)
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_map);

        // Retrieve current user's location and identifier from intent extras.
        currentLat = getIntent().getDoubleExtra("latitude", 0.0);
        currentLng = getIntent().getDoubleExtra("longitude", 0.0);
        currentUser = getIntent().getStringExtra("username");
        if (currentUser == null || currentUser.isEmpty()) {
            currentUser = "defaultUser";
        }

        moodController = new MoodController();

        // Set up back button to finish this activity (return to HomeMapFrag).
        ImageButton backButton = findViewById(R.id.imageButton_CommunityMapActivity_back);
        backButton.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_CommunityMapActivity_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Center the map on the current user's location.
        LatLng currentLocation = new LatLng(currentLat, currentLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

        // Load friends' mood markers within a 5 km radius.
        loadFriendsMoodMarkers(currentLocation);
    }

    /**
     * Loads the current user's friend list from the "follow" collection.
     * For each friend, fetch their public mood events, and if an event is within 5 km of
     * the current location, add it as a marker on the map.
     */
    private void loadFriendsMoodMarkers(final LatLng currentLocation) {
        db.collection("follow")
                .whereEqualTo("follower", currentUser)
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    List<String> friendList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String friend = doc.getString("followee");
                        if (friend != null && !friend.isEmpty()) {
                            friendList.add(friend);
                        }
                    }
                    Log.d(TAG, "Found friends: " + friendList.toString());
                    // For each friend, fetch their public mood events.
                    for (String friend : friendList) {
                        moodController.getPublicMoodsByUser(friend,
                                moods -> {
                                    if (moods == null || moods.isEmpty()) {
                                        return;
                                    }
                                    for (Mood mood : moods) {
                                        if (mood.getLatitude() != null && mood.getLongitude() != null) {
                                            LatLng moodLocation = new LatLng(mood.getLatitude(), mood.getLongitude());
                                            // Calculate distance (in meters) between currentLocation and moodLocation.
                                            float[] results = new float[1];
                                            Location.distanceBetween(
                                                    currentLocation.latitude, currentLocation.longitude,
                                                    moodLocation.latitude, moodLocation.longitude, results);
                                            if (results[0] <= 5000) { // within 5 km
                                                String title = (mood.getUsername() != null)
                                                        ? mood.getUsername() + "'s Mood"
                                                        : "Friend Mood";
                                                String snippet = (mood.getMoodState() != null)
                                                        ? mood.getMoodState().name()
                                                        : "Unknown";
                                                MarkerOptions markerOptions = new MarkerOptions()
                                                        .position(moodLocation)
                                                        .title(title)
                                                        .snippet(snippet)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                mMap.addMarker(markerOptions);
                                            }
                                        }
                                    }
                                },
                                error -> Log.e(TAG, "Error fetching moods for friend " + friend + ": " + error.getMessage())
                        );
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching friend list: " + e.getMessage()));
    }
}
