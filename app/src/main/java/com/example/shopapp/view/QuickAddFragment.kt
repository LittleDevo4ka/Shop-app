package com.example.shopapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentQuickAddBinding
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.model.dataClasses.ShoppingList
import com.example.shopapp.viewModel.CatalogViewModel


class QuickAddFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentQuickAddBinding
    private lateinit var viewModel: CatalogViewModel
    private var density: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQuickAddBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        density = resources.displayMetrics.density

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonQuickAdd.setOnClickListener {
            findNavController().navigate(R.id.action_quickAddFragment_to_productFragment)
        }

        val adapterList: MutableList<ShoppingList> = mutableListOf()
        val myAdapter = CompactShoppingLists(adapterList, requireContext(), viewModel.storageRef,
        density, this)
        binding.compactShoppingListsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.compactShoppingListsRecyclerView.adapter = myAdapter
    }

    override fun onItemClick(id: Int) {

    }

    override fun onItemClick(id: String) {

    }

    override fun onItemClick(id: String, isChecked: Boolean) {

    }
}