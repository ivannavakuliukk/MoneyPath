package com.example.moneypath.ui.elements.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.AppButtonGroup
import com.example.moneypath.ui.elements.MyTopAppBar
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.utils.ScreenSize
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

/*
    Діалогове вікно - введення балансу
    Використовується на сторінках додавання
    та редагування гаманця
 */
@Composable
fun BalanceDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
    balance: Double
) {
    var isPositive by remember { mutableStateOf( (balance>=0) ) }
    var numberPart by remember { mutableStateOf(balance.toString().removePrefix("-")) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        SoftLayerShadowContainer {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                MyTopAppBar(MaterialTheme.colorScheme.background, "Коригувати баланс", onDismiss)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp), end = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp)),
                    verticalArrangement = Arrangement.Center
                ) {
                    AppButtonGroup(
                        modifier = Modifier.fillMaxWidth(),
                        isPositive = isPositive
                    ) { isPositive = it }
                    // Поле вводу з автоматичним знаком
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((ScreenSize.height * 0.25f).coerceAtLeast(180.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.2f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .padding(start = 5.dp, end = 5.dp)
                        ) {
                            Text(
                                text = "UAH",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Center)
                            )
                        }

                        TextField(
                            value = (if (isPositive) "+" else "-") + numberPart,
                            onValueChange = { newValue ->
                                val cleaned = newValue.removePrefix("+").removePrefix("-")
                                if (cleaned.all { it.isDigit() || it == '.' }) {
                                    numberPart = cleaned
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )
                    }
                }

                //Кнопка
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
                {
                    AppButton(
                        onClick = {
                            val value = numberPart.toDoubleOrNull() ?: 0.0
                            val final = if (isPositive) value else -value
                            onSave(final)
                            onDismiss()
                        },
                        "Зберегти",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                start = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp),
                                end = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp),
                                bottom = 25.dp
                            ),
                        MaterialTheme.colorScheme.tertiary
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, device = "spec:width=1080px,height=2000px,dpi=440")
@Composable
private fun BalanceDialogPreview() {
    MoneyPathTheme {
        BalanceDialog(
            onDismiss = {},
            onSave = {},
            balance = 1000.0
        )
    }
}