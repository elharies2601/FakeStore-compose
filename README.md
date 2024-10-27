# FakeStore Project

Simple REST API project using https://fakestoreapi.com/
This Android project utilizes modern libraries and tools to deliver a highly functional, scalable, and maintainable application.

## Table of Contents
- [Libraries](#libraries)
  - [Core Libraries](#core-libraries)
  - [Jetpack Components](#jetpack-components)
  - [Dependency Injection](#dependency-injection)
  - [Networking](#networking)
  - [Asynchronous Programming](#asynchronous-programming)
  - [Image Loading](#image-loading)
  - [UI and Design](#ui-and-design)
  - [Testing](#testing)
- [Architecture](#architecture)

## Libraries

### Core Libraries
- **Core KTX** - Provides Kotlin extensions for Android (v1.10.1).
- **Kotlin Standard Library** - Kotlin language support (v2.0.0).

### Jetpack Components
- **Lifecycle Runtime KTX** - Enables lifecycle-aware components (v2.6.1).
- **Activity Compose** - Enables Jetpack Compose in Activities (v1.8.0).
- **Navigation Compose** - Integrates Jetpack Compose with Navigation (v2.7.6).
- **Paging Compose** - Supports pagination in Jetpack Compose (v3.2.1).
- **Room** - Provides a local database (v2.6.1).
- **DataStore Preferences** - For storing key-value pairs asynchronously (v1.1.1).

### Dependency Injection
- **Hilt** - Dependency injection framework powered by Dagger (v2.48).
- **Hilt Navigation Compose** - Integrates Hilt with Jetpack Compose’s Navigation (v1.1.0).

### Networking
- **Retrofit** - Type-safe HTTP client (v2.9.0).
- **OkHttp** - HTTP client for networking (v4.12.0).
    - Logging Interceptor - For logging network requests and responses.

### Asynchronous Programming
- **Kotlin Coroutines** - Concurrency design pattern for asynchronous tasks (v1.6.0).

### Image Loading
- **Coil Compose** - Image loading for Jetpack Compose (v2.5.0).
- **Landscapist** - Enhanced image loading library with animations and placeholders ([v2.4.1](https://github.com/skydoves/landscapist)).

### UI and Design
- **Jetpack Compose** - Declarative UI framework for Android (v2024.04.01).
- **Material 3** - Implements Material Design 3 components.
- **Google Fonts** - Custom fonts from Google in Jetpack Compose (v1.7.4).
- **Palette KTX** - Extracts prominent colors from images for dynamic theming (v1.0.0).

### Testing
- **JUnit** - Unit testing framework (v4.13.2).
- **AndroidX JUnit** - Provides compatibility with JUnit in Android (v1.1.5).
- **Espresso** - UI testing for Android (v3.5.1).
- **MockK** - Mocking framework for Kotlin (v1.12.0).
- **Coroutines Test** - Coroutines testing utilities (v1.6.0).

## Architecture
This project follows the MVVM (Model-View-ViewModel) architecture, leveraging the following components:
- **ViewModel** for managing UI-related data lifecycle-aware.
- **Repository** to handle data operations, providing a single source of truth.
- **Jetpack Compose** as a modern declarative UI toolkit, allowing highly responsive and efficient UI development.
