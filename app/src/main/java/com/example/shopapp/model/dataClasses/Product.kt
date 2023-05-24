package com.example.shopapp.model.dataClasses
data class Product(
    val name: String = "",
    val cost: Double = 0.0,
    val description: String = "",
    val image_link: String = "",
    val manufacturer: String = "",
    val category_id: Int = -1
)
