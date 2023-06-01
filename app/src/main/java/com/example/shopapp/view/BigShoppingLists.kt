package com.example.shopapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shopapp.R
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.ShoppingList
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class BigShoppingLists(private val shoppingLists: List<ShoppingList>, private val context: Context,
                       private val ref: StorageReference, density: Float,
                       onClickListener: OnItemClickListener):
    RecyclerView.Adapter<BigShoppingLists.MyViewHolder>() {

    private val tag = "BigShoppingLists"
    private val imageWidth = ceil(152 * density).toInt()
    private val imageHeight = ceil(144 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageWidth, imageHeight)

    private var mainListener: OnItemClickListener = onClickListener

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.shopping_list_card_title)
        val cardImage: ImageView = itemView.findViewById(R.id.shopping_list_card_image)
        val cardButton: MaterialCardView = itemView.findViewById(R.id.shopping_list_card_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.shopping_list_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = shoppingLists[position].name
        holder.cardButton.setOnClickListener {
            mainListener.onItemClick(shoppingLists[position])
        }

        if (shoppingLists[position].image_link != "default") {
            val tempImg = ref.child(shoppingLists[position].image_link)
            tempImg.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null) {
                        Glide.with(context).load(it.result).apply(glideOptions)
                            .placeholder(R.drawable.placeholder)
                            .into(holder.cardImage)
                    }
                }
            }
        } else {
            holder.cardImage.setImageResource(R.drawable.placeholder)
        }
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}