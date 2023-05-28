package com.example.shopapp.model.dataClasses

data class ShoppingList(
    val id: String = "",
    val name: String = "",
    val products_id: List<String> = listOf(),
)
