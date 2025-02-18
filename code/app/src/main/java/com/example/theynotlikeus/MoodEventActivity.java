package com.example.theynotlikeus;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

//public class MoodEventFrag extends Fragment {
//    public MoodEventFrag() {
//        // Constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        return inflater.inflate(R.layout.fragment_mood_event, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        BottomNavigationView bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView);
//        bottomNavigation.setVisibility(View.GONE);
//
//        requireActivity().getLifecycle().addObserver(new DefaultLifecycleObserver() {
//            @Override
//            public void onDestroy(@NonNull LifecycleOwner owner) {
//                bottomNavigation.setVisibility(View.VISIBLE);
//            }
//        });
//        Button saveButton = view.findViewById(R.id.save_button);
//        saveButton.setOnClickListener(v -> {
//            // TODO: Handle saving logic
//            Navigation.findNavController(view).navigateUp();
//        });
//
//        view.findViewById(R.id.back_button).setOnClickListener(v ->
//                Navigation.findNavController(view).navigateUp());
//    }
//}
public class MoodEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mood_event);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            // TODO: Handle saving logic
            finish(); // Closes activity and returns to previous screen
        });

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
}

