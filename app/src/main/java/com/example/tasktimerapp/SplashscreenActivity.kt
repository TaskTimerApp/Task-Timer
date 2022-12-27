package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView

class SplashscreenActivity : AppCompatActivity() {
    lateinit var logo: AppCompatImageView

    /** Splash screen timer */
    private val SPLASH_TIME_OUT = 4500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        /////////////////////////////////////////////////////
        ////////////////ANIMATION CREATION//////////////////
        logo = findViewById(R.id.logo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_logo)
        logo.startAnimation(animation)
        /////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        Handler().postDelayed({ val i = Intent(this@SplashscreenActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }, SPLASH_TIME_OUT)

    }
}