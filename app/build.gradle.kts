plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    }

android {
    namespace = "com.example.sportkardi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sportkardi"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(platform("com.android.support:multidex:1.0.3"))
    implementation(libs.firebase.auth)
    implementation("com.ramotion.foldingcell:folding-cell:1.2.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.applandeo:material-calendar-view:1.9.2")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.getkeepsafe.taptargetview:taptargetview:1.13.3")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}