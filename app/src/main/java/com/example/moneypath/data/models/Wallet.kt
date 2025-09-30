package com.example.moneypath.data.models

import com.google.firebase.database.Exclude


data class Wallet(
    val id: String = "",
    val name: String  = "",
    val type: WalletType = WalletType.Cash,
    @get:Exclude val balance: Double = 0.0,
    val source: WalletSource = WalletSource.Manual,

    val balanceEnc: String ="",
    val balanceIv: String = ""
)

enum class WalletType {
    Card, Cash
}

enum class WalletSource{
    Manual, Api
}
