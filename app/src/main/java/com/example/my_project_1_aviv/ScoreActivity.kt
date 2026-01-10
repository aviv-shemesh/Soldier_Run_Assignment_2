package com.example.my_project_1_aviv
import com.example.my_project_1_aviv.utilities.ScoreManager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.example.my_project_1_aviv.utilities.MapFragment
import com.example.my_project_1_aviv.utilities.Score
import com.example.my_project_1_aviv.utilities.ScoreListFragment

// שים לב: אם Score נמצא גם ב-utilities, תוסיף גם אותו. אם הוא בתיקייה הראשית, השורה הזו מיותרת אך לא תזיק.
// import com.example.my_project_1_aviv.utilities.Score

class ScoreActivity : AppCompatActivity() {


    private lateinit var score_LBL_score: TextView
    private lateinit var score_FRAME_content: FrameLayout
    private lateinit var score_BTN_list: MaterialButton
    private lateinit var score_BTN_map: MaterialButton
    private lateinit var score_BTN_back: MaterialButton

    private lateinit var listFragment: ScoreListFragment
    private lateinit var mapFragment: MapFragment

    // משתנה לניהול המיקום
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // אתחול רכיב המיקום
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViews()
        initViews()

        // 1. קבלת הניקוד מהאינטנט
        val scoreValue = intent.getIntExtra("KEY_SCORE", 0)
        score_LBL_score.text = "Score: $scoreValue"

        // 2. לוגיקה לשמירת הניקוד עם המיקום
        if (scoreValue > 0) {
            // בדיקה אם יש הרשאת מיקום
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // יש הרשאה - מנסים להביא את המיקום האחרון הידוע
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val scoreManager = ScoreManager(this)

                    // אם המיקום נמצא נשתמש בו, אחרת נשים 0.0
                    val lat = location?.latitude ?: 0.0
                    val lon = location?.longitude ?: 0.0

                    // וודא ש-Score מיובא נכון. אם Score הוא בתיקייה הראשית, זה יעבוד.
                    val newScore = Score(name = "Player", score = scoreValue, lat = lat, lon = lon)
                    scoreManager.saveNewScore(newScore)
                }
            } else {
                // אין הרשאה - מבקשים אותה מהמשתמש
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    123 // קוד זיהוי לבקשה
                )

                // בינתיים שומרים את הניקוד ללא מיקום (כדי לא לאבד אותו)
                val scoreManager = ScoreManager(this)
                val newScore = Score(name = "Player", score = scoreValue, lat = 0.0, lon = 0.0)
                scoreManager.saveNewScore(newScore)
            }
        }

        // יצירת הפרגמנטים
        listFragment = ScoreListFragment()
        mapFragment = MapFragment()

        // התחלה עם הרשימה
        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_content, listFragment)
            .commit()
    }

    private fun findViews() {
        score_LBL_score = findViewById(R.id.score_LBL_score)
        score_FRAME_content = findViewById(R.id.score_FRAME_content)
        score_BTN_list = findViewById(R.id.score_BTN_list)
        score_BTN_map = findViewById(R.id.score_BTN_map)
        score_BTN_back = findViewById(R.id.score_BTN_back)
    }

    private fun initViews() {
        score_BTN_list.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.score_FRAME_content, listFragment)
                .commit()
        }

        score_BTN_map.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.score_FRAME_content, mapFragment)
                .commit()
        }

        score_BTN_back.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}