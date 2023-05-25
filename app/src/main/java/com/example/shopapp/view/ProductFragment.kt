package com.example.shopapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentProductBinding
import com.google.android.material.carousel.CarouselLayoutManager

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonProduct.visibility = View.VISIBLE

        binding.backButtonProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_catalogListsFragment)
        }

        val adapterList: MutableList<Int> = mutableListOf(1, 2, 3)
        val myAdapter = CarouselRecyclerItem(adapterList, requireContext())
        binding.carouselRecyclerView.layoutManager = CarouselLayoutManager()
        binding.carouselRecyclerView.adapter = myAdapter
    }
}