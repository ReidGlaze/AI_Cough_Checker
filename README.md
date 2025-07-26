# AI Cough Checker

An AI-powered mobile application that analyzes cough sounds to provide educational health insights using Google's Gemini 2.5 Flash model.

## Features

- 🎤 Record and analyze cough sounds
- 🤖 AI-powered analysis using Gemini 2.5 Flash
- 📊 Track analysis history
- 🔒 Privacy-focused (no audio storage by default)
- 📱 Native iOS and Android apps

## Tech Stack

- **iOS**: Swift, SwiftUI
- **Android**: Kotlin, Jetpack Compose
- **Backend**: TypeScript, Firebase Functions, Vertex AI
- **AI Model**: Gemini 2.5 Flash via Vertex AI

## Setup Instructions

### Prerequisites

- Xcode 14+ (for iOS)
- Android Studio (for Android)
- Node.js 16+
- Firebase CLI
- Google Cloud SDK

### 1. Clone the Repository

```bash
git clone https://github.com/ReidGlaze/AI_Cough_Checker.git
cd AI_Cough_Checker
```

### 2. Firebase Setup

You'll need to add your own Firebase configuration files (not included for security):

#### iOS
1. Create a Firebase project at https://console.firebase.google.com
2. Add an iOS app to your project
3. Download `GoogleService-Info.plist`
4. Place it in `cough/cough/GoogleService-Info.plist`

#### Android
1. Add an Android app to your Firebase project
2. Download `google-services.json`
3. Place it in `cough_android/app/google-services.json`

### 3. Backend Setup

```bash
cd functions
npm install
```

Create a `.env` file in the functions directory:
```
GCLOUD_PROJECT=your-project-id
```

### 4. Deploy Functions

```bash
firebase deploy --only functions
```

### 5. iOS Setup

1. Open `cough/cough.xcodeproj` in Xcode
2. Update the bundle identifier and team
3. Build and run

### 6. Android Setup

1. Open `cough_android` in Android Studio
2. Create a keystore for release builds (see Android docs)
3. Update `app/build.gradle.kts` with your keystore info
4. Build and run

## Important Security Notes

- Never commit `GoogleService-Info.plist`, `google-services.json`, or keystore files
- Keep your Firebase project credentials secure
- The `.gitignore` file is configured to exclude sensitive files

## Architecture

- **Modular Backend**: Easy to swap AI models as they improve
- **Native Development**: Optimal performance and platform integration
- **Privacy First**: Audio processed in memory, not stored by default

## License

MIT License - see [LICENSE](LICENSE) file for details

## Disclaimer

This app provides educational insights only and is not intended as medical advice. Always consult healthcare professionals for medical concerns.