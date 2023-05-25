package com.example.shopapp.view

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.databinding.FragmentCatalogBinding
import com.example.shopapp.databinding.ProductCategoriesScreenBinding
import com.example.shopapp.databinding.ProductsScreenBinding
import com.example.shopapp.model.dataClasses.Category
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.search.SearchView.TransitionState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.ceil


class CatalogFragment : Fragment() {

    private lateinit var binding: FragmentCatalogBinding
    private lateinit var viewModel: CatalogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        return binding.root
    }
}