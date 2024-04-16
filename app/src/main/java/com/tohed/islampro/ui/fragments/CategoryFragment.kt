package com.tohed.islampro.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.api.CategoryApiService
import com.tohed.islampro.databinding.FragmentCategoryBinding
import com.tohed.islampro.viewModel.CategoryViewModel

class CategoryFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: CategoryViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

       // viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        // Assuming you have a click listener for the CardView in your layout
        binding.masnoonNimaz.setOnClickListener {
            val categoryId = 409 // Replace this with your actual category ID
            navigateToCategoryDetail(categoryId)
        }
    }

    private fun navigateToCategoryDetail(categoryId: Int) {
        //val action = CategoryFragmentDirections.actionCategoryFragmentToCategoryDetailFragment(categoryId)
        navController.navigate(R.id.action_categoryFragment_to_categoryDetailFragment)
    }
}