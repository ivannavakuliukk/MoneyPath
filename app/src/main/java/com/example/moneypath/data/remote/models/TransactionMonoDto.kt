package com.example.moneypath.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionMonoDto(
    val id: String,
    val amount: Int,
    val description: String,
    val mcc: Int,
    @SerialName("time")
    val date: Long
)


