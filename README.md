# 📰 NewsNow - Android News Application

NewsNow is a modern Android news application built using **Kotlin** and **Jetpack Compose**. The app fetches the latest news from [NewsAPI.org](https://newsapi.org/docs) and presents articles in a clean, responsive, and category-based user interface.

The application provides news in two major sections:

1. **Top Headlines**
2. **General News**

It also supports keyword-based search, bookmarking, authentication, theme customization, and full article reading using Android WebView.

---

## ✨ Features

- 📰 Browse latest news by category
- 🔥 View top headlines
- 🌍 Read general news articles
- 🔍 Search news using keywords
- 🔖 Bookmark favorite articles
- 🗄️ Store bookmarks locally using Room Database and SQLite
- 🌐 Read full news articles inside Android WebView
- 🎨 Material Theme support
- 🔤 Google Fonts support such as Merriweather
- 🔐 User authentication with register and login
- 💾 Shared Preferences for storing recent search queries
- ⚙️ Dependency Injection using Dagger Hilt
- 🧱 Clean architecture with Repository and ViewModel layers
- 🖼️ Image loading using Coil
- 🌐 API calls using Retrofit

---

## 🛠️ Tech Stack

| Technology | Usage |
|----------|-------|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern declarative UI |
| Material 3 | UI components and theming |
| Retrofit | API communication |
| Gson Converter | JSON parsing |
| NewsAPI.org | News data provider |
| Room Database | Local bookmark storage |
| SQLite | Local database support |
| Coil | Image loading |
| Dagger Hilt | Dependency Injection |
| Shared Preferences | Store search history / user data |
| Android WebView | Full article reading |
| Navigation Compose | Screen navigation |

---

## 📱 App Modules / Core Screens

- Login Screen
- Register Screen
- Home Screen
- Top Headlines Section
- General News Section
- Category News Screen
- Search News Screen
- Bookmark Screen
- WebView News Detail Screen
- Theme-enabled UI Screens

---

### Architecture Flow

```text
Composable UI
    ↓
ViewModel
    ↓
Repository
    ↓
Retrofit API / Room Database / Shared Preferences
    ↓
Data returned to UI as state
```

---

## 🚀 Getting Started

Follow the steps below to run the project locally.

---

## ✅ Prerequisites

Make sure you have the following installed:

- Android Studio
- JDK 17 or above
- Kotlin support
- Gradle
- NewsAPI API key

Get your API key from:

```text
https://newsapi.org/
```

---

## 📥 Clone the Repository

```bash
git clone https://github.com/your-username/newsnow.git
```

```bash
cd newsnow
```

---

## 🔑 API Key Setup

This project uses `secrets-gradle-plugin` to manage API keys securely.

Create a file named:

```text
local.properties
```

Add your NewsAPI key:

```properties
NEWS_API_KEY=your_news_api_key_here
```

> Do not commit your API key to GitHub.

---

## ⚙️ Gradle Configuration

### Project-level `build.gradle`

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false

    id("com.google.dagger.hilt.android") version "2.59.2" apply false
}
```

---

### App-level `build.gradle`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("com.google.dagger.hilt.android")
}
```

---

### Dependencies

```kotlin
dependencies {
    // Jetpack Compose & Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Retrofit (Networking)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room (Local Database)
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Material 3 (UI)
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended")

    // Dagger Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
}
```

---

## ▶️ Run the Application

1. Open the project in Android Studio.
2. Sync Gradle files.
3. Add your NewsAPI key in `local.properties`.
4. Connect an Android device or start an emulator.
5. Click **Run**.

Or run from terminal:

```bash
./gradlew assembleDebug
```

For Windows:

```bash
gradlew.bat assembleDebug
```

---

## 🌐 News API Integration

The app fetches news data from:

```text
https://newsapi.org/
```

Main API sections used:

```text
/top-headlines
/everything
```

Example API request:

```text
https://newsapi.org/v2/top-headlines?country=us&apiKey=YOUR_API_KEY
```

Example search request:

```text
https://newsapi.org/v2/everything?q=android&apiKey=YOUR_API_KEY
```

---

## 🔎 Search Functionality

NewsNow provides a search bar where users can search for news using keywords.

Examples:

```text
Technology
Sports
Business
Health
AI
Android
```

Search queries can be stored using Shared Preferences to improve user experience.

---

## 🔖 Bookmark Feature

Users can bookmark their favorite news articles.

Bookmarked articles are stored locally using:

- Room Database
- SQLite

This allows users to access saved articles even after closing the app.

---

## 🌐 WebView Article Reading

When a user selects a news article, the full article opens inside an Android WebView.

This provides an in-app reading experience without redirecting the user to an external browser.

---

## 🎨 Theme and Fonts

The app uses Material Theme for consistent UI styling.

It also supports custom fonts such as:

```text
Merriweather
```

Theme support includes:

- Light theme
- Dark theme
- Material 3 styling
- Typography customization
- Reusable Compose components

---

## 🔐 Authentication

NewsNow includes basic user authentication features:

- User Registration
- User Login
- Local session handling
- Shared Preferences-based user state management

---

## 💉 Dependency Injection with Hilt

Dagger Hilt is used to provide centralized dependencies such as:

- Retrofit instance
- API service
- Room database
- DAO
- Repositories
- Shared Preferences manager

Example dependency flow:

```text
Hilt Module
    ↓
Repository
    ↓
ViewModel
    ↓
Composable UI
```

---

## 🧪 Build Commands

Clean project:

```bash
./gradlew clean
```

Build debug APK:

```bash
./gradlew assembleDebug
```

Build release APK:

```bash
./gradlew assembleRelease
```

Run tests:

```bash
./gradlew test
```

---

## 📸 Screenshots

Add your screenshots here:

```markdown
screenshots/home.png
screenshots/search.png
screenshots/bookmarks.png
screenshots/webview.png
```

---

## 🔗 Useful Links

- [NewsAPI Documentation](https://newsapi.org/docs)
- [Jetpack Compose Documentation](https://developer.android.com/compose)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Dagger Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Coil Documentation](https://coil-kt.github.io/coil/)
- [Material Design 3](https://m3.material.io/)

---


## 📌 Git Commands

Initialize Git:

```bash
git init
```

Add remote repository:

```bash
git remote add origin https://github.com/your-username/newsnow.git
```

Add files:

```bash
git add .
```

Commit changes:

```bash
git commit -m "Initial commit"
```

Push code:

```bash
git branch -M main
git push -u origin main
```

Force push, only if required:

```bash
git push --force-with-lease origin main
```

---

## 📄 License

This project is licensed under the MIT License.

```text
MIT License
```

---

## 👨‍💻 Author

**Ranjit Raj Nahak**

---

## 📌 Project Summary

NewsNow is a feature-rich Android news application built with Kotlin and Jetpack Compose. It provides real-time news updates, category-based browsing, keyword search, bookmarking, WebView article reading, user authentication, local storage, Material theming, and dependency injection using Dagger Hilt.
