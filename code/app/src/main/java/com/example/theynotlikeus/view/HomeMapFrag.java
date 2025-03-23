package com.example.theynotlikeus.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeMapFrag extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MyMoodsMapFrag";
    private GoogleMap mMap;
    private MoodController moodController;
    private String currentUser;
    // We'll store a representative location (e.g., the first marker) as the current location.
    private LatLng currentLocation;

    public HomeMapFrag() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodController = new MoodController();
        // Retrieve current user's identifier from the hosting Activity.
        currentUser = requireActivity().getIntent().getStringExtra("username");
        if (currentUser == null || currentUser.isEmpty()) {
            Log.e(TAG, "Username extra is missing! Using defaultUser.");
            currentUser = "defaultUser";
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate our layout.
        return inflater.inflate(R.layout.fragment_home_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Setup the map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapUserFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Setup the button that takes the user to the community map.
        ImageButton btnCommunity = view.findViewById(R.id.button_toCommunityMap);
        btnCommunity.setOnClickListener(v -> {
            if (currentLocation != null) {
                // Pass current location to CommunityMapActivity.
                Intent intent = new Intent(getActivity(), CommunityMapActivity.class);
                intent.putExtra("latitude", currentLocation.latitude);
                intent.putExtra("longitude", currentLocation.longitude);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Current location not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        loadUserMoodMarkers();
    }

    /**
     * Loads the current user's mood events from the database and adds markers.
     */
    private void loadUserMoodMarkers() {
        moodController.getMoodsByUser(currentUser, moods -> {
            if (moods == null || moods.isEmpty()) {
                Log.d(TAG, "No mood events found for user: " + currentUser);
                return;
            }
            LatLng firstLocation = null;
            for (Mood mood : moods) {
                if (mood.getLatitude() != null && mood.getLongitude() != null) {
                    LatLng loc = new LatLng(mood.getLatitude(), mood.getLongitude());
                    if (firstLocation == null) {
                        firstLocation = loc;
                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(mood.getMoodState() != null ? mood.getMoodState().name() : "My Mood"));
                }
            }
            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
                currentLocation = firstLocation;
            }
        }, error -> Log.e(TAG, "Error fetching moods: " + error.getMessage()));
    }
}
