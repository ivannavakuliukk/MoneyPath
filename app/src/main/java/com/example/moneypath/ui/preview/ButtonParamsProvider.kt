package com.example.moneypath.ui.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ButtonParamsProvider: PreviewParameterProvider<Triple<Color, Boolean, String>>{
    override val values = sequenceOf(
        Triple(
            Color(0xFF2BB0FF),
            true,
            "Зберегти"
        ),
        Triple(
            Color(0xFF2BB0FF),
            false,
            "Видалити"
        ),
        Triple(
            Color(0xFFFF6F61),
            true,
            "Видалити"
        )
    )

}