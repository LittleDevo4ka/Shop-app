package com.example.shopapp.model

import android.content.Context
import android.util.Log
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.example.shopapp.BuildConfig
import com.example.shopapp.model.dataClasses.DBShoppingList
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.model.dataClasses.ShoppingList
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class Repository {
    private val tag = "Repository"

    private val auth = Firebase.auth
    val database = Firebase.database(BuildConfig.Database_URL).reference
    private val currentUserMutableFlow: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)
    val currentUserStateFlow: StateFlow<FirebaseUser?> = currentUserMutableFlow

    init {
        if (auth.currentUser == null) {
            signInAnonymously()
        } else {
            currentUserMutableFlow.value = auth.currentUser
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUserMutableFlow.value = auth.currentUser
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return currentUserMutableFlow.value
    }

    fun addItemIntoShoppingList(shoppingList: ShoppingList, product: Product) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                val shoppingListRef = database.child("users").child(currentUser.uid)
                    .child(shoppingList.id)
                val productsListRef = shoppingListRef.child("products_id")

                if (shoppingList.products_id[0] == "-1") {
                    val productList = listOf(product.id)
                    productsListRef.setValue(productList)
                    shoppingListRef.child("image_link")
                        .setValue("products/${product.id}/1.jpg")
                } else {
                    productsListRef.child(shoppingList.products_id.size.toString())
                        .setValue(product.id)
                }
            }
        }
    }

    fun deleteItemFromShoppingList(shoppingList: ShoppingList, product: Product) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                val shoppingListRef = database.child("users").child(currentUser.uid)
                    .child(shoppingList.id)
                val productsListRef = shoppingListRef.child("products_id")

                if (shoppingList.products_id.size == 1) {
                    val productList = listOf(-1)
                    productsListRef.setValue(productList)
                    shoppingListRef.child("image_link").setValue("default")
                } else if (shoppingList.products_id[0] != product.id) {
                    val tempProductId = shoppingList.products_id.indexOf(product.id)
                    productsListRef.child(tempProductId.toString()).removeValue()
                } else {
                    productsListRef.child("0").removeValue()
                    if (product.id.isNotEmpty()) {
                        val db: FirebaseFirestore = Firebase.firestore
                        val docRef = db.collection("products")
                            .document(product.id)

                        docRef.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val tempProduct = task.result.toObject(Product::class.java)
                                    if (tempProduct != null) {
                                        Log.w(tag, "getProduct: the product has been received")
                                        tempProduct.id = task.result.id
                                        shoppingListRef.child("image_link")
                                            .setValue("products/${tempProduct.image_link}/1.jpg")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.w(tag, "getProduct: error, unable to get the product")
                            }
                    }
                }
            }
        }
    }

    private fun getProduct(productId: String) {
        if (productId.isNotEmpty()) {
            val db: FirebaseFirestore = Firebase.firestore
            val docRef = db.collection("products")
                .document(productId)

            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val tempProduct = task.result.toObject(Product::class.java)
                        if (tempProduct != null) {
                            Log.w(tag, "getProduct: the product has been received")
                            tempProduct.id = task.result.id
                        }
                    }
                }
                .addOnFailureListener {
                    Log.w(tag, "getProduct: error, unable to get the product")
                }
        }
    }



    fun getShoppingLists(mutableShoppingLists: MutableStateFlow<List<ShoppingList>?>){
        mutableShoppingLists.value = null
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                let {
                    database.child("users").child(currentUser.uid)
                        .get().addOnCompleteListener {  task ->
                            if (task.isSuccessful) {

                                val shoppingLists: MutableList<ShoppingList> = mutableListOf()
                                val shoppingListsStr = task.result.value.toString()
                                    .removePrefix("{")
                                    .removeSuffix("}}").split("}, ")
                                for (i in shoppingListsStr.indices) {
                                    val key = shoppingListsStr[i].substringBefore("=")
                                    var shoppingData = shoppingListsStr[i].removeRange(0, key.length+2)

                                    val imageLink = shoppingData.substringBefore(", ").substringAfter("image_link=")
                                    shoppingData = shoppingData.removeRange(0, imageLink.length + 13)

                                    val productsIdStr = shoppingData.substringBefore("], ").substringAfter("products_id=[")
                                    val productsIds = productsIdStr.split(", ")
                                    val productsIdList: MutableList<String> = mutableListOf()
                                    for (j in productsIds.indices) {
                                        productsIdList.add(productsIds[j])
                                    }
                                    shoppingData = shoppingData.removeRange(0, productsIdStr.length + 16)

                                    val name = shoppingData.removeRange(0, 5)

                                    val tempShoppingList = ShoppingList(key, name, imageLink, productsIdList)
                                    shoppingLists.add(tempShoppingList)
                                    mutableShoppingLists.value = shoppingLists.toList()
                                }
                            }
                        }
                }
            }

        }
    }

    fun createShoppingList(listName: String, textLayout: TextInputLayout?, alertDialog: AlertDialog) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                database.child("users").child(currentUser.uid)
                    .orderByChild("name")
                    .equalTo(listName)
                    .limitToFirst(1)
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result.value == null) {
                                val key = database.child("users").child(currentUser.uid).push().key
                                if (key == null) {
                                    Log.w(tag, "createShoppingList: key equals null")
                                    return@addOnCompleteListener
                                }

                                val dbShoppingList = DBShoppingList(listName, "default", listOf("-1"))
                                val dbObj = dbShoppingList.getMap()

                                val childUpdates = hashMapOf<String, Any>(
                                    "/users/${currentUser.uid}/$key" to dbObj
                                )

                                database.updateChildren(childUpdates)
                                if (alertDialog.isShowing) {
                                    alertDialog.dismiss()
                                }
                            } else {
                                if (alertDialog.isShowing) {
                                    textLayout?.error =
                                        "Error: A shopping list with that name already exists"
                                }
                            }

                            /*
                            val key = task.result.value.toString().substringBefore("=").substring(1)
                            val name = task.result.child("$key/name").getValue(String::class.java)
                             */
                        }
                    }
            }
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                database.child("users").child(currentUser.uid).child(shoppingList.id).removeValue()
            }
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(context: Context): Repository {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Repository()
                INSTANCE = instance
                return instance
            }
        }
    }
}