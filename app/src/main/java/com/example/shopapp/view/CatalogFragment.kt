package com.example.shopapp.view

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.databinding.FragmentCatalogBinding
import com.example.shopapp.databinding.ProductCategoriesScreenBinding
import com.example.shopapp.viewModel.AccountViewModel
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.search.SearchView.TransitionState
import kotlinx.coroutines.launch
import kotlin.math.ceil


class CatalogFragment : Fragment() {

    private lateinit var binding: FragmentCatalogBinding
    private var ds: Float = 0f

    private lateinit var viewModel: CatalogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(layoutInflater, container, false)
        ds = resources.displayMetrics.density

        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getFragmnetNum().collect {
                    when(it) {
                        0 -> addProductCategoriesScreen()
                        1 -> addProductsScreen()
                        2 -> addProductScreen()
                        else -> Log.i(tag, "Oops, an unexpected fragmentNum: $it")
                    }
                }
            }
        }
    }

    private fun addProductCategoriesScreen() {
        val productCategoriesScreen = ProductCategoriesScreenBinding.inflate(
            layoutInflater,
            binding.placeForCatalog, false
        )

        val productCategoriesCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                productCategoriesScreen.searchViewCategories.hide()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(productCategoriesCallback)


        //Убираем заголовок Catalog, при открытии searchView и возвращем его обратно при его закрытии
        productCategoriesScreen.searchViewCategories.addTransitionListener { _, _, newState ->
            if (newState === TransitionState.SHOWING) {
                binding.wasteidCatalogTitleTv.visibility = View.GONE
                val params = (productCategoriesScreen.searchCoordinatorLayoutCategories.layoutParams
                        as ConstraintLayout.LayoutParams)
                params.topMargin = 0
                productCategoriesScreen.searchCoordinatorLayoutCategories.layoutParams = params

                productCategoriesCallback.isEnabled = true
            }
            if (newState === TransitionState.HIDING) {
                binding.wasteidCatalogTitleTv.visibility = View.VISIBLE
                val params = (productCategoriesScreen.searchCoordinatorLayoutCategories.layoutParams
                        as ConstraintLayout.LayoutParams)
                params.topMargin = ceil(24 * ds).toInt()
                productCategoriesScreen.searchCoordinatorLayoutCategories.layoutParams = params

                productCategoriesCallback.isEnabled = false
            }
        }

        val strList: MutableList<String> = mutableListOf()
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.simple_list_item_1, strList)

        productCategoriesScreen.placeForSearchSuggestionCategories.adapter = arrayAdapter

        //слушатель нажатий на элементы listView
        productCategoriesScreen.placeForSearchSuggestionCategories.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                productCategoriesScreen.searchViewCategories.clearFocusAndHideKeyboard()
                productCategoriesScreen.searchViewCategories.hide()


                productCategoriesScreen.searchBarCategories.text = strList[0]

                strList.clear()
                arrayAdapter.notifyDataSetChanged()
            }

        //добавляем слушатель ввода текста в searchView, чтобы изменить элемент listView
        productCategoriesScreen.searchViewCategories.editText
            .doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    strList.clear()
                    strList.add(text.toString())

                    arrayAdapter.notifyDataSetChanged()
                }
        }

        val adapterList: MutableList<CatalogViewModel.Category> = mutableListOf()
        val myAdapter = CategoriesRecyclerItem(adapterList, requireContext())
        productCategoriesScreen.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        productCategoriesScreen.recyclerViewCategories.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getCategoryList().collect {
                    adapterList.clear()
                    adapterList.addAll(it)

                    myAdapter.notifyDataSetChanged()
                }
            }
        }


        binding.placeForCatalog.addView(productCategoriesScreen.root)

    }

    private fun addProductsScreen() {

    }

    private fun addProductScreen() {

    }
}