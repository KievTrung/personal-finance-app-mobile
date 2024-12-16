plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.personalfinance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.personalfinance"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    testImplementation("androidx.test:core:1.6.1")
    
    implementation("androidx.core:core-ktx:1.15.0")

    implementation("com.aldoapps:autoformatedittext:0.9.3")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("androidx.work:work-rxjava3:2.10.0")
    implementation("androidx.work:work-runtime:2.10.0")
    implementation("androidx.work:work-testing:2.10.0")

    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1")
}