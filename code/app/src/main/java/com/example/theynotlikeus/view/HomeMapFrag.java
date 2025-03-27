package com.example.theynotlikeus.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.theynotlikeus.R;
import com.example.theynotlikeus.controller.MoodController;
import com.example.theynotlikeus.model.Mood;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

/**
 * A fragment that displays a map showing mood markers based on the current user's mood events.
 * The user’s location is retrieved, and the map is updated with markers representing their mood events.
 * The fragment allows the user to navigate to a community map by clicking a button.
 *
 */
public class HomeMapFrag extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeMapFrag";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private MoodController moodController;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentUser = "currentUserID";
    private LatLng currentLocation;

    public HomeMapFrag() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodController = new MoodController();
        // Retrieve current user from the hosting Activity's intent if available.
        String userFromIntent = requireActivity().getIntent().getStringExtra("username");
        if (userFromIntent != null && !userFromIntent.isEmpty()) {
            currentUser = userFromIntent;
        } else {
            Log.e(TAG, "Username extra is missing; using default currentUserID.");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
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

        // Check if location permissions are granted.
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted.
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions granted; attempt to get the last known location.
            getLastKnownLocation();
        }

        // Setup the map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapUserFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Setup the community map button.
        ImageButton btnCommunity = view.findViewById(R.id.button_MapUserFrag_toCommunityMap);
        btnCommunity.setOnClickListener(v -> {
            if (currentLocation != null) {
                Intent intent = new Intent(getActivity(), CommunityMapActivity.class);
                intent.putExtra("latitude", currentLocation.latitude);
                intent.putExtra("longitude", currentLocation.longitude);
                intent.putExtra("username", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Current location not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Attempts to get the last known location and update currentLocation.
     */
    private void getLastKnownLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // If map is already loaded, move the camera.
                    if (mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));
                    }
                } else {
                    Log.e(TAG, "Last location is null.");
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Load user's mood markers.
        loadUserMoodMarkers();
    }

    /**
     * Loads the current user's mood events from Firestore and adds markers to the map.
     * If multiple moods share the same location, subsequent markers are offset slightly.
     */
    private void loadUserMoodMarkers() {
        moodController.getMoodsByUser(currentUser, moods -> {
            // Ensure UI thread for UI updates
            requireActivity().runOnUiThread(() -> {
                mMap.clear(); // Clear any previously added markers.
                if (moods == null || moods.isEmpty()) {
                    Log.d(TAG, "No mood events found for user: " + currentUser);
                    Toast.makeText(getContext(), "No mood events found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLng firstLocation = null;
                Map<String, Integer> locationCount = new HashMap<>();
                int markersAdded = 0;
                for (Mood mood : moods) {
                    // Only add moods that are approved (pendingReview is false) and have valid location data.
                    if (mood.getLatitude() != null && mood.getLongitude() != null && !mood.isPendingReview()) {
                        double lat = mood.getLatitude();
                        double lng = mood.getLongitude();
                        String key = lat + "," + lng;
                        int count = locationCount.containsKey(key) ? locationCount.get(key) : 0;
                        locationCount.put(key, count + 1);
                        if (count > 0) {
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
                        markersAdded++;
                    }
                }
                if (firstLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
                    currentLocation = firstLocation;
                }
                if (markersAdded == 0) {
                    Toast.makeText(getContext(), "No approved mood events to display.", Toast.LENGTH_SHORT).show();
                }
            });
        }, error -> Log.e(TAG, "Error fetching moods for user " + currentUser + ": " + error.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted; try getting the last known location.
                getLastKnownLocation();
            } else {
                Toast.makeText(getContext(), "Location permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
