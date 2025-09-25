package com.example.moneypath.data.models


data class Wallet(
    val id: String = "",
    val name: String  = "",
    val type: WalletType = WalletType.Cash,
    val balance: Double = 0.0,
    val source: WalletSource = WalletSource.Manual
)

enum class WalletType {
    Card, Cash
}

enum class WalletSource{
    Manual, Api
}
