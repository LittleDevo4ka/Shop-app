package com.example.shopapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shopapp.R
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class ProductsRecyclerItem(private val productsList: List<Product>, private val context: Context,
                           private val ref: StorageReference, density: Float,
                           onClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ProductsRecyclerItem.MyViewHolder>() {

    private val productsTag = "ProductsRecyclerItem"
    private val imageSize = ceil(56 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageSize, imageSize)
    private val mainListener = onClickListener

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.title_product_card)
        val cardCost: TextView = itemView.findViewById(R.id.secondary_title_product_card)
        val cardImage: ImageView = itemView.findViewById(R.id.image_product_card)
        val productCard: MaterialCardView = itemView.findViewById(R.id.product_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = productsList[position].name
        holder.cardCost.text = productsList[position].cost.toString()
        holder.productCard.setOnClickListener{
            mainListener.onItemClick(productsList[position].id)
        }

        val tempImg = ref.child("products/${productsList[position].id}/1.jpg")
        tempImg.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    Glide.with(context).load(it.result).apply(glideOptions)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.cardImage)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}