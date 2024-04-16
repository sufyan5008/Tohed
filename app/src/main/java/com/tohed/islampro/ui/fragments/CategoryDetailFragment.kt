package com.tohed.islampro.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.tohed.islampro.R
import com.tohed.islampro.databinding.FragmentCategoryDetailBinding
import com.tohed.islampro.viewModel.CategoryViewModel

class CategoryDetailFragment : Fragment() {

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentCategoryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categoryDetail.observe(viewLifecycleOwner) { categoryDetail ->
            // Update UI with category detail
            if (categoryDetail != null) {
                binding.titleTextView.text = categoryDetail.title
                binding.contentTextView.text = categoryDetail.content

            }
        }
        /*categoryId.let { id ->
            viewModel.getCategoryDetail(id).observe(viewLifecycleOwner) { categoryDetail ->
                // Update UI with category detail
                binding.titleTextView.text = categoryDetail.title
                binding.contentTextView.text = categoryDetail.content
            }
        }*/


    }
}
