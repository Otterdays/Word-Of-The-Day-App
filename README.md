# 📚 Word of the Day

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue?logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple?logo=jetpackcompose)
![Gradle](https://img.shields.io/badge/Build-Gradle%209.6-blue?logo=gradle)
![API](https://img.shields.io/badge/API-26%2B-brightgreen)

*A beautiful Android app that delivers a new word every day to expand your vocabulary*

</div>

## ✨ Features

- 🎯 **Daily Word Delivery**: Get a curated word every day with comprehensive information
- 📱 **Modern UI**: Built with Jetpack Compose for a smooth, responsive experience
- 🎨 **Material Design 3**: Follows latest Android design guidelines with adaptive layouts
- 📖 **Rich Content**: Each word includes definition, pronunciation, examples, and etymology
- 🏗️ **Clean Architecture**: MVVM pattern with separation of concerns
- 🔄 **Offline Ready**: Designed to work seamlessly with local data persistence

## 🏗️ Architecture

This app follows modern Android development best practices with a clean, layered architecture:

```
┌─────────────────┐
│   UI Layer      │  ← Jetpack Compose Screens
├─────────────────┤
│ Presentation    │  ← ViewModels with UI State
├─────────────────┤
│   Domain        │  ← Business Logic (when needed)
├─────────────────┤
│    Data Layer   │  ← Repository Pattern
└─────────────────┘
```

### Key Components

- **Single Activity Architecture**: Modern navigation with Compose Navigation
- **MVVM Pattern**: ViewModels manage UI state and business logic
- **Repository Pattern**: Clean data layer abstraction
- **Coroutines**: Asynchronous operations handled efficiently
- **Serialization**: Kotlinx Serialization for data models

## 📱 Screenshots

*(Add screenshots here when the app is ready)*

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Hedgehog | 2023.1.1 or newer
- **JDK**: 21 or newer
- **Android SDK**: API level 26 (Android 8.0) minimum
- **Gradle**: 9.6

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Otterdays/Word-Of-The-Day-App.git
   cd Word-Of-The-Day-App
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

3. **Sync the project**
   - Android Studio will automatically sync the Gradle files
   - Wait for the build to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button (▶️) in Android Studio
   - Or use the command line:
     ```bash
     ./gradlew :app:installDebug
     ```

## 📂 Project Structure

```
Word-Of-The-Day-App/
├── app/
│   └── src/main/
│       ├── java/com/example/wordofday/
│       │   ├── data/
│       │   │   ├── model/          # Data models
│       │   │   └── repository/     # Data repositories
│       │   ├── ui/
│       │   │   ├── home/           # Main screen components
│       │   │   └── theme/          # Material 3 theming
│       │   └── MainActivity.kt     # Single entry point
│       └── res/                    # Android resources
├── DOCS/                           # Project documentation
├── build.gradle.kts               # Root build configuration
├── settings.gradle.kts            # Gradle settings
└── gradle.properties              # Gradle properties
```

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **AndroidX**: Core Android libraries
- **Material 3**: Design system and components

### Architecture & Libraries
- **Compose Navigation**: Screen navigation
- **ViewModel**: UI state management
- **Coroutines**: Asynchronous programming
- **Kotlinx Serialization**: JSON parsing
- **Compose BOM**: Aligned Compose dependencies

### Build Tools
- **Gradle 9.6**: Build automation
- **Kotlin DSL**: Build script configuration
- **Version Catalogs**: Centralized dependency management

## 📋 Requirements

- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 36 (Android 15)
- **Compile SDK**: API 36

## 🧪 Testing

The project includes JVM unit tests (for example, grade search-order behavior) and CI runs
unit tests + lint + debug assemble on pushes/PRs.

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📖 Documentation

Detailed project documentation is available in the `DOCS/` directory:

- **[ARCHITECTURE.md](DOCS/ARCHITECTURE.md)**: Technical architecture details
- **[ROADMAP.md](DOCS/ROADMAP.md)**: Development roadmap and feature planning
- **[STYLE_GUIDE.md](DOCS/STYLE_GUIDE.md)**: Coding standards and conventions
- **[AGENT_WORKMAP.md](DOCS/AGENT_WORKMAP.md)**: Fast "where to edit what" map (constants,
  routes, config, content pipeline, CI)
- **[CHANGELOG.md](DOCS/CHANGELOG.md)**: Version history and changes

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Guidelines

- Follow the existing code style and architecture patterns
- Write unit tests for new functionality
- Update documentation as needed
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Material Design 3](https://m3.material.io/)

---

<div align="center">

**Built with ❤️ using modern Android development practices**

Made with [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Kotlin](https://kotlinlang.org/)

</div>
