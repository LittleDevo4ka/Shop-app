package com.example.shopapp.view

import android.content.Context
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
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class ShoppingListProduct(private val productsList: List<Product>, private val context: Context,
                          private val ref: StorageReference, density: Float,
                          onClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ShoppingListProduct.MyViewHolder>() {

    private val tag = "ShoppingListProduct"
    private val imageSize = ceil(56 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageSize, imageSize)

    private var mainListener: OnItemClickListener = onClickListener

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.shopping_list_product_card_title)
        val cardImage: ImageView = itemView.findViewById(R.id.shopping_list_product_card_image)
        val cardButton: MaterialButton =
            itemView.findViewById(R.id.shopping_list_product_card_button)
        val deleteButton: Button =
            itemView.findViewById(R.id.delete_button_shopping_list_product_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.shopping_list_product_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = productsList[position].name

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

        holder.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Remove a product from the list?")
                .setMessage(
                    "Are you sure you want to remove the product:" +
                            " ${productsList[position].name} from the list?"
                )
                .setNeutralButton("Cancel") { _, _ ->

                }
                .setPositiveButton("Okay") { _, _ ->
                    mainListener.deleteProductFromShoppingList(productsList[position])
                }
                .show()
        }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}