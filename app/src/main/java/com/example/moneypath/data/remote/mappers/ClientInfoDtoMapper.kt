package com.example.moneypath.data.remote.mappers

import com.example.moneypath.data.remote.models.ClientInfoDto
import com.example.moneypath.domain.models.Account

fun ClientInfoDto.toDomain(): Account {
    return accounts.first().let { dto ->
        Account(
            balance = dto.balance,
            currencyCode = dto.currencyCode
        )
    }
}
