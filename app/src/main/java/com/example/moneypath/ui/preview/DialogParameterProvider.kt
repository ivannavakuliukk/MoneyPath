package com.example.moneypath.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.moneypath.R

data class DialogParams(
    val image: Int?,
    val title: String,
    val message: String,
    val confirmText: String,
    val dismissText: String
)

class DialogParameterProvider : PreviewParameterProvider<DialogParams>{
    override val values = sequenceOf(
        DialogParams(
            image = R.drawable.happy_bunny,
            title = "Вітаємо!!",
            message = "Ви виконали поставлену ціль, хоч час ще залишився! Хочете створити новий план чи залишитись на поточному?",
            confirmText = "Поточний",
            dismissText = "Новий план"
        ),
        DialogParams(
            image = R.drawable.sad_cat,
            title = "На жаль...",
            message = "Ви не виконали поставлену ціль, а час плану вже минув. Давайте створимо новий?",
            confirmText = "Зрозуміло",
            dismissText = "Новий план",
        )
    )
}