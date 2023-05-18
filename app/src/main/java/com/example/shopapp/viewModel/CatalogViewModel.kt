package com.example.shopapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CatalogViewModel: ViewModel() {

    private val categoryList = mutableListOf<Category>()
    private val categoryMutableFlow: MutableStateFlow<List<Category>> = MutableStateFlow(categoryList.toList())
    val categoryStateFlow: StateFlow<List<Category>> = categoryMutableFlow

    private val productList = mutableListOf<Product>()
    private val productMutableFlow: MutableStateFlow<List<Product>> = MutableStateFlow(productList.toList())
    val productStateFlow: StateFlow<List<Product>> = productMutableFlow

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)

    val db: FirebaseFirestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    data class Category(
        val id: Int = -1,
        val name: String = "",
        val image_link: String = ""
    )
    data class Product(
        val name: String,
        val price: Double,
        val description: String,
        val image: String
    )

    init {
        db = Firebase.firestore

        val docRef = db.collection("categories")
        docRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    val list = it.toObjects(Category::class.java)
                    for (i in 0 until list.size) {
                        if (list[i] != null) {
                            categoryList.add(list[i])
                        }
                    }
                    categoryMutableFlow.value = categoryList
                }
            }
            .addOnFailureListener {
                Log.i("Error", it.message.toString())
            }
    }

    fun getFragmentNum(): MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(value: Int) {
        fragmentNum.value = value
    }
}