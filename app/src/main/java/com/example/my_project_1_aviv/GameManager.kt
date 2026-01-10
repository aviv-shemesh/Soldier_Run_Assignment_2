package com.example.my_project_1_aviv

class GameManager(private val lifeCount: Int = 3, val cols: Int = 3) {

    // מיקום השחקן מתחיל תמיד באמצע (לפי מספר המסלולים)
    var playerIdx: Int = cols / 2
        private set

    var currentLives: Int = lifeCount
        private set

    val isGameOver: Boolean
        get() = currentLives <= 0

    fun reduceLives() {
        if (currentLives > 0) {
            currentLives--
        }
    }


    fun moveLeft(): Boolean {
        // אם השחקן לא נמצא בקצה השמאלי ביותר -> זוז שמאלה
        if (playerIdx > 0) {
            playerIdx--
            return true
        }
        return false
    }

    fun moveRight(): Boolean {
        // אם השחקן לא נמצא בקצה הימני ביותר -> זוז ימינה
        if (playerIdx < cols - 1) {
            playerIdx++
            return true
        }
        return false
    }
}