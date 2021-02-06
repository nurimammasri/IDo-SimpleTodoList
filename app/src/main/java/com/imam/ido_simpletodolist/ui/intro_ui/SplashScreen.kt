package com.imam.ido_simpletodolist.ui.intro_ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.imam.ido_simpletodolist.R

class SplashScreen : AppCompatActivity() {
    
    private lateinit var headerSplash: TextView
    private lateinit var ido: ImageView
    private lateinit var idoDesc: TextView
    private lateinit var bubbleSplash: ImageView
    private lateinit var bubbleSplash1: ImageView
    private lateinit var bubbleSplash2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*transparant toolbar*/
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.splash_screen)


        /*Animation Splash Screen*/
        //LOGO
        val fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        ido = findViewById(R.id.IDo)
        ido.startAnimation(fadein)
        //BUBBLE
        bubbleSplash = findViewById(R.id.bubble)
         bubbleSplash.startAnimation(fadein)
        //BUBBLE SPLASH 1
        bubbleSplash1 = findViewById(R.id.bubble_splash1)
        bubbleSplash1.startAnimation(fadein)
        //BUBBLE SPLASH 1
        bubbleSplash2 = findViewById(R.id.bubble_splash2)
         bubbleSplash2.startAnimation(fadein)
        idoDesc = findViewById(R.id.IDo_desc)
        headerSplash = findViewById(R.id.header_splash)
        idoDesc.startAnimation(fadein)
        headerSplash.startAnimation(fadein)


        /*Menjalankan Splash Screen dalam beberapa detik*/
        Handler()
            .postDelayed({
                val intent = Intent(this@SplashScreen, IntroActivity::class.java)
                startActivity(intent)

                /*Splash Screen Hilang*/finish()
            }, 4000)

    }
}