package com.example.shopapp.model.dataClasses

import com.google.firebase.database.Exclude

data class DBShoppingList(
    val name: String = "",
    val image_link: String = "",
    val products_id: List<String> = listOf()
) {
    @Exclude
    fun getMap(): Map<String, Any?>{
        return mapOf(
            "name" to name,
            "image_link" to image_link,
            "products_id" to products_id
        )
    }

}

data class DBShoppingListItem(
    val product_id: String = ""
)

data class ShoppingList(
    var id: String = "",
    var name: String = "",
    var image_link: String = "",
    var products_id: List<String> = listOf()
)
