package com.example.theynotlikeus.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeMapFrag extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeMapFrag";
    private GoogleMap mMap;
    private MoodController moodController;
    // Replace this with your actual current user's identifier.
    private String currentUser = "currentUserID";

    public HomeMapFrag() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodController = new MoodController();
        // Optionally, retrieve the current user ID from the hosting Activity's intent.
        String userFromIntent = requireActivity().getIntent().getStringExtra("username");
        if (userFromIntent != null && !userFromIntent.isEmpty()) {
            currentUser = userFromIntent;
        } else {
            Log.e(TAG, "Username extra is missing; using default currentUserID.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        return inflater.inflate(R.layout.fragment_home_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the SupportMapFragment and request the map asynchronously.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapUserFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Called when the map is ready.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Load and display the current user's mood markers.
        loadUserMoodMarkers();
    }

    /**
     * Fetches the current user's mood events (with geolocation) and adds them as markers on the map.
     * If multiple moods share the same location, subsequent markers are offset slightly.
     */
    private void loadUserMoodMarkers() {
        moodController.getMoodsByUser(currentUser, moods -> {
            if (moods == null || moods.isEmpty()) {
                Log.d(TAG, "No mood events found for user: " + currentUser);
                return;
            }
            LatLng firstLocation = null;
            // Use a Map to track how many markers share the same location.
            Map<String, Integer> locationCount = new HashMap<>();
            for (Mood mood : moods) {
                if (mood.getLatitude() != null && mood.getLongitude() != null) {
                    double lat = mood.getLatitude();
                    double lng = mood.getLongitude();
                    // Create a key for this coordinate (you may round the values if necessary).
                    String key = lat + "," + lng;
                    int count = locationCount.containsKey(key) ? locationCount.get(key) : 0;
                    locationCount.put(key, count + 1);
                    // If this location already has one or more markers, offset the coordinates slightly.
                    if (count > 0) {
                        // Adjust by a small amount (e.g., 0.00005 degrees per duplicate).
                        // so you can see moods at the same location
                        lat += count * 0.00005;
                        lng += count * 0.00005;
                    }
                    LatLng location = new LatLng(lat, lng);
                    if (firstLocation == null) {
                        firstLocation = location;
                    }
                    String title = (mood.getUsername() != null)
                            ? mood.getUsername() + "'s Mood"
                            : "My Mood";
                    String snippet = (mood.getMoodState() != null)
                            ? mood.getMoodState().name()
                            : "Unknown";
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(markerOptions);
                }
            }
            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
            }
        }, error -> Log.e(TAG, "Error fetching moods for user " + currentUser + ": " + error.getMessage()));
    }
}
