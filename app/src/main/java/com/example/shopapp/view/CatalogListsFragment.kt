package com.example.shopapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentCatalogListsBinding
import com.example.shopapp.databinding.ProductCategoriesScreenBinding
import com.example.shopapp.databinding.ProductsScreenBinding
import com.example.shopapp.model.dataClasses.Category
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.search.SearchView
import kotlinx.coroutines.launch
import kotlin.math.ceil

class CatalogListsFragment : Fragment(), CategoriesRecyclerItem.OnItemClickListener {

    private lateinit var binding: FragmentCatalogListsBinding
    private lateinit var viewModel: CatalogViewModel

    private var ds: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCatalogListsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        ds = resources.displayMetrics.density

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayoutCatalog.setOnRefreshListener {
            updateCatalog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getFragmentNum().collect {
                    when(it) {
                        0 -> {
                            addProductCategoriesScreen()
                            viewModel.updateCatalog()
                        }
                        1 -> {
                            addProductsScreen()
                            viewModel.getProducts()
                        }
                        else -> Log.i(tag, "Oops, an unexpected fragmentNum: $it")
                    }
                }
            }
        }
    }


    private fun addProductCategoriesScreen() {
        binding.swipeRefreshLayoutCatalog.isRefreshing = true

        val productCategoriesScreen = ProductCategoriesScreenBinding.inflate(
            layoutInflater,
            binding.placeForCatalog, false
        )

        val productCategoriesCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.searchViewCatalog.hide()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(productCategoriesCallback)


        //Убираем заголовок Catalog, при открытии searchView и возвращем его обратно при его закрытии
        binding.searchViewCatalog.addTransitionListener { _, _, newState ->
            if (newState === SearchView.TransitionState.SHOWING) {
                binding.wasteidCatalogTitleTv.visibility = View.GONE
                val params = (binding.searchCoordinatorLayoutCatalog.layoutParams
                        as ConstraintLayout.LayoutParams)
                params.topMargin = 0
                binding.searchCoordinatorLayoutCatalog.layoutParams = params

                productCategoriesCallback.isEnabled = true
            }
            if (newState === SearchView.TransitionState.HIDING) {
                binding.wasteidCatalogTitleTv.visibility = View.VISIBLE
                val params = (binding.searchCoordinatorLayoutCatalog.layoutParams
                        as ConstraintLayout.LayoutParams)
                params.topMargin = ceil(24 * ds).toInt()
                binding.searchCoordinatorLayoutCatalog.layoutParams = params

                productCategoriesCallback.isEnabled = false
            }
        }

        val strList: MutableList<String> = mutableListOf()
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, strList)

        binding.placeForSearchSuggestionCatalog.adapter = arrayAdapter

        //слушатель нажатий на элементы listView
        binding.placeForSearchSuggestionCatalog.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                binding.searchViewCatalog.clearFocusAndHideKeyboard()
                binding.searchViewCatalog.hide()


                binding.searchBarCatalog.text = strList[0]

                strList.clear()
                arrayAdapter.notifyDataSetChanged()
            }

        //добавляем слушатель ввода текста в searchView, чтобы изменить элемент listView
        binding.searchViewCatalog.editText
            .doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    strList.clear()
                    strList.add(text.toString())

                    arrayAdapter.notifyDataSetChanged()
                }
            }

        val adapterList: MutableList<Category> = mutableListOf()
        val myAdapter = CategoriesRecyclerItem(adapterList, requireContext(), viewModel.storageRef,
            resources.displayMetrics.density, this)
        productCategoriesScreen.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        productCategoriesScreen.recyclerViewCategories.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.categoryStateFlow.collect {
                    if (binding.swipeRefreshLayoutCatalog.isRefreshing && it != null) {
                        binding.swipeRefreshLayoutCatalog.isRefreshing = false
                    }

                    adapterList.clear()

                    if (it != null) {
                        adapterList.addAll(it)
                    }

                    myAdapter.notifyDataSetChanged()
                }
            }
        }


        binding.placeForCatalog.addView(productCategoriesScreen.root)

    }

    private fun addProductsScreen() {
        binding.swipeRefreshLayoutCatalog.isRefreshing = true

        binding.backButtonCatalog.setOnClickListener {
            binding.backButtonCatalog.visibility = View.GONE
            viewModel.setFragmentNum(0)
        }
        binding.backButtonCatalog.visibility = View.VISIBLE

        val productCategoriesScreen = ProductsScreenBinding.inflate(
            layoutInflater,
            binding.placeForCatalog, false
        )

        val adapterList: MutableList<Product> = mutableListOf()
        val myAdapter = ProductsRecyclerItem(adapterList, requireContext(), viewModel.storageRef,
            resources.displayMetrics.density, this)
        productCategoriesScreen.recyclerViewProducts.layoutManager =
            GridLayoutManager(requireContext(), 2)

        productCategoriesScreen.recyclerViewProducts.adapter = myAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.productsStateFlow.collect{productsList ->
                    if (binding.swipeRefreshLayoutCatalog.isRefreshing && productsList != null) {
                        binding.swipeRefreshLayoutCatalog.isRefreshing = false
                    }

                    adapterList.clear()

                    if (productsList != null) {
                        if (productsList.isEmpty()) {

                            if (binding.noResultsTv.isGone) {
                                binding.noResultsTv.visibility = View.VISIBLE
                            }

                        } else if (binding.noResultsTv.isVisible) {
                            binding.noResultsTv.visibility = View.GONE
                        }
                        adapterList.addAll(productsList)
                    }

                    myAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.placeForCatalog.addView(productCategoriesScreen.root)
    }

    override fun onItemClick(id: Int, isCategory: Boolean) {
        if (isCategory) {
            viewModel.setCategoryId(id)

            binding.placeForCatalog.removeAllViews()
            addProductsScreen()
        } else {
            findNavController().navigate(R.id.action_catalogListsFragment_to_productFragment)
        }
    }

    private fun updateCatalog() {
        if (viewModel.getCategoryId() != -1) {
            viewModel.getProducts()
        } else {
            viewModel.updateCatalog()
        }
    }
}