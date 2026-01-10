package com.example.my_project_1_aviv.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.my_project_1_aviv.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private val tiltCallback: TiltCallback) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private lateinit var sensorEventListener: SensorEventListener

    private var lastTiltTime: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // לא בשימוש
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                calculateTilt(x, y)
            }
        }
    }

    private fun calculateTilt(x: Float, y: Float) {
        val currentTime = System.currentTimeMillis()

        // --- בדיקת ציר X (תזוזה ימינה/שמאלה) ---
        // אנחנו בודקים אם עבר מספיק זמן מאז ההטיה האחרונה (350 מילישניות)
        // זה מונע מהחייל לזוז 5 נתיבים בשנייה אחת
        if (currentTime - lastTiltTime > 350) {

            // בדיקת סף רגישות (Threshold) - רק אם ההטיה חזקה מ-3.0
            if (abs(x) >= 3.0) {
                lastTiltTime = currentTime
                tiltCallback.tiltX(x) // שולח ל-Main כדי להזיז את החייל
            }
        }

        tiltCallback.tiltY(y)
    }

    fun start() {
        if (sensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        if (sensor != null) {
            sensorManager.unregisterListener(
                sensorEventListener,
                sensor
            )
        }
    }
}