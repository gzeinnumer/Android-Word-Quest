package com.aar.app.wsp.features

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aar.app.wsp.R
import com.aar.app.wsp.WordSearchApp
import com.aar.app.wsp.features.settings.Preferences
import javax.inject.Inject

/**
 * Created by abdularis on 21/04/17.
 *
 * Extend this class to make a fullscreen activity
 */
@SuppressLint("Registered")
open class FullscreenActivity : AppCompatActivity() {

    @Inject
    protected lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as WordSearchApp).appComponent.inject(this)

        if (preferences.enableFullscreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }
}