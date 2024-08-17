package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.ExcerptAdapter
import com.tohed.islampro.databinding.FragmentExcerptBinding
import com.tohed.islampro.datamodel.Post

class ExcerptFragment : Fragment() {

    private lateinit var binding: FragmentExcerptBinding
    private lateinit var posts: Array<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcerptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the posts and title from the arguments
        arguments?.let {
            posts = it.getParcelableArray("posts") as Array<Post>
            val categoryTitle = it.getString("categoryTitle")
            binding.excerptTitleTV.text = categoryTitle
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = ExcerptAdapter(posts.toList()) { post ->
            navigateToPostDetails(post.id.toLong())
        }

    }

    private fun navigateToPostDetails(postId: Long) {
        val bundle = Bundle().apply {
            putLong("postId", postId)
        }
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_excerptFragment_to_postDetailsFragment, bundle)
    }
}
