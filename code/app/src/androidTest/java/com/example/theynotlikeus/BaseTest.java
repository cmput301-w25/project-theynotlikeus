package com.example.theynotlikeus;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;

/* BaseTest.java is used by
 * HomeMyMoodsFragTest.java
 */
// Used for initializing the intent.
public class BaseTest {
    protected static Intent testIntent;
    private static String testUsername = "Username";

    /* How to putExtra data using ActivityScenarioRule from: https://stackoverflow.com/questions/54179560/how-to-putextra-data-using-newest-activityscenariorule-activityscenarioespress
     * Authored by: Jose Leles
     * Taken by: Ercel Angeles
     * Taken on: March 8, 2025
     */
    @BeforeClass
    public static void setUpBase() {
        testIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", testUsername);
        testIntent.putExtras(bundle);

        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8089;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }
}
