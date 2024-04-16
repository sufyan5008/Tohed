package com.tohed.islampro.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.tohed.islampro.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        runSplash()
    }


    private fun runSplash(){
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(HomeActivity.getIntent(this))
            finish()
        }, 3000)
    }
}