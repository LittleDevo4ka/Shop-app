package com.example.shopapp.model.dataClasses

interface OnItemClickListener{
    fun onItemClick(id: Int)
    fun onItemClick(id: String)
    fun onItemClick(id: String, isChecked: Boolean)
}