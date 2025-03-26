package com.example.theynotlikeus;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.theynotlikeus.view.MainActivity;
import com.example.theynotlikeus.view.RecyclerViewItemCountAssertion;
import com.example.theynotlikeus.view.SearchUserActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchUserActivityTest {

    @Before
    public void disableAnimations() {
        // Disable animations for Espresso tests
        System.setProperty("ro.zygote.disable.translucent", "true");
        System.setProperty("ro.zygote.disable.surfaceflinger", "true");
    }

    /**
     * Scenario is in SearchUserActivity
     */
    @Rule
    public ActivityScenarioRule<SearchUserActivity> scenario = new ActivityScenarioRule<>(SearchUserActivity.class);

    /**
     * Set up Firestore locally
     */
    @BeforeClass
    public static void setup() {
        // Specific address for emulated device to access our localhost.
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    /**
     * Helper method to add a user to the database.
     */
    private void addUserToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testUser");
        user.put("password", "123");
        // Write the user data to the "users" collection with document id "nour".
        db.collection("users").document("testUser").set(user);
    }

    @Test
    public void testSearchFunctionality() throws InterruptedException {
        // Upload a user to database to search
        addUserToDatabase();

//        onView(withId(R.id.searchBar_SearchUserActivity_searchBar))
//                .perform(click());
//
//        Thread.sleep(2000);
//        // Wait for SearchView to be displayed
//        onView(withId(R.id.searchView_SearchUserActivity_searchView))
//                .check(matches(isDisplayed()));
//
//        onView(withId(R.id.searchView_SearchUserActivity_searchView))
//                .perform(typeText("testUser"), closeSoftKeyboard());
//
//        // Simulate pressing the "Enter" key (IME action)
//        onView(withId(R.id.searchView_SearchUserActivity_searchView))
//                .perform(ViewActions.pressImeActionButton());

        // Verify that a user containing "john" appears in the RecyclerView
        //onView(withText("testUser"))
                //.check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Thread.sleep(2000);

        onView(withId(R.id.recyclerview_SearchUserActivity))  // Replace with your RecyclerView's ID
                .check(matches(hasDescendant(withText("testUser"))));
    }

    /**
     *  Tear down: Clear all documents from the emulator after each test */
    @After
    public void tearDown() {
        String projectId = "theynotlikeus-6a9f1";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8089/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
