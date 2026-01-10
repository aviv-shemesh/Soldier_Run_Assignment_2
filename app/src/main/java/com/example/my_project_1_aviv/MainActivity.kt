package com.example.my_project_1_aviv

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_project_1_aviv.interfaces.TiltCallback
import com.example.my_project_1_aviv.utilities.TiltDetector
import com.example.my_project_1_aviv.utilities.ArmyScoreActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // --- UI Views ---

    private lateinit var rootLayout: View
    private lateinit var main_IMG_player: ImageView
    private lateinit var main_LBL_score: TextView
    private lateinit var main_BTN_left: ImageButton
    private lateinit var main_BTN_right: ImageButton
    private lateinit var main_IMG_hearts: Array<ImageView>
    private lateinit var main_IMG_berets: Array<ImageView>
    private lateinit var main_IMG_coins: Array<ImageView>
    private lateinit var main_LAYOUT_lives: RelativeLayout
    private lateinit var main_LAYOUT_buttons: LinearLayout

    // --- Game Logic ---
    private lateinit var gameManager: GameManager
    private lateinit var tiltDetector: TiltDetector
    private var startWithSensor: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var gameRunning = false
    private var baseDelay = 50L
    private var currentDelay = 50L
    private var frameCounter = 0
    private var score = 0
    private val NUM_LANES = 5
    private var laneWidth = 0f
    private val BASE_FALL_SPEED = 18f

    // --- Sound & Media ---
    private lateinit var soundPool: SoundPool
    private var soundCoinId: Int = 0
    private var soundCrashId: Int = 0
    private var mediaPlayerBackground: MediaPlayer? = null

    // --- Location (GPS) ---
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // טיפול ב-Edge to Edge (שורת סטטוס)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            findViewById<View>(R.id.livesLayout).setPadding(0, systemBars.top, 0, 0)
            insets
        }

        // אתחול רכיב המיקום
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViews()
        initGame()
        initTiltDetector()

        // חישוב רוחב המסלולים לאחר שהמסך נטען
        rootLayout.post {
            laneWidth = rootLayout.width.toFloat() / NUM_LANES
            updateUI(animated = false)
        }
    }

    // ---------------------- Initialization ----------------------

    private fun findViews() {
        rootLayout = findViewById(R.id.rootLayout)
        main_IMG_player = findViewById(R.id.imgPlayer)
        main_LBL_score = findViewById(R.id.lblScore)
        main_BTN_left = findViewById(R.id.btnLeft)
        main_BTN_right = findViewById(R.id.btnRight)
        main_LAYOUT_lives = findViewById(R.id.livesLayout)
        main_LAYOUT_buttons = findViewById(R.id.buttonsLayout)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        main_IMG_berets = arrayOf(
            findViewById(R.id.imgBeret1),
            findViewById(R.id.imgBeret2),
            findViewById(R.id.imgBeret3),
            findViewById(R.id.imgBeret4),
            findViewById(R.id.imgBeret5)
        )

        main_IMG_coins = arrayOf(
            findViewById(R.id.imgCoin1),
            findViewById(R.id.imgCoin2),
            findViewById(R.id.imgCoin3)
        )
    }

    private fun initGame() {
        startWithSensor = intent.getBooleanExtra("KEY_SENSOR_MODE", false)
        main_LAYOUT_buttons.visibility = if (startWithSensor) View.INVISIBLE else View.VISIBLE

        val isFast = intent.getBooleanExtra("KEY_SPEED", false)
        baseDelay = if (isFast) 20L else 50L
        currentDelay = baseDelay

        gameManager = GameManager(cols = NUM_LANES)
        score = 0

        main_BTN_left.setOnClickListener { moveSoldierLeft() }
        main_BTN_right.setOnClickListener { moveSoldierRight() }

        for (beret in main_IMG_berets) beret.visibility = View.INVISIBLE
        for (coin in main_IMG_coins) coin.visibility = View.INVISIBLE

        // אתחול סאונד
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        soundCoinId = soundPool.load(this, R.raw.coin, 1)
        soundCrashId = soundPool.load(this, R.raw.crash, 1)

        try {
            mediaPlayerBackground = MediaPlayer.create(this, R.raw.background_music)
            mediaPlayerBackground?.isLooping = true
            mediaPlayerBackground?.setVolume(0.5f, 0.5f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltX(x: Float) {
                    if (!startWithSensor) return
                    if (abs(x) >= 3.0) {
                        if (x > 0) moveSoldierLeft() else moveSoldierRight()
                    }
                }
                override fun tiltY(y: Float) {
                    if (!startWithSensor) return
                    if (abs(y) < 1.2f) {
                        currentDelay = baseDelay
                        return
                    }
                    val maxTilt = 9.0f
                    val t = (y / maxTilt).coerceIn(-1f, 1f)
                    val minDelay = (baseDelay * 0.8f).coerceAtLeast(15f)
                    val maxDelay = (baseDelay * 1.8f)
                    val mapped = baseDelay - (t * (baseDelay * 0.5f))
                    val newDelay = mapped.coerceIn(minDelay, maxDelay).roundToInt().toLong()
                    currentDelay = ((currentDelay * 0.85f) + (newDelay * 0.15f)).roundToInt().toLong()
                }
            }
        )
    }

    // ---------------------- Game Loop ----------------------

    private val gameRunnable = object : Runnable {
        override fun run() {
            if (gameRunning) {
                updateGameLogic()
                handler.postDelayed(this, currentDelay)
            }
        }
    }

    private fun updateGameLogic() {
        frameCounter++
        score += 1
        main_LBL_score.text = String.format("%03d", score)

        if (frameCounter % 35 == 0) dropBeret()
        if (frameCounter % 120 == 0) dropCoin()

        val factor = (baseDelay.toFloat() / currentDelay.toFloat()).coerceIn(0.6f, 2.2f)
        val fallSpeed = BASE_FALL_SPEED * factor

        // עדכון כומתות (מכשולים)
        for (beret in main_IMG_berets) {
            if (beret.visibility == View.VISIBLE) {
                beret.y += fallSpeed
                if (checkCollision(beret)) {
                    collision()
                    beret.visibility = View.INVISIBLE
                }
                if (beret.y > main_IMG_player.y + main_IMG_player.height - 350) {
                    beret.visibility = View.INVISIBLE
                }

            }

        }

        // עדכון מטבעות
        for (coin in main_IMG_coins) {
            if (coin.visibility == View.VISIBLE) {
                coin.y += fallSpeed
                if (checkCollision(coin)) {
                    if (soundCoinId != 0) soundPool.play(soundCoinId, 1f, 1f, 0, 0, 1f)
                    score += 100
                    main_LBL_score.text = String.format("%03d", score)
                    coin.visibility = View.INVISIBLE
                }
                if (coin.y > main_IMG_player.y + main_IMG_player.height - 350) {
                    coin.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun checkCollision(view: View): Boolean {
        if (view.visibility != View.VISIBLE) return false
        if (view.y + view.height > main_IMG_player.y &&
            view.y < main_IMG_player.y + main_IMG_player.height
        ) {
            val viewCenter = view.x + view.width / 2f
            val playerCenter = main_IMG_player.x + main_IMG_player.width / 2f
            if (laneWidth > 0f && abs(viewCenter - playerCenter) < laneWidth / 2f) {
                return true
            }
        }
        return false
    }

    private fun dropBeret() {
        if (laneWidth == 0f) return
        for (beret in main_IMG_berets) {
            if (beret.visibility != View.VISIBLE) {
                beret.visibility = View.VISIBLE
                beret.y = main_LAYOUT_lives.bottom.toFloat() + 20f
                val randomLane = Random.nextInt(0, NUM_LANES)
                beret.x = (randomLane + 0.5f) * laneWidth - beret.width / 2f
                break
            }
        }
    }

    private fun dropCoin() {
        if (laneWidth == 0f) return
        for (coin in main_IMG_coins) {
            if (coin.visibility != View.VISIBLE) {
                coin.visibility = View.VISIBLE
                coin.y = main_LAYOUT_lives.bottom.toFloat() + 20f
                val randomLane = Random.nextInt(0, NUM_LANES)
                coin.x = (randomLane + 0.5f) * laneWidth - coin.width / 2f
                break
            }
        }
    }

    // ---------------------- Collision & Game Over ----------------------

    private fun collision() {
        if (soundCrashId != 0) {
            soundPool.play(soundCrashId, 1f, 1f, 0, 0, 1f)
        }

        gameManager.reduceLives()
        updateLivesUI()

        if (gameManager.isGameOver) {
            gameRunning = false
            handler.removeCallbacks(gameRunnable)

            if (mediaPlayerBackground != null && mediaPlayerBackground!!.isPlaying) {
                mediaPlayerBackground?.pause()
            }

            // הצגת דיאלוג לסיום משחק (שם + מיקום)
            showGameOverDialog()
        }
    }

    private fun updateLivesUI() {
        val lives = gameManager.currentLives
        if (lives in 0 until main_IMG_hearts.size) {
            main_IMG_hearts[lives].visibility = View.INVISIBLE
        }
    }

    private fun showGameOverDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Enter your name"

        android.app.AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Score: $score\nEnter your name:")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Save Score") { _, _ ->
                val name = input.text.toString().ifEmpty { "Soldier" }

                // --- בדיקת הרשאות וקבלת מיקום ---
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // אין הרשאה - נבקש אותה (ובינתיים נשמור בלי מיקום)
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        101
                    )
                    moveToScoreScreen(name, 0.0, 0.0)
                    return@setPositiveButton
                }

                // יש הרשאה - ניסיון לקבלת מיקום אחרון
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            moveToScoreScreen(name, location.latitude, location.longitude)
                        } else {
                            moveToScoreScreen(name, 0.0, 0.0)
                        }
                    }
                    .addOnFailureListener {
                        moveToScoreScreen(name, 0.0, 0.0)
                    }
            }
            .show()
    }

    private fun moveToScoreScreen(name: String, lat: Double, lon: Double) {
        val intent = Intent(this, ArmyScoreActivity::class.java)
        intent.putExtra("KEY_SCORE", score)
        intent.putExtra("KEY_NAME", name)
        intent.putExtra("KEY_LAT", lat)
        intent.putExtra("KEY_LON", lon)
        startActivity(intent)
        finish()
    }

    // ---------------------- Movement & UI ----------------------

    private fun moveSoldierLeft() {
        if (gameManager.moveLeft()) updateUI(animated = true)
    }

    private fun moveSoldierRight() {
        if (gameManager.moveRight()) updateUI(animated = true)
    }

    private fun updateUI(animated: Boolean = true) {
        if (laneWidth == 0f) {
            val w = rootLayout.width.toFloat()
            if (w > 0f) laneWidth = w / NUM_LANES
        }
        if (laneWidth == 0f) return

        val laneCenterX = (gameManager.playerIdx + 0.5f) * laneWidth
        val targetX = laneCenterX - main_IMG_player.width / 2f

        if (animated) {
            main_IMG_player.animate().x(targetX).setDuration(80).start()
        } else {
            main_IMG_player.x = targetX
        }
    }

    // ---------------------- LifeCycle ----------------------

    override fun onResume() {
        super.onResume()
        if (!gameManager.isGameOver) {
            gameRunning = true
            handler.post(gameRunnable)
            if (startWithSensor) tiltDetector.start()
            try {
                if (mediaPlayerBackground != null && !mediaPlayerBackground!!.isPlaying) {
                    mediaPlayerBackground?.start()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onPause() {
        super.onPause()
        gameRunning = false
        handler.removeCallbacks(gameRunnable)
        if (startWithSensor) tiltDetector.stop()
        try {
            if (mediaPlayerBackground != null && mediaPlayerBackground!!.isPlaying) {
                mediaPlayerBackground?.pause()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        mediaPlayerBackground?.release()
        mediaPlayerBackground = null
    }
}