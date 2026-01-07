package com.example.moneypath.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class TopBarParamsProvider: PreviewParameterProvider<Pair<Long, Long>?>{
    // якщо nullable тип треба явно вказувати
    override val values = sequenceOf<Pair<Long, Long>?>(
        Pair(1764540000,1767132000),
        null,
        Pair(1761948000,	1764453600),
    )
}