package com.example.shopapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopapp.R
import com.example.shopapp.viewModel.CatalogViewModel

class CategoriesRecyclerItem(private val categoryList: List<CatalogViewModel.Category>, private val context: Context) :
    RecyclerView.Adapter<CategoriesRecyclerItem.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.category_card_title)
        val cardImage: ImageView = itemView.findViewById(R.id.category_card_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.category_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = categoryList[position].name

        Glide.with(context).load(categoryList[position].image)
                .into(holder.cardImage)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}