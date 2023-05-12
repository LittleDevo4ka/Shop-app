package com.example.shopapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)
    private val saveInfo: SharedPreferences

    private val auth: FirebaseAuth = Firebase.auth

    init {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fragmentNum.value = 3
        }

        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)

    }

    fun getFragmentNum() : MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(tempFragmentNum: Int) {
        fragmentNum.value = tempFragmentNum
    }

    fun createUserWithEmailAndPassword(email: String, password: String): String {
        var resultString= ""

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(getApplication(), "The user has been successfully created.",
                    Toast.LENGTH_SHORT).show()

                    fragmentNum.value = 3
                } else {
                    Toast.makeText(getApplication(), "Error",
                        Toast.LENGTH_SHORT).show()
                    resultString = "Error"
                }
            }
        return resultString
    }

    fun signInWithEmailAndPassword(email: String, password: String): String {
        var resultString = ""

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(getApplication(), "Sign in was completed successfully",
                        Toast.LENGTH_SHORT).show()

                    fragmentNum.value = 3
                } else {
                    Toast.makeText(getApplication(), "Error",
                        Toast.LENGTH_SHORT).show()
                    resultString = "Error"
                }
            }
        return resultString
    }
}