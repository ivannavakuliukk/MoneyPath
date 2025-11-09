package com.example.moneypath.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlanScreenViewModel @Inject constructor(private val repository: FirebaseRepository): ViewModel() {
    data class UiState(
        val isPlanned: Boolean = false,
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())
}