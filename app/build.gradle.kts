android {
    namespace = "com.example.tic_pv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tic_pv"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //Google Play Services
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    //Dependencias de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-messaging")

    //Libreria para redondear imágenes
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Libreria para hacer zoom en las imágenes
    implementation ("com.jsibbold:zoomage:1.3.1")

    //Dependencias para recortar imágenes
    implementation("com.vanniktech:android-image-cropper:4.6.0")

    //SDK de cloudinary
    implementation ("com.cloudinary:cloudinary-android:3.0.2")

    //Libreria para justificar textos
    implementation ("com.codesgood:justifiedtextview:1.1.0")

    //Librería para generar archivo PDF
    implementation ("com.itextpdf:itext7-core:9.1.0")
    implementation ("com.itextpdf:layout:9.1.0")
    implementation ("com.itextpdf:kernel:9.1.0")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.exifinterface:exifinterface:1.3.3")
    implementation ("com.github.bumptech.glide:glide:4.15.0")
    implementation(libs.scenecore)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0") // Para el procesamiento de anotaciones

    //Dependencias de CameraX
    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")
    implementation("androidx.camera:camera-extensions:1.4.0")

    //Dependencias de Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
//    implementation(libs.play.services.maps)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dependencia para Shimmer Effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
}

plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
}