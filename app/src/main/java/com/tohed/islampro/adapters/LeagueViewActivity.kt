package com.tohed.islampro.adapters

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tohed.islampro.databinding.ActivityLeagueViewBinding

class LeagueViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeagueViewBinding

    companion object {
        fun getIntent(context: Context): Intent {
            return  Intent(context, LeagueViewActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeagueViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.teamRanks.adapter = LeaguesTeamAdapter()
    }

}