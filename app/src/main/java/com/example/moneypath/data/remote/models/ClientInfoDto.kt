package com.example.moneypath.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class ClientInfoDto(
    val accounts: List<AccountDto>
)

@Serializable
data class AccountDto(
    val id: String,
    val balance: Int,
    val currencyCode: Int
)
