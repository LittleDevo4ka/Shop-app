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
import com.google.firebase.storage.StorageReference
import kotlin.math.ceil

class CarouselRecyclerItem(private val mainList: List<StorageReference>,
                           private val context: Context, density: Float) :
    RecyclerView.Adapter<CarouselRecyclerItem.MyViewHolder>() {

    private val carouselTag = "CarouselRecyclerItem"
    private val imageHeight = ceil(196 * density).toInt()
    private val imageWidth = ceil(150 * density).toInt()
    private val glideOptions = RequestOptions()
        .override(imageWidth, imageHeight)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.carousel_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.carousel_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mainList[position].downloadUrl.addOnCompleteListener {
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
        return mainList.size
    }
}