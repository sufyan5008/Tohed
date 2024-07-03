package com.tohed.islampro.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tohed.islampro.PostDetailsFragment
import com.tohed.islampro.databinding.ActivityPostDetailsBinding
import com.tohed.islampro.viewModel.PostViewModel

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var postViewModel: PostViewModel
    private var postId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get postId from intent
        postId = intent.getLongExtra("postId", 0)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Set up PostDetailsFragment
        val fragment = PostDetailsFragment()
        val bundle = Bundle()
        bundle.putLong("postId", postId)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}
