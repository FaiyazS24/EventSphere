# EventSphere - Event Booking Application

## Overview

**EventSphere** is a modern event booking application designed to make discovering, booking, and managing events seamless and intuitive. The app provides users with features like event browsing, secure booking, and profile management, making it a one-stop solution for all event enthusiasts. With Firebase integration and a user-friendly design, **EventSphere** ensures a reliable and smooth experience.


## Features

- **Browse Events**: Discover a variety of events, including sports, workshops, and entertainment.
- **Event Details**: Access detailed information about events, such as date, time, location, and pricing.
- **Secure Booking**: Book your favorite events with a secure checkout process.
- **User Profiles**: Sign up, log in, and manage your personal profile.
- **Manage Bookings**: View and manage your upcoming, saved, and past events.
- **Responsive Design**: Enjoy a seamless user experience on different device sizes.



## Technologies Used

- **Programming Language**: Java
- **Framework**: Android SDK
- **Backend**: Firebase Realtime Database
- **Build System**: Gradle
- **Version Control**: Git



## File Structure

```plaintext
EventsClientApp1/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/eventsclientapp1/
│   │   │   │   ├── MainActivity.java               # Main entry point
│   │   │   │   ├── EventDetailActivity.java        # Event details page
│   │   │   │   ├── CheckoutActivity.java           # Checkout logic
│   │   │   │   ├── CartActivity.java               # Cart management
│   │   │   │   ├── SignInActivity.java             # User login functionality
│   │   │   │   ├── SignUpActivity.java             # User registration logic
│   │   │   │   ├── UpcomingActivity.java           # Displays upcoming events
│   │   │   │   ├── UpdateProfileActivity.java      # User profile management
│   │   │   ├── res/
│   │   │       ├── layout/                         # XML layouts for UI
│   │   │       │   ├── activity_main.xml           # Main layout
│   │   │       │   ├── activity_checkout.xml       # Checkout layout
│   │   │       │   ├── activity_event_detail.xml   # Event details layout
│   │   │       ├── drawable/                       # App icons and graphics
│   │   │       │   ├── logo.png                    # Application logo
│   └── AndroidManifest.xml                         # App configuration
├── gradle/                                         # Gradle configuration files
├── build.gradle.kts                                # Build script for the app
├── gradlew                                         # Gradle wrapper script
├── gradlew.bat                                     # Gradle wrapper for Windows
├── README.md                                       # Project documentation


## Developed By: Faiyaz Sattar & Mohit Singh
