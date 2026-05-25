# OceanxAgency

OceanxAgency is an Android app built with **Kotlin** and **Jetpack Compose**. It is centered around a clean, modern **Orders** dashboard UI for viewing trips, searching orders, and accessing common order actions.

---

## Overview

This project focuses on a simple, corporate-style mobile experience with a clear layout and easy navigation.

### Key UI areas
- Branded header section
- Search bar for quick filtering
- Order status tabs
- Informational banner
- Order cards with actions
- Floating help button
- Bottom navigation bar

---

## What this project uses

### Core technologies
- **Kotlin**
- **Android SDK**
- **Jetpack Compose**
- **Material 3**
- **RecyclerView**
- **AndroidX libraries**

### Main dependencies
- `androidx.activity:activity-compose`
- `androidx.compose.material3`
- `androidx.compose.ui`
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.recyclerview:recyclerview`

### Testing libraries
- **JUnit**
- **AndroidX JUnit**
- **Espresso**
- **Compose UI Test**

---

## App features

### Orders screen
- Displays sample order data
- Shows order ID, amount, pickup, drop, and status
- Includes actions such as:
  - Invoice
  - Book Again

### Search and filters
- Search by:
  - Order ID
  - Pickup location
  - Drop location
  - Status
- Tabs for:
  - All Orders
  - Completed
  - Cancelled
  - Booked Again

### Navigation
- Home
- Orders
- Payments
- Account

---

## Project structure

- `app/src/main/java/com/akshat/oceanxagency/MainActivity.kt` — Main app screen and UI logic
- `app/src/main/java/com/akshat/oceanxagency/ui/theme/` — Theme, colors, and typography
- `app/src/main/AndroidManifest.xml` — App entry point and activity setup
- `gradle/libs.versions.toml` — Central dependency and plugin versions

---

## Requirements

- Android Studio
- JDK 11 or higher
- Android emulator or physical device

---

## How to run

1. Open the project in **Android Studio**.
2. Let Gradle sync complete.
3. Run the `app` module on an emulator or connected device.

---

## Notes

- The app currently uses a UI-first implementation.
- Some actions such as Help, Filter, Sort, Invoice, and Book Again are present as interface elements and can be wired to business logic later.
- The project is ready for extension into a fuller order-management experience.

---

## Version

- **App version:** 1.0
- **Package name:** `com.akshat.oceanxagency`

---

## License

This repository does not currently include a license.
If you plan to share or distribute the app publicly, consider adding one.

