plugins {
    //alias(libs.plugins.android.application) // (Commented out as before)
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.theynotlikeus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.theynotlikeus"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BOM ensures consistent Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-firestore")

    // Glide to display photos
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // UI
    implementation("com.google.android.material:material:1.12.0")

    // App dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.espresso.core)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-storage:20.2.1")
    implementation(libs.play.services.location)

    // Google map
    implementation ("com.google.android.gms:play-services-maps:18.1.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")


    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.fragment:fragment-testing:1.5.7")
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.8")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")


    // Byte Buddy for runtime code generation (if needed)
    implementation("net.bytebuddy:byte-buddy:1.17.1")

    // Removed explicit inclusion of android.jar, which causes duplicate classes.
    // implementation(files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"))
}
