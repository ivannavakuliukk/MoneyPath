package com.example.moneypath.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneypath.R
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import androidx.compose.ui.unit.DpOffset
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer


@Composable
fun SingInScreen(onSignInClick: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }

    // Запуск анімацій при старті екрана
    LaunchedEffect(Unit) {
        startAnim = true
    }

    //  1. Анімація появи логотипа
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    // 2. Пульсуючий слоган
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val textScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    //  3. Поява кнопки
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "btnFade"
    )



    SoftLayerShadowContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.weight(0.62f).fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Логотип (анімація 1)
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(0.63f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .graphicsLayer {
                            this.alpha = logoAlpha
                            this.scaleX = logoScale
                            this.scaleY = logoScale
                        }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(0.57f)
                                .align(Alignment.CenterHorizontally)
                                .padding(top= 8.dp)
                        )
                        Text(
                            text = "MoneyPath",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )

                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                    Text(
                        text = "Плануйте. Відстежуйте. Досягайте.",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer {
                            scaleX = textScale
                            scaleY = textScale
                        }.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(0.321f).fillMaxWidth()
            ) {
                // Кнопка (анімація 3)
                Button(
                    onClick = { onSignInClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight(0.24f)
                        .align(Alignment.Center)
                        .shadowsPlus(
                            type = ShadowsPlusType.SoftLayer,
                            color = Color.Black.copy(alpha = 0.25f),
                            radius = 4.dp,
                            offset = DpOffset(x = 4.dp, y = 8.dp),
                            spread = 1.dp,
                            shape = RoundedCornerShape(25.dp),
                            isAlphaContentClip = true
                        )
                        .graphicsLayer { this.alpha = alpha },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.signin),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Увійти через Google",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.059f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize().align(Alignment.BottomCenter),
                    contentScale = ContentScale.Crop,
                )

            }
        }
    }
}


@Preview(
    name = "Small Phone",
    device = "spec:width=320dp,height=640dp,dpi=320",
    showSystemUi = true,
    showBackground = true
)
@Preview(
    name = "Large Phone",
    device = "spec:width=480dp,height=960dp,dpi=440",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun SignInScreenPreview() {
    MoneyPathTheme {
        SingInScreen(onSignInClick = {})
    }
}