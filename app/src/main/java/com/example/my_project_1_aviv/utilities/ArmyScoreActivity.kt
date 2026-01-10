package com.example.my_project_1_aviv.utilities

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.my_project_1_aviv.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlin.random.Random

class ArmyScoreActivity : AppCompatActivity() {

    private lateinit var score_FRAME_content: FrameLayout
    private lateinit var score_TOGGLE_view: MaterialButtonToggleGroup
    private lateinit var score_BTN_back: MaterialButton
    private lateinit var score_LBL_current_score: TextView

    private lateinit var listFragment: ScoreListFragment
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_army_scores)

        findViews()
        handleNewScore()
        initViews()
    }

    private fun findViews() {
        score_FRAME_content = findViewById(R.id.score_FRAME_content)
        score_TOGGLE_view = findViewById(R.id.score_TOGGLE_view)
        score_BTN_back = findViewById(R.id.score_BTN_back)
        score_LBL_current_score = findViewById(R.id.score_LBL_current_score)
    }

    private fun handleNewScore() {
        val newScoreValue = intent.getIntExtra("KEY_SCORE", -1)
        val playerName = intent.getStringExtra("KEY_NAME") ?: "Soldier"
        val receivedLat = intent.getDoubleExtra("KEY_LAT", 0.0)
        val receivedLon = intent.getDoubleExtra("KEY_LON", 0.0)

        if (newScoreValue != -1) {
            score_LBL_current_score.text = "Your Score: $newScoreValue"

            val scoreManager = ScoreManager(this)
            val finalLat: Double
            val finalLon: Double

            if (receivedLat == 0.0 && receivedLon == 0.0) {
                // אין GPS -> ברירת מחדל
                finalLat = 32.0853 + kotlin.random.Random.nextDouble(-0.01, 0.01)
                finalLon = 34.7818 + kotlin.random.Random.nextDouble(-0.01, 0.01)
            } else {
                // יש GPS! -> נשתמש במיקום האמיתי
                finalLat = receivedLat
                finalLon = receivedLon
            }

            val newScore = Score(
                name = playerName,
                score = newScoreValue,
                lat = finalLat,
                lon = finalLon
            )
            scoreManager.saveNewScore(newScore)
        } else {
            score_LBL_current_score.text = "High Scores"
        }
    }
    private fun initViews() {
        listFragment = ScoreListFragment()
        mapFragment = MapFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_content, listFragment)
            .commit()

        score_TOGGLE_view.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val transaction = supportFragmentManager.beginTransaction()
                if (checkedId == R.id.score_BTN_list) {
                    transaction.replace(R.id.score_FRAME_content, listFragment)
                } else if (checkedId == R.id.score_BTN_map) {
                    transaction.replace(R.id.score_FRAME_content, mapFragment)
                }
                transaction.commit()
            }
        }

        score_BTN_back.setOnClickListener {
            finish()
        }
    }

    fun showMapLocation(lat: Double, lon: Double) {
        score_TOGGLE_view.check(R.id.score_BTN_map)


        supportFragmentManager.beginTransaction()
            .replace(R.id.score_FRAME_content, mapFragment)
            .commit()

        score_FRAME_content.postDelayed({
            mapFragment.zoom(lat, lon)
        }, 100) 
    }
}