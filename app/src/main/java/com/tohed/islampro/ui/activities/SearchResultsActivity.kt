package com.tohed.islampro.ui.activities


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.PostAdapter
import com.tohed.islampro.databinding.ActivitySearchResultsBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.ui.activities.PostDetailsActivity
import com.tohed.islampro.viewModel.PostViewModel

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = intent.getStringExtra("query") ?: ""
        binding.searchText.text = query

        postAdapter = PostAdapter(emptyList()) { clickedPost -> handlePostItemClick(clickedPost) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = postAdapter

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        postViewModel.searchResultsLiveData.observe(this) { posts ->
            if (posts.isEmpty()) {
                showNoResultsPopup()
            } else {
                postAdapter.updatePosts(posts)
            }
        }

        postViewModel.loadingLiveData.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loadingTextv.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loadingTextv.visibility = View.GONE
            }
        }

        if (query.isNotEmpty()) {
            postViewModel.searchPosts(query)
        }
    }

    private fun showNoResultsPopup() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)

        val titleTextView = dialogView.findViewById<TextView>(R.id.customDialogTitle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.customDialogMessage)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("ٹھیک ہے") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
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
}
