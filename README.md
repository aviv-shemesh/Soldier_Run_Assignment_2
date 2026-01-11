🏃‍♂️ Soldier Run - Assignment 2 (Upgraded Version)
This is the second phase of the Soldier Run project. This version introduces advanced UI components, data persistence, and location-based features.

✨ What's New in Assignment 2:
High Scores System: Persistent storage of the top 10 highest scores using SharedPreferences.

Fragments Integration: The high scores screen is divided into two interactive fragments:

List Fragment: Displays a sorted leaderboard with names and scores.

Map Fragment: Integrates Google Maps API to show markers at the locations where high scores were achieved.

Interactive UI: Clicking on a score in the list automatically focuses the map on that specific location with a smooth zoom.

User Personalization: Added a "Game Over" dialog for players to enter their names before saving scores.

Haptic Feedback: Integrated vibration effects upon obstacle collision to improve the gaming experience.

Real GPS Integration: Uses FusedLocationProvider to capture the player's real-world coordinates.

🛠 Tech Stack
Language: Kotlin

APIs: Google Maps SDK, Fused Location Provider

UI: Fragments, RecyclerView, Material Design

Persistence: SharedPreferences (JSON via GSON)
