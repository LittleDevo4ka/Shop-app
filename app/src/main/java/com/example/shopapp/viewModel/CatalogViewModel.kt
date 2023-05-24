package com.example.shopapp.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.shopapp.model.Repository
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    private val categoryList = mutableListOf<Category>()
    private val categoryMutableFlow: MutableStateFlow<List<Category>?> = MutableStateFlow(null)
    val categoryStateFlow: StateFlow<List<Category>?> = categoryMutableFlow

    private var productsList = mutableListOf<Product>()
    private val productsMutableFlow: MutableStateFlow<List<Product>?> = MutableStateFlow(null)
    val productsStateFlow: StateFlow<List<Product>?> = productsMutableFlow

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)

    val db: FirebaseFirestore = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    init {
        repository = Repository.getRepository()

        viewModelScope.launch {
            repository.currentUserStateFlow.collect{ currentUser ->
                if (currentUser != null) {
                    getCategories()
                }
            }
        }
    }

    private fun getCategories() {
        categoryList.clear()
        categoryMutableFlow.value = null

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
                    categoryMutableFlow.value = categoryList.toList()
                }
            }
            .addOnFailureListener {
                Log.i("Error", it.message.toString())
            }
    }

    fun updateCatalog() {
        if (repository.getCurrentUser() != null) {
            getCategories()
        }
    }

    fun getProducts(categoryId: Int) {
        productsList.clear()
        productsMutableFlow.value = null

        val docRef = db.collection("products")
            .whereEqualTo("category_id", categoryId)

        docRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tempProductsList = task.result.toObjects(Product::class.java)
                    for (i in 0 until tempProductsList.size) {
                        if (tempProductsList[i] != null) {
                            productsList.add(tempProductsList[i])
                        }
                    }
                    productsMutableFlow.value = productsList.toList()
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