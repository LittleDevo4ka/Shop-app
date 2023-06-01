package com.example.shopapp.model.dataClasses

import android.widget.CheckBox
import android.widget.CompoundButton

interface OnItemClickListener{
    fun onItemClick(id: Int)
    fun onItemClick(id: String)
    fun onItemClick(isChecked: Boolean, shoppingList: ShoppingList)
    fun onItemClick(shoppingList: ShoppingList)

    fun deleteProductFromShoppingList(product: Product)
}