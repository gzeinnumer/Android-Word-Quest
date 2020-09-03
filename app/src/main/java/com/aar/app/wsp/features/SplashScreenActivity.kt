package com.aar.app.wsp.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aar.app.wsp.features.mainmenu.MainMenuActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MainMenuActivity::class.java)
        )
        finish()
    }
}