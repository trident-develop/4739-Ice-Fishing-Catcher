package com.feelingtouch.r

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.feelingtouch.r.audio.SoundManager
import com.feelingtouch.r.navigation.AppNavGraph
import com.feelingtouch.r.storage.PrefsManager
import com.feelingtouch.r.ui.theme.IceFishingCatcherTheme

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager
    private lateinit var prefsManager: PrefsManager
    private val windowController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }
    private var multiTouchDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefsManager = PrefsManager(this)
        soundManager = SoundManager(this).apply {
            setMusicEnabled(prefsManager.musicEnabled)
            setSoundEnabled(prefsManager.soundEnabled)
        }

        setContent {
            IceFishingCatcherTheme() {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    prefsManager = prefsManager,
                    soundManager = soundManager,
                    onExitApp = { finishAffinity() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        windowController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowController.hide(WindowInsetsCompat.Type.systemBars())
        if (prefsManager.musicEnabled) {
            soundManager.startMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            if (!multiTouchDetected) {
                multiTouchDetected = true
                val cancelEvent = MotionEvent.obtain(ev)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
            }
            return true
        }
        if (multiTouchDetected) {
            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                multiTouchDetected = false
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}