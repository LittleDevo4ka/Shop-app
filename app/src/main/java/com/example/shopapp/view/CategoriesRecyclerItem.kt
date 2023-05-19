package com.example.shopapp.view

import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shopapp.R
import com.example.shopapp.model.dataClasses.Category
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.math.ceil

class CategoriesRecyclerItem(private val categoryList: List<Category>, private val context: Context,
                             private val ref: StorageReference, density: Float,
                             onClickListener: OnItemClickListener) :
    RecyclerView.Adapter<CategoriesRecyclerItem.MyViewHolder>() {

    private val categoriesTag = "CategoriesRecyclerItem"
    private val imageSize = ceil(56 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageSize, imageSize)

    private var mainListener: OnItemClickListener = onClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int, isCategory: Boolean)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.category_card_title)
        val cardImage: ImageView = itemView.findViewById(R.id.category_card_image)
        val categoryCardButton: Button = itemView.findViewById(R.id.category_card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.category_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.categoryCardButton.setOnClickListener{
            mainListener.onItemClick(position, true)
        }

        holder.cardTitle.text = categoryList[position].name

        if (categoryList[position].image_link.isNotEmpty()) {
            val tempImg = ref.child(categoryList[position].image_link)
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

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}