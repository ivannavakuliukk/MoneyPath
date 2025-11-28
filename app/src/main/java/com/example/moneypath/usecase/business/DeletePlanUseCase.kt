package com.example.moneypath.usecase.business

import com.example.moneypath.data.local.PrefsHelper
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class DeletePlanUseCase @Inject constructor(
    private val repository: FirebaseRepository,
    private val helper: PrefsHelper
) {
    suspend operator fun invoke(){
        repository.deleteUserPlanning()
        helper.clearAll()
        repository.deleteTransactionsByCategory("goal")
    }
}