# Clean Architecture Demo — Android

Android project demonstrating **Clean Architecture + MVVM** with Hilt, Retrofit, Coroutines, and Flow.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary language |
| MVVM + Clean Architecture | Architecture pattern |
| Hilt | Dependency injection |
| Retrofit + Gson | Network calls |
| Kotlin Flow + StateFlow | Reactive data streams |
| ViewBinding | View access |
| Material Components | UI components |

---

## Project Structure

```
com.premitivekey.cleanarchitecturedemo
│
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── PagedUsers.kt
│   │   └── CreateUserRequest.kt
│   ├── repository/
│   │   └── UserRepository.kt        ← interface
│   └── usecase/
│       ├── GetUsersUseCase.kt
│       └── CreateUserUseCase.kt
│
├── data/
│   ├── model/
│   │   └── UserNetworkModels.kt     ← DTOs
│   ├── mapper/
│   │   └── UserMapper.kt
│   ├── remote/
│   │   └── UserApiService.kt        ← Retrofit
│   └── repository/
│       └── UserRepositoryImpl.kt
│
├── presentation/
│   ├── base/
│   │   └── UiState.kt
│   ├── viewmodel/
│   │   ├── UserViewModel.kt
│   │   └── AddUserViewModel.kt
│   ├── screen/
│   │   ├── HomeFragment.kt
│   │   ├── UserListFragment.kt
│   │   └── AddUserFragment.kt
│   └── adapter/
│       └── UserAdapter.kt
│
├── di/
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── MainActivity.kt
└── UserApp.kt
```

---

## Architecture & Dependency Rule

```
presentation  ──►  domain  ◄──  data
                     ▲
               di (wires all layers)
```

- **domain** — pure Kotlin, zero Android/Retrofit imports
- **data** — implements domain interfaces, handles network
- **presentation** — observes domain models via ViewModel
- **di** — wires interface to implementation using Hilt

---

## Layer Responsibilities

### Domain
- `User` — clean domain entity, no `@SerializedName`
- `PagedUsers` — wraps list with `currentPage`, `totalPages`, `hasNextPage`
- `CreateUserRequest` — input model for creating user
- `UserRepository` — interface owned by domain, implemented in data
- `GetUsersUseCase` / `CreateUserUseCase` — single-responsibility business logic

### Data
- `UserNetworkModels` — DTOs (`UserDto`, `UserResponse`, `CompanyDto`, etc.) with Gson annotations
- `UserMapper` — converts DTOs → domain models, domain models → DTOs
- `UserApiService` — Retrofit interface with `@GET` and `@POST`
- `UserRepositoryImpl` — implements `UserRepository`, converts page → skip, maps responses

### Presentation
- `UiState` — sealed class: `Idle`, `Loading`, `Success`, `Error`
- `UserViewModel` — accumulates paged users, manages `currentPage`, `isLastPage`, `isLoadingMore`
- `AddUserViewModel` — handles create user flow with `resetState()`
- `UserAdapter` — `ListAdapter` with `DiffUtil`, binds domain `User` model
- `UserListFragment` — scroll listener triggers next page load
- `AddUserFragment` — form with validation, disables button during loading

### DI
- `NetworkModule` — provides `Retrofit` and `UserApiService` as singletons
- `RepositoryModule` — `@Binds` domain interface to data implementation

---

## API

Base URL: `https://dummyjson.com/`

### GET Users
```
GET /users?limit=10&skip=0
```
```json
{
  "users": [{ "id": 1, "firstName": "Emily", "lastName": "Johnson", "email": "...", "company": { "name": "..." } }],
  "total": 208,
  "skip": 0,
  "limit": 10
}
```

### POST Create User
```
POST /users/add
```
```json
// Request
{ "firstName": "John", "lastName": "Doe", "email": "john@example.com" }

// Response
{ "id": 209, "firstName": "John", "lastName": "Doe", "email": "john@example.com" }
```

---

## Pagination

- Page size: `10`, Total users: `208`, Total pages: `21`
- Formula: `skip = (page - 1) * pageSize`
- ViewModel tracks page numbers — repository converts to `skip` internally
- Scroll listener fires `loadUsers()` when within 3 items of bottom
- `isLoadingMore` StateFlow controls bottom spinner independently from main state

---

## Dependencies — `libs.versions.toml`

```toml
[versions]
hilt       = "2.51.1"
retrofit   = "2.9.0"
lifecycle  = "2.8.7"
coroutines = "1.9.0"
fragment   = "1.8.6"

[libraries]
hilt-android        = { group = "com.google.dagger",      name = "hilt-android",            version.ref = "hilt" }
hilt-compiler       = { group = "com.google.dagger",      name = "hilt-android-compiler",   version.ref = "hilt" }
retrofit-core       = { group = "com.squareup.retrofit2", name = "retrofit",                version.ref = "retrofit" }
retrofit-gson       = { group = "com.squareup.retrofit2", name = "converter-gson",          version.ref = "retrofit" }
lifecycle-viewmodel = { group = "androidx.lifecycle",     name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
fragment-ktx        = { group = "androidx.fragment",      name = "fragment-ktx",            version.ref = "fragment" }
coroutines-android  = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

[plugins]
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp  = { id = "com.google.devtools.ksp",        version = "2.0.21-1.0.28" }
```

## `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.fragment.ktx)
    implementation(libs.coroutines.android)
}
```

---

## Common Errors & Fixes

| Error | Fix |
|---|---|
| `Expected BEGIN_OBJECT but was BEGIN_ARRAY` | `UserApiService` must return `Response<UserResponse>` not `Response<List<UserDto>>` |
| `MissingBinding UserRepository` | Add `RepositoryModule` with `@Binds` |
| `Too many arguments for getUsers()` | Update `UserApiService` to include `@Query("limit")` and `@Query("skip")` |
| Bottom loader shows continuously | Use dedicated `isLoadingMore` StateFlow, show only when `accumulatedUsers.isNotEmpty()` |
| Hilt crash on launch | Add `@HiltAndroidApp` to `UserApp`, set `android:name=".UserApp"` in manifest |

---

## Setup

1. Set `android:name=".UserApp"` in `AndroidManifest.xml`
2. Add `<uses-permission android:name="android.permission.INTERNET" />` in manifest
3. Sync Gradle and run — min SDK 24
