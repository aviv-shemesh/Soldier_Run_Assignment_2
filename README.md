# 🏃‍♂️ Soldier Run - Assignment 2

### 🎓 Android Development Course Project (2025) - Afeka College
An upgraded, fast-paced survival game where the soldier must dodge falling berets and collect coins to reach the top of the leaderboard.

---

## 🚀 Key Features - Assignment 2 Upgrades

This version introduces advanced logic and UI components as per assignment requirements:

* **🏆 Global Leaderboard**: Persistent storage of the top 10 highest scores achieved.
* **🧩 Multi-Fragment UI**: Implementation of two dynamic fragments within the High Scores screen:
    * **List View**: A detailed, sorted list of players and their scores.
    * **Map View**: Google Maps integration displaying locations where records were set.
* **📍 Interactive Map Navigation**: Clicking the map icon in the score list triggers an automatic camera zoom to the specific location on the map.
* **👤 Name Personalization**: A Game Over dialog allows players to enter and save their names to the record table.
* **🛰 Real-World GPS**: Capturing real-time coordinates using the Fused Location Provider.
* **📳 Haptic Feedback**: Enhanced user experience with vibration effects upon collision.

---

## 🛠 Tech Stack & Tools
* **Language**: Kotlin
* **Architecture**: Fragments, RecyclerView, Material Design
* **Storage**: SharedPreferences (JSON parsing via GSON)
* **Maps**: Google Maps SDK for Android
* **Location**: Google Play Services (Location)

---

## ⚙️ How to Run
1. Clone the repository.
2. Open in Android Studio.
3. Add your `MAPS_API_KEY` to `local.properties`.
4. Run on an Emulator or Physical Device.
