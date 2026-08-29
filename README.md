# BitBloom Admin

An Android administration client for the BitBloom Firebase project, covering users, plans, withdrawals, announcements, reports, support tickets, and leaderboard data.

> **Project status:** This is a project-specific admin application with significant credential and trust-boundary issues. It should not be treated as production-ready without security remediation and backend validation.

## Implemented Features

- Firebase Authentication login linked to an `Admin` Firestore record.
- Dashboard totals for active and inactive users, deposits, and withdrawals.
- User and account browsing with profile details.
- Investment-plan creation, editing, deletion, and user-plan visibility.
- Withdrawal request review with approval, rejection, address copying, and user notifications.
- Text announcement creation/deletion and broadcast notification attempts.
- Announcement poster upload and deletion through Firebase Storage and Firestore.
- Support-ticket lists filtered by pending, answered, closed, or all statuses.
- Ticket detail review and administrator responses.
- Time-filtered reports and top-leader views.
- Room caching for users, accounts, withdrawals, and user plans.
- Periodic Firestore-to-Room synchronization with WorkManager.
- Local FCM notification history.

## How It Works

1. `SplashActivity` checks the saved login state and routes to Firebase login or the main admin activity.
2. `LoginActivity` authenticates the administrator, updates the matching `Admin` record with its Firebase UID and FCM token, and saves the local session.
3. `MainActivity` hosts the Navigation graph, drawer, bottom navigation, and periodic `SyncWorker` job.
4. Repositories use `FirebaseHelper` and direct Firestore queries for remote data, while Room provides local flows for dashboard and list screens.
5. Feature fragments write plans, withdrawal decisions, announcements, posters, and ticket responses directly to Firebase services.

## Tech Stack

- Kotlin and Android XML layouts with view binding
- AndroidX Navigation, ViewModel, LiveData, Lifecycle, and WorkManager
- Room with KSP
- Firebase Authentication, Firestore, Storage, Cloud Messaging, Analytics, and Remote Config
- FirebaseUI Firestore
- Kotlin coroutines and Flow
- OkHttp, Volley, Gson, Glide, Picasso, Lottie, and ZXing
- Gradle Kotlin DSL, Android SDK 35, and JDK 11

## Project Structure

```text
app/src/main/java/com/example/bitbloomadmin/
|-- UI/ and ui/       # Dashboard and administration screens
|-- Viewmodel/        # Screen state and operations
|-- Repository/       # Users, plans, withdrawals, reports, tickets, and leaders
|-- Data/remote/      # Firebase access helper
|-- Data/local/       # Room database
|-- Dao/              # Room data-access interfaces
|-- Worker/           # Periodic Firestore-to-Room synchronization
|-- notifications/    # FCM receiving and sending helpers
`-- models/           # Firebase and Room models
```

## Getting Started

### Prerequisites

- Android Studio with Android SDK 35
- JDK 11
- Android 7.0 (API 24) or newer
- A compatible BitBloom Firebase project

The application ID is `com.example.bitbloomadmin`. The app expects Firebase Authentication and collections such as `Admin`, `users`, `accounts`, `plans`, `userPlans`, `withdraw_requests`, `announcements`, `announcement_images`, `tickets`, and `top_leaders`.

Build on Windows:

```powershell
.\gradlew.bat assembleDebug
```

Build on macOS or Linux:

```bash
./gradlew assembleDebug
```

## Current Limitations and Security Notes

- A Firebase service-account configuration template is provided. Privileged FCM sending should ideally be moved to a trusted backend.
- Administrative writes and withdrawal decisions occur directly in the client and depend on correctly restricted Firebase rules.
- The app is tightly coupled to an existing Firestore schema and does not include backend provisioning or seed data.
- Package naming is inconsistent between some source imports and the configured namespace, which may affect clean builds.
- Room uses destructive migration fallback and the sync worker replaces local tables from Firestore.
- Automated coverage is limited to generated example tests.

<!-- gitpulse:contribution index="1" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="2" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="3" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="4" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="5" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="6" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="7" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="8" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="9" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="10" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="11" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="12" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="13" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="14" timestamp="2026-08-22" -->
<!-- gitpulse:contribution index="15" timestamp="2026-08-22" -->