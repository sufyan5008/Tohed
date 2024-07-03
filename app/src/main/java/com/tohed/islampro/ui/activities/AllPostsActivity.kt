package com.tohed.islampro.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.PostDetailsFragment
import com.tohed.islampro.R
import com.tohed.islampro.adapters.PostAdapter
import com.tohed.islampro.databinding.ActivityAllMatchesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.viewModel.PostViewModel

class AllPostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllMatchesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter


    companion object {
        fun getIntent(requireContext: Context): Intent {
            return Intent(requireContext, AllPostsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        postAdapter = PostAdapter(emptyList()) { clickedPost ->
            handlePostItemClick(clickedPost)
        }

        binding.allMatches.apply {
            layoutManager = LinearLayoutManager(this@AllPostsActivity)
            adapter = postAdapter
        }

        // Observe posts LiveData
        postViewModel.postsLiveData.observe(this) { posts ->
            postAdapter.updatePosts(posts)
            binding.loadingIndicator.visibility = View.GONE
            binding.loadMoreButton.visibility = View.VISIBLE
        }


      //  postViewModel.fetchPosts()
        fetchPosts()

        binding.loadMoreButton.setOnClickListener {
            //postViewModel.fetchPosts()
            fetchPosts()
        }
    }

    private fun fetchPosts() {
        binding.loadingIndicator.visibility = View.VISIBLE // Show loader when starting fetch
        binding.loadMoreButton.visibility = View.GONE // Hide load more button
        postViewModel.fetchPosts()
    }
    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val intent = Intent(this, PostDetailsActivity::class.java)
        intent.putExtra("postId", postId)
        startActivity(intent)
    }
    /*private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle()
        args.putLong("postId", postId)

        val postDetailsFragment = PostDetailsFragment()
        postDetailsFragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, postDetailsFragment)
            .addToBackStack(null) // Optional: Add to back stack for back navigation
            .commit()
    }*/

}
