# Voice Notes Android Application

A simple, clean Android voice notes app using Material 3 Design with offline-first architecture, multilingual transcription, and cloud sync capabilities.

## Features

- 🎤 **Easy Voice Recording**: One-tap voice recording with real-time waveform visualization
- 📝 **Auto Transcription**: Automatic speech-to-text conversion in multiple languages
- 🔍 **Powerful Search**: Full-text search across note titles and transcripts
- ☁️ **Cloud Sync**: Secure synchronization across multiple Android devices
- 🌐 **Multilingual Support**: Support for multiple languages for recording and transcription
- 🌓 **Dark/Light Themes**: Beautiful Material 3 design with theme support
- 📱 **Offline-First**: Fully functional offline with automatic sync when online
- 🔒 **Secure**: Firebase authentication and encrypted cloud storage

## Architecture

The app follows **Clean Architecture** with clear separation of concerns:

### Layers

- **Presentation Layer**: Jetpack Compose UI screens, ViewModels
- **Domain Layer**: Use cases, repository interfaces, and business logic
- **Data Layer**: Local Room database, Firebase cloud services, audio processing
- **Infrastructure**: Hilt dependency injection, WorkManager for background sync

### Tech Stack

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Latest Material Design system
- **Room** - Local database for offline-first capability
- **Hilt** - Dependency injection
- **Firebase** - Authentication, Firestore database, Cloud Storage
- **WorkManager** - Background synchronization
- **ML Kit** - On-device language identification
- **MediaRecorder API** - Audio recording
- **MediaPlayer API** - Audio playback

## Project Structure

```
app/src/main/kotlin/com/voicenotes/app/
├── VoiceNotesApplication.kt          # App entry point
├── MainActivity.kt                   # Main activity
├── data/
│   ├── local/
│   │   ├── database/                 # Room database
│   │   ├── dao/                      # Data access objects
│   │   ├── entity/                   # Database entities
│   │   └── preferences/              # DataStore preferences
│   ├── audio/                        # Audio recording/playback
│   ├── cloud/                        # Firebase services
│   ├── transcription/                # Speech-to-text services
│   ├── sync/                         # Cloud synchronization
│   └── repository/                   # Repository implementations
├── domain/
│   ├── model/                        # Domain models
│   ├── repository/                   # Repository interfaces
│   └── usecase/                      # Business logic use cases
├── di/                               # Dependency injection modules
├── navigation/                       # Navigation configuration
├── presentation/
│   ├── screens/                      # Compose screens
│   └── viewmodel/                    # ViewModels for state management
└── utils/                            # Utility functions
```

## Installation & Setup

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+ (minimum)
- Java 17
- Gradle 8.2.0+

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/skcode98/VoiceNotes.git
   cd VoiceNotes
   ```

2. Open in Android Studio

3. Set up Firebase:
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Download `google-services.json` and place in `app/` directory
   - Enable Authentication (Email/Password)
   - Enable Firestore Database
   - Enable Cloud Storage

4. Build and run:
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## Building APK

### Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

## Usage

1. **Launch the app**: Tap the VoiceNotes icon
2. **Record a note**: Tap the microphone FAB
3. **Stop recording**: Press the Stop button
4. **Save**: Edit title/transcript and tap Save
5. **Search**: Use the search bar on the home screen
6. **Sync**: Enable auto-sync in settings for cloud backup
7. **Playback**: Tap any note to play the recording

## API Configuration

### Google Cloud Speech-to-Text (Optional)

For production transcription, configure Google Cloud Speech API:

1. Create Google Cloud project
2. Enable Speech-to-Text API
3. Create service account and download JSON key
4. Add to `local.properties`:
   ```
   GOOGLE_CLOUD_SPEECH_API_KEY=your_api_key
   ```

## Database Schema

### voice_notes table
```sql
CREATE TABLE voice_notes (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    transcript TEXT NOT NULL,
    audio_file_path TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    duration_seconds INTEGER NOT NULL,
    language TEXT NOT NULL DEFAULT 'en',
    is_synced BOOLEAN NOT NULL DEFAULT 0,
    sync_timestamp TIMESTAMP,
    cloud_id TEXT
);
```

## Permissions

The app requires:
- `RECORD_AUDIO` - For voice recording
- `INTERNET` - For cloud sync
- `ACCESS_NETWORK_STATE` - For connectivity checks
- `READ_EXTERNAL_STORAGE` - For importing audio files
- `WRITE_EXTERNAL_STORAGE` - For saving recordings

## Security

- **Authentication**: Firebase Authentication with email/password
- **Database**: Firestore security rules restrict access to user's own data
- **Storage**: Firebase Storage encryption at rest and in transit
- **Local Database**: Encrypted with Android Keystore

## Performance Optimization

- Lazy loading of note list
- Pagination for large datasets
- Background sync with WorkManager
- Efficient audio compression (AAC codec)
- Proguard/R8 code shrinking in release builds

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## CI/CD Pipeline

The project includes GitHub Actions workflow for:
- Automatic builds on push/PR
- Unit test execution
- Code analysis with Lint
- APK generation
- Artifact upload

## Troubleshooting

### Recording not working
- Check microphone permissions in Android settings
- Verify app has RECORD_AUDIO permission granted
- Restart the device

### Sync not working
- Check internet connection
- Verify Firebase credentials in google-services.json
- Check app permissions for INTERNET

### Transcription not available
- Ensure ML Kit is properly initialized
- For cloud transcription, verify Google Cloud credentials

## License

Apache License 2.0 - See LICENSE file for details

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For issues and feature requests, please use the GitHub Issues page.

## Roadmap

- [ ] Real-time transcription with Google Cloud Speech API
- [ ] Note categories/tags
- [ ] Note sharing with other users
- [ ] Advanced search filters
- [ ] Export notes as PDF
- [ ] Voice command support
- [ ] Widget for quick recording
- [ ] Backup to cloud storage services (Google Drive, OneDrive)

---

**Made with ❤️ for voice note enthusiasts**
