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

import java.util.List;

public class HomeMapFrag extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeMapFrag";
    private GoogleMap mMap;
    private MoodController moodController;

    // Optional parameters
    private String mParam1;
    private String mParam2;

    public HomeMapFrag() {
        // Required empty public constructor.
    }

    public static HomeMapFrag newInstance(String param1, String param2) {
        HomeMapFrag fragment = new HomeMapFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve any passed parameters.
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
        // Initialize the MoodController for fetching mood events.
        moodController = new MoodController();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (fragment_home_map.xml)
        return inflater.inflate(R.layout.fragment_home_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the SupportMapFragment and request notification when the map is ready.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.mapUserFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Callback when the map is ready to be used.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Optionally, enable zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Load mood events that have geolocation enabled.
        loadMoodMarkers();
    }

    /**
     * Fetch mood events, filter those with valid geolocation data, and add markers on the map.
     */
    private void loadMoodMarkers() {
        moodController.getAllMoods(moods -> {
            if (moods == null || moods.isEmpty()) {
                Log.d(TAG, "No mood events found.");
                return;
            }

            LatLng firstLocation = null;
            for (Mood mood : moods) {
                if (mood.getLatitude() != null && mood.getLongitude() != null) {
                    LatLng location = new LatLng(mood.getLatitude(), mood.getLongitude());
                    // Save the first valid location to center the camera later.
                    if (firstLocation == null) {
                        firstLocation = location;
                    }
                    String title = (mood.getUsername() != null)
                            ? mood.getUsername() + "'s Mood"
                            : "User Mood";
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
            // If we found at least one mood with location, move the camera to it.
            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f));
            }
        }, error -> Log.e(TAG, "Error fetching moods: " + error.getMessage()));
    }
}
