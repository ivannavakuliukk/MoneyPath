package com.example.moneypath.utils

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.moneypath.data.models.TransactionType


object ScreenSize {
    var width by mutableStateOf(0.dp)
    var height by mutableStateOf(0.dp)
}


fun TransactionType.toDisplayName():String{
    return when(this){
        TransactionType.Transfer -> "Переказ"
        TransactionType.Income -> "Дохід"
        TransactionType.Expense -> "Витрата"
    }
}

@Composable
fun TransactionType.backgroundColor(): androidx.compose.ui.graphics.Color {
    return when(this){
        TransactionType.Income -> MaterialTheme.colorScheme.background
        TransactionType.Transfer -> MaterialTheme.colorScheme.inverseSurface
        TransactionType.Expense -> MaterialTheme.colorScheme.surface
    }
}

fun TransactionType.displaySign(): String {
    return when(this){
        TransactionType.Income -> "+"
        TransactionType.Transfer -> ""
        TransactionType.Expense -> "-"
    }
}


val AppTextFieldColors
    @Composable get() = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
        focusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f),
        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f),
        cursorColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f),
        disabledTextColor = MaterialTheme.colorScheme.onPrimary,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
        disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
    )

val AppTextFieldColors2
    @Composable get() = TextFieldDefaults.colors(
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledIndicatorColor = MaterialTheme.colorScheme.primary,
        disabledTrailingIconColor = MaterialTheme.colorScheme.secondary,
        disabledTextColor = MaterialTheme.colorScheme.onPrimary
    )


// Слайд анімації
val slideInLeft: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(800)
    )
}

val slideOutRight: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(800)
    )
}
