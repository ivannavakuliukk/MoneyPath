package com.example.moneypath.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.moneypath.R
import com.example.moneypath.data.models.PieSlice
import kotlin.collections.listOf

class DonutChartParamsProvider: PreviewParameterProvider<Pair<List<PieSlice>, Int>> {
    override val values = sequenceOf(
        Pair(
            listOf(
                PieSlice(
                    value = 1000.0,
                    color = Color(0xFFD747FF),
                    R.drawable.category_cafe
                ),
                PieSlice(
                    value = 200.0,
                    color = Color(0xFF5EF721),
                    R.drawable.category_travel
                ),
                PieSlice(
                    value = 2000.0,
                    color = Color(0xFFC7A486),
                    R.drawable.category_taxi
                ),
                PieSlice(
                    value = 2000.0,
                    color = Color(0xFFF462EE),
                    R.drawable.category_books
                )
            ),
            0
        ),
        Pair(
            listOf(
                PieSlice(
                    value = 200.0,
                    color = Color(0xFFD747FF),
                    R.drawable.category_cafe
                ),
                PieSlice(
                    value = 1000.0,
                    color = Color(0xFF5EF721),
                    R.drawable.category_travel
                ),
                PieSlice(
                    value = 100.0,
                    color = Color(0xFFC7A486),
                    R.drawable.category_taxi
                ),
                PieSlice(
                    value = 2000.0,
                    color = Color(0xFFF462EE),
                    R.drawable.category_books
                )
            ),
            1
        ),
        Pair(
            emptyList(),
            0
        ),
        Pair(
            emptyList(),
            1
        )
    )
}