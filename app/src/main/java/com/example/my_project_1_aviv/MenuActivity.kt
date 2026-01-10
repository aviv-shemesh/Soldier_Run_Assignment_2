package com.example.my_project_1_aviv

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
// --- הוספתי את השורה הזו שחסרה לך ---
import com.example.my_project_1_aviv.utilities.ArmyScoreActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var menu_TOGGLE_speed: MaterialButtonToggleGroup
    private lateinit var menu_BTN_slow: MaterialButton
    private lateinit var menu_BTN_fast: MaterialButton
    private lateinit var menu_BTN_start: MaterialButton
    private lateinit var menu_BTN_sensors: MaterialButton
    private lateinit var menu_BTN_scores: MaterialButton

    private var isFast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViews()
        initViews()

        // סימון ברירת מחדל (איטי) בהתחלה
        updateSpeedColors(false)
    }

    private fun findViews() {
        menu_TOGGLE_speed = findViewById(R.id.menu_TOGGLE_speed)
        menu_BTN_slow = findViewById(R.id.menu_BTN_slow)
        menu_BTN_fast = findViewById(R.id.menu_BTN_fast)
        menu_BTN_start = findViewById(R.id.menu_BTN_start)
        menu_BTN_sensors = findViewById(R.id.menu_BTN_sensors)
        menu_BTN_scores = findViewById(R.id.menu_BTN_scores)
    }

    private fun initViews() {
        // האזנה לשינוי במהירות
        menu_TOGGLE_speed.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isFast = (checkedId == R.id.menu_BTN_fast)
                updateSpeedColors(isFast)
            }
        }


        menu_BTN_start.setOnClickListener {
            startGame(useSensors = false)
        }

        menu_BTN_sensors.setOnClickListener {
            startGame(useSensors = true)
        }

        // --- כפתור שיאים ---
        menu_BTN_scores.setOnClickListener {
            val intent = Intent(this, ArmyScoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateSpeedColors(isFastSelected: Boolean) {
        if (isFastSelected) {
            menu_BTN_fast.setBackgroundColor(Color.parseColor("#6B8E23"))
            menu_BTN_fast.setTextColor(Color.WHITE)
            menu_BTN_slow.setBackgroundColor(Color.TRANSPARENT)
            menu_BTN_slow.setTextColor(Color.WHITE)
        } else {
            menu_BTN_slow.setBackgroundColor(Color.parseColor("#6B8E23"))
            menu_BTN_slow.setTextColor(Color.WHITE)
            menu_BTN_fast.setBackgroundColor(Color.TRANSPARENT)
            menu_BTN_fast.setTextColor(Color.WHITE)
        }
    }

    private fun startGame(useSensors: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("KEY_SENSOR_MODE", useSensors)
        intent.putExtra("KEY_SPEED", isFast)
        startActivity(intent)
    }
}