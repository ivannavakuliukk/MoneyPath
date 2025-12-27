package com.example.moneypath.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
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

fun monthWordForm(number: Int): String {
    val n = number % 100
    return if (n in 11..19) {
        "місяців"
    } else {
        when (n % 10) {
            1 -> "місяць"
            2, 3, 4 -> "місяці"
            else -> "місяців"
        }
    }
}

fun transactionWordForm(number: Int): String {
    val n = number % 100
    return if (n in 11..19) {
        "транзакцій"
    } else {
        when (n % 10) {
            1 -> "транзакція"
            2, 3, 4 -> "транзакції"
            else -> "транзакцій"
        }
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

val GreenTextFieldColors
    @Composable get() = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
        focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
        cursorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
        focusedLabelColor = MaterialTheme.colorScheme.onTertiary
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

// Source - https://stackoverflow.com/a
// Posted by BennyG
// Retrieved 2025-11-09, License - CC BY-SA 4.0

fun Modifier.rotateVertically(clockwise: Boolean = true): Modifier {
    val rotate = rotate(if (clockwise) 90f else -90f)

    val adjustBounds = layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }
    return rotate then adjustBounds
}

@Composable
fun adaptivePadding(): Dp {
    val widthClass = LocalAppWindowInfo.current.windowSizeClass.windowWidthSizeClass
    return when (widthClass) {
        WindowWidthSizeClass.COMPACT -> 10.dp  // телефон
        WindowWidthSizeClass.MEDIUM  -> 20.dp  // середній екран
        WindowWidthSizeClass.EXPANDED -> 32.dp // планшет
        else -> 10.dp
    }
}

// Зберігаємо Metrics і SizeClass для всього додатку
data class AppWindowInfo(
    val windowSizeClass: WindowSizeClass
)

// CompositionLocal для доступу з будь-якого Composable
val LocalAppWindowInfo = staticCompositionLocalOf<AppWindowInfo> {
    error("No AppWindowInfo provided")
}
