package com.tohed.islampro.ui.fragments

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.PostAdapter
import com.tohed.islampro.databinding.FragmentGamesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.datamodel.Title
import com.tohed.islampro.ui.activities.AllPostsActivity
import com.tohed.islampro.utils.NetworkReceiver
import com.tohed.islampro.viewModel.PostViewModel


class PostsFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var networkReceiver: NetworkReceiver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        postAdapter = PostAdapter(emptyList()) { clickedPost ->
            handlePostItemClick(clickedPost)
        }
        binding.matches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
            binding.progressBar.visibility = View.GONE
        }
        fetchPosts()
        //postViewModel.fetchPosts()
        binding.seeAll.setOnClickListener {
            startActivity(Intent(requireContext(), AllPostsActivity::class.java))
        }
    }
    /*override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(networkReceiver)
    }*/

    private fun fetchPosts() {
        binding.progressBar.visibility = View.VISIBLE
        postViewModel.fetchPosts()
    }
    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle()
        args.putLong("postId", postId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_gamesFragment_to_postDetailsFragment, args)
    }
}

/*
class PostsFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Initialize the adapter
        postAdapter = PostAdapter(emptyList()) { clickedPost ->
            handlePostItemClick(clickedPost)
        }

        // Set adapter to RecyclerView
        binding.matches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Observe posts LiveData
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
        }

        // Fetch posts
        postViewModel.fetchPosts()

        binding.seeAll.setOnClickListener {
            startActivity(Intent(requireContext(), AllPostsActivity::class.java))
        }
    }

    // Handle post item click
    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle()
        args.putLong("postId", postId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_gamesFragment_to_postDetailsFragment, args)
    }
}
*/
