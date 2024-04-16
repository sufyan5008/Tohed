package com.tohed.islampro.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tohed.islampro.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private  lateinit var binding: ActivitySettingBinding

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}