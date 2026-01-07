package com.example.moneypath.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.moneypath.domain.models.TransactionType

class SegmentedButtonParamsProvider: PreviewParameterProvider<Pair<Color, TransactionType>>{
    override val values = sequenceOf(
        Pair(
            Color(0xFF55D6BE),
            TransactionType.Income
        ),
        Pair(
            Color(0xFFE46458),
            TransactionType.Expense
        ),
        Pair(
            Color(0xFF9EA7AD),
            TransactionType.Transfer
        )
    )

}