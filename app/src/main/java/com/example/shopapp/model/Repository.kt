package com.example.shopapp.model

import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class Repository {

    private val auth = Firebase.auth
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



    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(): Repository {
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