package com.example.my_project_1_aviv.utilities
import android.content.Context
import com.example.my_project_1_aviv.utilities.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreManager(val context: Context) {
    private val SP_FILE = "MY_SCORES"
    private val SP_KEY_SCORES = "SCORES_LIST"
    private val gson = Gson()

    fun saveNewScore(score: Score) {
        val currentScores = getAllScores()
        currentScores.add(score)
        currentScores.sortByDescending { it.score }
        val topScores = if (currentScores.size > 10) {
            ArrayList(currentScores.subList(0, 10))
        } else {
            currentScores
        }

        val jsonString = gson.toJson(topScores)
        val prefs = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        prefs.edit().putString(SP_KEY_SCORES, jsonString).apply()
    }

    fun getAllScores(): ArrayList<Score> {
        val prefs = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(SP_KEY_SCORES, null)

        return if (jsonString == null) {
            ArrayList()
        } else {
            val type = object : TypeToken<ArrayList<Score>>() {}.type
            gson.fromJson(jsonString, type)
        }
    }
}