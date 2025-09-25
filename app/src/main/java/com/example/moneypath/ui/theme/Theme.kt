package com.example.moneypath.ui.theme


import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moneypath.R


private val LightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFF0F7173),
    tertiary = Color(0xFF2BB0FF),
    background = Color(0xFF55D6BE),
    surface = Color(0xFFFF6F61),
    onSurface = Color(0xFFE46458),
    inverseSurface = Color(0xFF9EA7AD),
    inverseOnSurface = Color(0xFF6E7C87),
    surfaceVariant = Color(0xFFE46458),
    onTertiary = Color(0xFF05AEA0)
)

object Inter {
    val Regular = FontFamily(Font(R.font.inter_regular))
    val Medium  = FontFamily(Font(R.font.inter_medium))
    val Bold    = FontFamily(Font(R.font.inter_bold))
    val SemiBold = FontFamily(Font(R.font.inter_semibold))
    val C_Bold = FontFamily(Font(R.font.chrimston_text_bold))
}

// Scale для шрифтів (від базового екрану)
@Composable
fun rememberScaleFactor(): Float {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val scale = screenWidth/360f
    Log.d("AdaptiveFont", "Screen width=${configuration.screenWidthDp}dp, scale=$scale")
    return scale
}

@Composable
fun AdaptiveTypography(): Typography {
    val scale = rememberScaleFactor()
    // контент
    return Typography(
        bodySmall = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (12*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (13*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        bodyLarge = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (14*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        // Маленькі надписи
        displaySmall = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (9*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        displayMedium = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (10*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        // Гроші
        headlineSmall = TextStyle(
            fontFamily = Inter.Medium,
            fontSize = (16*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        headlineLarge = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (50*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        // Заголовки
        titleSmall = TextStyle(
            fontFamily = Inter.Medium,
            fontSize = (14*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        titleMedium = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (15*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        titleLarge = TextStyle(
            fontFamily = Inter.Regular,
            fontSize = (16*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        labelSmall = TextStyle(
            fontFamily = Inter.Medium,
            fontSize = (15*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        labelMedium = TextStyle(
            fontFamily = Inter.Medium,
            fontSize = (16*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        labelLarge = TextStyle(
            fontFamily = Inter.SemiBold,
            fontSize = (16*scale).sp,
            fontWeight = FontWeight.Normal
        ),
        headlineMedium = TextStyle(
            fontFamily = Inter.C_Bold,
            fontSize = (30*scale).sp,
            fontWeight = FontWeight.Normal
        )
    )
}

@Composable
fun MoneyPathTheme(
    content: @Composable () -> Unit
) {
    // Використовуємо лише світлу кольорову схему
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdaptiveTypography(),
        content = content
    )
}