package com.example.shopapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)
    private val saveInfo: SharedPreferences

    private var isLogged: Boolean

    init {
        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
        isLogged = saveInfo.getBoolean("isLogged", false)

        if(isLogged) fragmentNum.value = 3
    }

    fun getFragmentNum() : MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(tempFragmentNum: Int) {
        fragmentNum.value = tempFragmentNum

        if (tempFragmentNum == 3) {
            isLogged = true
            saveInfo.edit().putBoolean("isLogged", isLogged).apply()
        }
    }
}