package com.example.shopapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopapp.model.Repository
import com.example.shopapp.model.dataClasses.ShoppingList
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "AccountViewModel"

    private val repository = Repository.getRepository(getApplication())

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)
    private val saveInfo: SharedPreferences

    private val mutableShoppingLists: MutableStateFlow<List<ShoppingList>?> = MutableStateFlow(null)
    val stateShoppingLists: StateFlow<List<ShoppingList>?> = mutableShoppingLists

    private val auth: FirebaseAuth = Firebase.auth

    private val wrong_password_or_email = "Error: invalid email or password"
    private val network_error = "Error: there is no internet connection"
    private val two_factor_auth_error = "Error: two-step authentication is required"
    private val email_alredy_in_use = "Error: the user already exists"
    private val weak_password = "Error: the password is too simple"
    private val user_not_found = "Error: user not found"

    private val storage = Firebase.storage
    val storageRef = storage.reference

    init {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                if (email.isNotEmpty()) {
                    fragmentNum.value = 3
                }
            }
        }

        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
    }

    fun getFragmentNum() : MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(tempFragmentNum: Int) {
        fragmentNum.value = tempFragmentNum
    }

    fun createUserWithEmailAndPassword(email: String, password: String,
    emailTextLayout: TextInputLayout, passwordTextLayout: TextInputLayout,
    repeatPasswordTextLayout: TextInputLayout) {

        val credential = EmailAuthProvider.getCredential(email, password)
        val currentUser = repository.getCurrentUser()

        if (currentUser == null) {
            repeatPasswordTextLayout.error = network_error
        } else {
            currentUser.linkWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        getApplication(), "The user has been successfully created.",
                        Toast.LENGTH_SHORT
                    ).show()

                    fragmentNum.value = 3
                }
            }.addOnFailureListener {
                try {
                    throw it
                } catch (e: FirebaseAuthUserCollisionException) {
                    emailTextLayout.error = email_alredy_in_use
                } catch (e: FirebaseAuthWeakPasswordException) {
                    passwordTextLayout.error = weak_password
                } catch (e: FirebaseNetworkException) {
                    repeatPasswordTextLayout.error = network_error
                } catch (e: Exception) {
                    Log.w(tag, "createUserWithEmailAndPassword: " + e.message)
                }
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String,
    emailTextLayout: TextInputLayout, passwordTextLayout: TextInputLayout) {

        val currentUser = repository.getCurrentUser()

        if (currentUser == null) {
            emailTextLayout.error = " "
            passwordTextLayout.error = network_error
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(getApplication(), "Sign in was completed successfully",
                            Toast.LENGTH_SHORT).show()

                        currentUser.delete()

                        fragmentNum.value = 3
                    }
                }
                .addOnFailureListener{
                    try {
                        throw it
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        emailTextLayout.error = " "
                        passwordTextLayout.error = wrong_password_or_email
                    } catch (e: FirebaseAuthMultiFactorException) {
                        emailTextLayout.error = ""
                        passwordTextLayout.error = two_factor_auth_error
                    } catch (e: FirebaseNetworkException) {
                        emailTextLayout.error = " "
                        passwordTextLayout.error = network_error
                    } catch (e: FirebaseAuthInvalidUserException) {
                        emailTextLayout.error = " "
                        passwordTextLayout.error = user_not_found
                    } catch (e: Exception) {
                        Log.w(tag, "signInWithEmailAndPassword: " + e.message)
                    }
                }
        }
    }

    fun getShoppingLists(){
        return repository.getShoppingLists(mutableShoppingLists)
    }
}