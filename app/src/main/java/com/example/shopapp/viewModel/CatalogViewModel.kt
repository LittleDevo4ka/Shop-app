package com.example.shopapp.viewModel

import android.app.Application
import android.icu.text.CaseMap.Title
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.shopapp.model.Repository
import com.example.shopapp.model.dataClasses.Category
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.model.dataClasses.ShoppingList
import com.example.shopapp.model.dataClasses.TitleVisibility
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
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

    private val tag: String = "CatalogViewModel"

    private val repository: Repository

    private val categoryList = mutableListOf<Category>()
    private val categoryMutableFlow: MutableStateFlow<List<Category>?> = MutableStateFlow(null)
    val categoryStateFlow: StateFlow<List<Category>?> = categoryMutableFlow

    private var productsList = mutableListOf<Product>()
    private val productsMutableFlow: MutableStateFlow<List<Product>?> = MutableStateFlow(null)
    val productsStateFlow: StateFlow<List<Product>?> = productsMutableFlow

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)

    private val mutableProduct: MutableStateFlow<Product?> = MutableStateFlow(null)
    val stateProduct: StateFlow<Product?> = mutableProduct
    private val mutableCategory: MutableStateFlow<Category?> = MutableStateFlow(null)
    val stateCategory: StateFlow<Category?> = mutableCategory

    private val mutableShoppingLists: MutableStateFlow<List<ShoppingList>?> = MutableStateFlow(null)
    val stateShoppingLists: StateFlow<List<ShoppingList>?> = mutableShoppingLists

    private var categoryId: Int = -1
    private var productId: String = ""

    val db: FirebaseFirestore = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    init {
        repository = Repository.getRepository(getApplication())
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

    fun getProducts() {
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
                            tempProductsList[i].id = task.result.documents[i].id
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

    fun setFragmentNum(num: Int) {
        fragmentNum.value = num
    }

    fun setCategoryId(id: Int) {
        categoryId = id
        fragmentNum.value = 1
    }

    fun getCategoryId(): Int {
        return categoryId
    }

    fun setProductId(id: String) {
        productId = id
        getProduct()
    }

    fun getProduct() {
        if (productId.isNotEmpty()) {
            mutableProduct.value = null
            val docRef = db.collection("products")
                .document(productId)

            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val tempProduct = task.result.toObject(Product::class.java)
                        if (tempProduct != null) {
                            Log.w(tag, "getProduct: the product has been received")
                            tempProduct.id = task.result.id
                            mutableProduct.value = tempProduct
                        }
                    }
                }
                .addOnFailureListener {
                    Log.w(tag, "getProduct: error, unable to get the product")
                }
        }

    }

    fun getCategory(id: Int) {
        mutableCategory.value = null
        val docRef = db.collection("categories")
            .whereEqualTo("id", id)

        docRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    val list = it.toObjects(Category::class.java)
                    for (i in 0 until list.size) {
                        if (list[i] != null) {
                            mutableCategory.value = list[i]
                            break
                        }
                    }
                }
            }
    }

    fun createShoppingList(listName: String, textLayout: TextInputLayout?, alertDialog: AlertDialog) {
        repository.createShoppingList(listName, textLayout, alertDialog)
    }

    fun getShoppingLists(){
        return repository.getShoppingLists(mutableShoppingLists)
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        repository.deleteShoppingList(shoppingList)
    }

    fun addItemIntoShoppingList(shoppingList: ShoppingList, product: Product) {
        repository.addItemIntoShoppingList(shoppingList, product)
        Log.i(tag, "item added")
    }

    fun deleteItemFromShoppingList(shoppingList: ShoppingList, product: Product) {
        repository.deleteItemFromShoppingList(shoppingList, product)
        Log.i(tag, "item deleted")
    }

    fun getRealtimeDatabase(): DatabaseReference {
        return repository.database
    }
}