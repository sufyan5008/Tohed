package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.adapters.HomeRecyclerViewAdapter
import com.tohed.islampro.databinding.FragmentHomeBinding
import com.tohed.islampro.viewModel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    // Create a reference to the adapter
    private lateinit var homeRecyclerViewAdapter: HomeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list initially
        homeRecyclerViewAdapter = HomeRecyclerViewAdapter(emptyList())
        binding.recyclerView.adapter = homeRecyclerViewAdapter

        // Observe the home page data from the ViewModel
        viewModel.homePageData.observe(viewLifecycleOwner) { page ->
            // Update the adapter when data changes
            homeRecyclerViewAdapter.updateData(listOf())  // listOf() if homePageData is a single page
        }

        // Load home page data
        viewModel.loadHomePageData()
    }
}
