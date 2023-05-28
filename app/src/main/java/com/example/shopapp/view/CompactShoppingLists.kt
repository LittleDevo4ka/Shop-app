package com.example.shopapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shopapp.R
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.ShoppingList
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class CompactShoppingLists (private val shoppingLists: List<ShoppingList>, private val context: Context,
                            private val ref: StorageReference, density: Float,
                            onClickListener: OnItemClickListener):
    RecyclerView.Adapter<CompactShoppingLists.MyViewHolder>()  {

    private val categoriesTag = "CompactShoppingLists"
    private val imageSize = ceil(56 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageSize, imageSize)

    private var mainListener: OnItemClickListener = onClickListener

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.name_small_shopping_card)
        val cardImage: ImageView = itemView.findViewById(R.id.image_small_shopping_card)
        val cardCheckBox: CheckBox = itemView.findViewById(R.id.checkbox_small_shopping_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.small_shopping_list_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            mainListener.onItemClick(shoppingLists[position].id, isChecked)
        }

        holder.cardTitle.text = shoppingLists[position].name
    }

    override fun getItemCount(): Int {
        return shoppingLists.size
    }
}