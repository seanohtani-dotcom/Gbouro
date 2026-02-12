# Firebase Setup Instructions

## 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "Anime Gallery" (or your preferred name)
4. Follow the setup wizard

## 2. Add Android App to Firebase

1. In Firebase Console, click "Add app" → Android
2. Enter package name: `com.animegallery.app`
3. Download `google-services.json`
4. **Replace** the placeholder `app/google-services.json` with your downloaded file

## 3. Enable Firebase Authentication

1. In Firebase Console, go to **Authentication**
2. Click "Get Started"
3. Enable **Email/Password** sign-in method
4. Click "Save"

## 4. Enable Firebase Firestore (Optional - for cloud sync)

1. In Firebase Console, go to **Firestore Database**
2. Click "Create database"
3. Choose "Start in test mode" (for development)
4. Select a location close to your users
5. Click "Enable"

### Firestore Security Rules (for production):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own favorites
    match /users/{userId}/favorites/{favoriteId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Users can read their own profile
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 5. Build and Run

After replacing `google-services.json`:

```bash
./gradlew assembleDebug
```

## Features with Firebase

### Without Account (Offline Mode)
- Browse images from Gelbooru API
- Search by tags
- Save favorites locally (Room database)
- Download images
- All features work offline

### With Account (Online + Offline)
- All offline features
- **Sync favorites across devices** (Firestore)
- **Backup favorites to cloud**
- Access favorites from any device
- Seamless online/offline sync

## Testing

### Test Accounts
Create test accounts in Firebase Console → Authentication → Users

### Offline Testing
1. Sign in with account
2. Add favorites
3. Turn off internet
4. Favorites still work (local Room database)
5. Turn on internet
6. Favorites sync to cloud automatically

## Security

- Authentication required for cloud sync
- Local data always available offline
- Firebase Security Rules protect user data
- Each user can only access their own data

## Troubleshooting

### Build Error: "google-services.json not found"
- Make sure you downloaded and replaced the file
- File must be at: `app/google-services.json`

### Authentication Error
- Check Firebase Console → Authentication is enabled
- Verify Email/Password provider is enabled
- Check internet connection

### Sync Not Working
- Verify Firestore is enabled in Firebase Console
- Check Firestore Security Rules
- Ensure user is signed in
