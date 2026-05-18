# SpotShare - Discover Hidden Gems

SpotShare is a location-based social platform for discovering and sharing local "hidden spots" like cozy cafes, street art, or breathtaking viewpoints.

## Features
- **Authentication**: Secure login/registration via Firebase.
- **Interactive Map**: View spots around you with custom markers and category filtering.
- **Discovery**: Browse a curated list of nearby spots with ratings and distances.
- **Contribute**: Share your own finds by taking photos and tagging locations.
- **Spot Details**: Deep dive into spot info, reviews, and get directions via Google Maps.
- **Profile**: Manage your contributions and saved locations.

## Tech Stack
- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room (Local caching)
- **Backend**: Firebase (Auth, Firestore, Storage)
- **Networking**: Retrofit + OkHttp
- **Maps**: Google Maps Compose SDK
- **Image Loading**: Coil
- **Camera**: CameraX

## Setup Instructions

1.  **Firebase Setup**:
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android App with package name `com.spotshare`.
    - Download `google-services.json` and place it in the `app/` directory.
    - Enable **Email/Password** Authentication.
    - Create a **Firestore** database in test mode.
    - Enable **Firebase Storage**.

2.  **Google Maps API**:
    - Get an API Key from the [Google Cloud Console](https://console.cloud.google.com/).
    - Enable "Maps SDK for Android".
    - Replace `YOUR_API_KEY_HERE` in `AndroidManifest.xml` with your actual key.

3.  **Build**:
    - Open the project in Android Studio (Ladybug or newer recommended).
    - Sync Gradle.
    - Run the app on an emulator or physical device with Google Play Services.

## Firestore Structure
- `users/{userId}`: User profiles and saved spot IDs.
- `spots/{spotId}`: Spot details, location (lat/lng), and image URLs.
- `reviews/{reviewId}`: User reviews for specific spots.

## License
This project is for demonstration purposes.
