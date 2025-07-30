# BitBloom Admin

## Overview
BitBloom‑Admin is an Android admin application designed to manage the BitBloom platform. It enables administrators to oversee users, subscription plans, and content, send notifications, and view analytics — all from a modern, mobile interface. Built with Kotlin and a clean MVVM architecture, the app integrates Firebase services and other Jetpack components to provide a robust, scalable solution.

## Features
- **User Management**: View, edit and manage user accounts, including status, roles and subscriptions.
- **Plan & Subscription Management**: Create, update and delete subscription plans, monitor active subscriptions and manage billing cycles.
- **Content Moderation**: Approve or reject content submissions and manage categories and tags.
- **Real‑Time Notifications**: Use Firebase Cloud Messaging (FCM) to send announcements and updates directly to users.
- **Secure Authentication**: Integrate with Firebase Authentication for administrator sign‑in and role‑based access control.
- **Dashboard & Analytics**: Display key metrics and visual insights about user activity, subscriptions, and content performance.
- **Modern UI & Navigation**: Built with Jetpack Compose/XML and Material Design guidelines for a responsive, intuitive interface.
- **Clean Architecture**: Implements the MVVM pattern with a repository layer, use cases, and separation of concerns using coroutines and Flow.

## Tech Stack

| Layer            | Technology                                 |
|------------------|---------------------------------------------|
| Language         | Kotlin                                      |
| Architecture     | MVVM + Repository Pattern                   |
| Dependency       | Dagger‑Hilt                                 |
| Database         | Room / SQLite                               |
| Network          | Retrofit & OkHttp                           |
| Backend Services | Firebase (Authentication, Firestore, FCM)   |
| UI               | Jetpack Compose or XML + Material Design    |
| Asynchronous     | Kotlin Coroutines & Flow                    |

## Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/shayann07/BitBloom-Admin.git
   ```
2. **Open in Android Studio**: Use the latest stable version of Android Studio.
3. **Configure Firebase**:
   - Create a Firebase project and add an Android app.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable Authentication, Firestore and Cloud Messaging in the Firebase console.
4. **Build and run** the app on an emulator or physical device.
5. **Admin Credentials**: Set up administrator accounts in Firebase Authentication or your backend to gain access to the admin features.

## Contribution

Contributions are welcome! Feel free to submit issues or pull requests to improve features, fix bugs or suggest enhancements.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
