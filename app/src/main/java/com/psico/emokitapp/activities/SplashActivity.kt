package com.psico.emokitapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.psico.emokitapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }

}
