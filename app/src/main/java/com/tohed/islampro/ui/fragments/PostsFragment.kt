package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.adapters.PostAdapter
import com.tohed.islampro.databinding.FragmentGamesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.viewModel.PostViewModel

class PostsFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private var isLoading = false

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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager?.childCount ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val firstVisibleItemPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    // Check if the user is scrolling up
                    if (dy < 0) {
                        binding.seeAll.visibility = View.GONE
                    }

                    // Check if the user is scrolling down and is at the end of the list
                    if (dy > 0 && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) {
                        binding.seeAll.visibility = View.VISIBLE
                    }
                }
            })
        }

        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
            binding.progressBar.visibility = View.GONE
            binding.seeAll.visibility = View.GONE
            isLoading = false
        }

        binding.seeAll.setOnClickListener {
            fetchPosts()
        }

        fetchPosts() // Initial fetch
    }

    private fun fetchPosts() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
        postViewModel.fetchPosts()
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle().apply {
            putLong("postId", postId)
        }
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_gamesFragment_to_postDetailsFragment, args)
    }
}



/*class PostsFragment : Fragment() {

    private lateinit var binding: FragmentGamesBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var networkReceiver: NetworkReceiver
    private var isLoading = false
    private var hasMorePosts = true // Flag to indicate if more posts are available


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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoading && isLastItemVisible()) {
                        showLoadMoreButton()
                    }
                }
            })
        }
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            postAdapter.updatePosts(posts)
            binding.progressBar.visibility = View.GONE
            isLoading = false

        }
        fetchPosts()
        //postViewModel.fetchPosts()
        binding.seeAll.setOnClickListener {
            //startActivity(Intent(requireContext(), AllPostsActivity::class.java))
            fetchPosts()

        }
    }

    *//*override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(networkReceiver)
    }*//*
    private fun isLastItemVisible(): Boolean {
        val layoutManager = binding.matches.layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        return lastVisibleItemPosition >= postAdapter.itemCount - 1
    }

    private fun showLoadMoreButton() {
        binding.seeAll.visibility = View.VISIBLE
        fetchPosts()
    }

    private fun fetchPosts() {
        if (isLoading) return
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
        binding.seeAll.visibility = View.GONE

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
}*/

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
