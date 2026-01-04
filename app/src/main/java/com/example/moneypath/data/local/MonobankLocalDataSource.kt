package com.example.moneypath.data.local

import android.util.Log
import javax.inject.Inject

class MonobankLocalDataSource @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
)  {

    fun saveToken(token: String) {
        encryptedPrefsHelper.saveToken(token)
    }

    fun getToken(): String? {
        Log.d("LocalDataSource", "${encryptedPrefsHelper.getToken()}")
        return encryptedPrefsHelper.getToken()
    }

    fun clearToken() {
        encryptedPrefsHelper.removeToken()
    }
}
