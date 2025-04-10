plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlinx-serialization")
    //id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.mealgenie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mealgenie"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.navigation:navigation-compose:2.8.9")

    //Material2
    implementation ("androidx.compose.material:material")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.material3)
    debugImplementation ("androidx.compose.ui:ui-tooling")
    implementation ("androidx.compose.material:material-icons-core")
    implementation ("androidx.compose.material:material-icons-extended")

    //Glide Compose
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    //Ktor
    val ktorVersion = "3.1.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    //Livedata en Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")

    //Room
//    val room_version = "2.6.1"
//    implementation("androidx.room:room-runtime:$room_version")
//    ksp("androidx.room:room-compiler:$room_version")
//    //Room para corrutinas
//    implementation("androidx.room:room-ktx:$room_version")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}