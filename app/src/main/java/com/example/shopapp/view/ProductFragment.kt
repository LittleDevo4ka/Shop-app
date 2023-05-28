package com.example.shopapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentProductBinding
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private lateinit var viewModel: CatalogViewModel
    private var density = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        density = resources.displayMetrics.density

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayoutProduct.setOnRefreshListener {
            updateProduct()
        }
        binding.swipeRefreshLayoutProduct.isRefreshing = true

        binding.backButtonProduct.visibility = View.VISIBLE

        binding.backButtonProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_catalogListsFragment)
        }

        val adapterList: MutableList<StorageReference> = mutableListOf()
        val myAdapter = CarouselRecyclerItem(adapterList, requireContext(), density)
        binding.carouselRecyclerView.layoutManager = CarouselLayoutManager()
        binding.carouselRecyclerView.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateProduct.collect { product ->
                    if (product != null) {
                        adapterList.clear()

                        binding.productNameTv.text = product.name
                        binding.productDescriptionTv.text = product.description

                        viewModel.storageRef.child("/products/${product.id}/").listAll()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val tempLinks = task.result.items
                                    val links: MutableList<StorageReference> = mutableListOf()
                                    for (i in 0 until tempLinks.size) {
                                        if (tempLinks[i] != null) {
                                            links.add(tempLinks[i])
                                        }
                                    }
                                    adapterList.addAll(links)
                                    myAdapter.notifyDataSetChanged()
                                }
                            }

                        viewModel.getCategory(product.category_id)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateCategory.collect { category ->
                    if (category != null) {
                        binding.productCategoryTv.text = "Category: ${category.name}"

                        binding.swipeRefreshLayoutProduct.isRefreshing = false
                    }
                }
            }
        }

        binding.plusFabProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_quickAddFragment)
        }
    }

    private fun updateProduct() {
        viewModel.getProduct()
    }
}