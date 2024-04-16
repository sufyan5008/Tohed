package com.tohed.islampro.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.adapters.PostAdapter
import com.tohed.islampro.databinding.ActivityAllMatchesBinding
import com.tohed.islampro.viewModel.PostViewModel

class AllPostsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAllMatchesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter


    companion object {
        fun getIntent(requireContext: Context): Intent {
            return  Intent(requireContext, AllPostsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        postAdapter = PostAdapter(emptyList()) {

        }
        binding.allMatches.apply {
            layoutManager = LinearLayoutManager(this@AllPostsActivity)
            adapter = postAdapter
        }

            // Observe posts LiveData
            postViewModel.postsLiveData.observe(this) { posts ->
                postAdapter.updatePosts(posts)
            }


            // Fetch posts
            postViewModel.fetchPosts()


        binding.loadMoreButton.setOnClickListener {
            postViewModel.fetchPosts()
        }
        }
    }
