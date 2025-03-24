package com.example.theynotlikeus.view;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CommunityMapActivity";
    private GoogleMap mMap;
    private MoodController moodController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current user's identifier (needed for fetching friend list)
    private String currentUser;
    // Latest location derived from current user's mood events.
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_map);

        // Retrieve current user's identifier from intent extras.
        currentUser = getIntent().getStringExtra("username");
        if (currentUser == null || currentUser.isEmpty()) {
            currentUser = "defaultUser";
        }

        moodController = new MoodController();

        // Set up the back button.
        ImageButton backButton = findViewById(R.id.imageButton_CommunityMapActivity_back);
        backButton.setOnClickListener(v -> finish());

        // Instead of getting location from extras, we now fetch the current user's latest mood with geolocation.
        getCurrentUserLatestLocation();
    }

    /**
     * Fetches the current user's latest mood event that has geolocation enabled,
     * and sets that as the currentLocation.
     */
    private void getCurrentUserLatestLocation() {
        moodController.getMoodsByUser(currentUser, moods -> {
            List<Mood> geoMoods = new ArrayList<>();
            if (moods != null) {
                for (Mood mood : moods) {
                    if (mood.getLatitude() != null && mood.getLongitude() != null && mood.getDateTime() != null) {
                        geoMoods.add(mood);
                    }
                }
            }
            if (geoMoods.isEmpty()) {
                Toast.makeText(this, "No valid location found from your moods.", Toast.LENGTH_SHORT).show();
                // Use a default location (e.g., 0,0) as a fallback.
                currentLocation = new LatLng(0, 0);
            } else {
                // Sort in descending order by date (most recent first).
                geoMoods.sort((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()));
                Mood latestMood = geoMoods.get(0);
                currentLocation = new LatLng(latestMood.getLatitude(), latestMood.getLongitude());
            }
            // Now initialize the map.
            initMap();
        }, error -> {
            Log.e(TAG, "Error fetching current user's moods: " + error.getMessage());
            Toast.makeText(this, "Error loading your location.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Initializes the map by retrieving the SupportMapFragment and setting the OnMapReadyCallback.
     */
    private void initMap() {
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

        // Center the map on the current user's latest location.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

        // Load friends' mood markers within a 5 km radius.
        loadFriendsMoodMarkers(currentLocation);
    }

    /**
     * Loads the friend list from the "follow" collection.
     * For each friend, fetches their mood events using the MoodController.
     * For each friend, all mood events are processed (not limited), and only those that are public are added.
     * If an event is within 5 km of the current user's latest location, a marker is added.
     * If multiple moods share the same coordinates, subsequent markers are offset slightly.
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

                    if (friendList.isEmpty()) {
                        Toast.makeText(CommunityMapActivity.this, "No friends found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Use AtomicIntegers to track markers added and queries processed.
                    final java.util.concurrent.atomic.AtomicInteger markerCount = new java.util.concurrent.atomic.AtomicInteger(0);
                    final java.util.concurrent.atomic.AtomicInteger queriesProcessed = new java.util.concurrent.atomic.AtomicInteger(0);

                    // For each friend, fetch their moods.
                    for (String friend : friendList) {
                        moodController.getMoodsByUser(friend,
                                moods -> {
                                    if (moods == null || moods.isEmpty()) {
                                        Log.d(TAG, "No moods for friend: " + friend);
                                    } else {
                                        // Process all moods for this friend.
                                        // Create a local map to track markers at the same coordinate.
                                        Map<String, Integer> locationCount = new HashMap<>();
                                        Log.d(TAG, "Friend " + friend + " has " + moods.size() + " mood(s).");
                                        for (Mood mood : moods) {
                                            // Only process the mood if it is public.
                                            if (mood.isPublic()) {
                                                if (mood.getLatitude() != null && mood.getLongitude() != null) {
                                                    double lat = mood.getLatitude();
                                                    double lng = mood.getLongitude();
                                                    String key = lat + "," + lng;
                                                    int count = locationCount.containsKey(key) ? locationCount.get(key) : 0;
                                                    locationCount.put(key, count + 1);
                                                    // Offset subsequent markers if they share the same coordinate.
                                                    if (count > 0) {
                                                        lat += count * 0.00005;
                                                        lng += count * 0.00005;
                                                    }
                                                    LatLng moodLocation = new LatLng(lat, lng);
                                                    // Calculate the distance (in meters) between currentLocation and moodLocation.
                                                    float[] results = new float[1];
                                                    Location.distanceBetween(
                                                            currentLocation.latitude, currentLocation.longitude,
                                                            moodLocation.latitude, moodLocation.longitude, results);
                                                    Log.d(TAG, "Distance for mood from friend " + friend + ": " + results[0] + " meters.");
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
                                                        markerCount.incrementAndGet();
                                                        Log.d(TAG, "Added marker for friend " + friend + " at " + moodLocation);
                                                    } else {
                                                        Log.d(TAG, "Mood event for friend " + friend + " is outside 5 km.");
                                                    }
                                                } else {
                                                    Log.d(TAG, "Mood for friend " + friend + " does not have valid geo data.");
                                                }
                                            } else {
                                                Log.d(TAG, "Skipping mood for friend " + friend + " because it's not public.");
                                            }
                                        }
                                    }
                                    // Increment the processed friend counter.
                                    int processed = queriesProcessed.incrementAndGet();
                                    if (processed == friendList.size() && markerCount.get() == 0) {
                                        Toast.makeText(CommunityMapActivity.this, "No friend moods within 5 km.", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error fetching moods for friend " + friend + ": " + error.getMessage());
                                    int processed = queriesProcessed.incrementAndGet();
                                    if (processed == friendList.size() && markerCount.get() == 0) {
                                        Toast.makeText(CommunityMapActivity.this, "No friend moods within 5 km.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching friend list: " + e.getMessage()));
    }
}
