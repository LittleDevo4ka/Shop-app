package com.example.shopapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shopapp.model.dataClasses.Category
import com.example.shopapp.model.dataClasses.Product
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

    private val productsList = mutableListOf<Product>()
    private val productsMutableFlow: MutableStateFlow<List<Product>> = MutableStateFlow(productsList.toList())
    val productsStateFlow: StateFlow<List<Product>> = productsMutableFlow

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)

    val db: FirebaseFirestore
    val storage = Firebase.storage
    val storageRef = storage.reference

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

    fun getProducts(categoryId: Int) {
        val docRef = db.collection("products").whereEqualTo("category_id", 1)
        docRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tempProductsList = task.result.toObjects(Product::class.java)
                    for (i in 0 until tempProductsList.size) {
                        if (tempProductsList[i] != null) {
                            productsList.add(tempProductsList[i])
                        }
                    }
                    productsMutableFlow.value = productsList
                }
            }
    }

    fun getFragmentNum(): MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(value: Int) {
        fragmentNum.value = value
    }
}