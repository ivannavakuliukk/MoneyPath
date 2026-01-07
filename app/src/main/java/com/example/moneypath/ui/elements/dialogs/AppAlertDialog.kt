package com.example.moneypath.ui.elements.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneypath.ui.theme.MoneyPathTheme

/*
    Вікно підтвердження
    Параметри - текст, функції для підтвердження та для відміни
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAlertDialog(text:String, onConfirmClick: () -> Unit, onCancelClick: ()-> Unit){
    AlertDialog(
        onDismissRequest = onCancelClick,
        content = {
            Card(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(Modifier.Companion.padding(15.dp)) {
                    Text(
                        text = "Підтвердження",
                        style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onPrimary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.7f
                            )
                        ),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(
                            bottom = 1.dp,
                            start = 5.dp,
                            end = 5.dp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onCancelClick) {
                            Text("Ні", color = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onConfirmClick) {
                            Text("Так", color = Color.Red)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppConfirmDialog(text: String, title: String, onDismiss: ()-> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss() },
        content = {
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ){
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            MaterialTheme.colorScheme.onPrimary
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        ),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 1.dp, start = 5.dp, end = 5.dp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { onDismiss() }) {
                        Text("Зрозуміло", color = MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AlertDialogPreview() {
    MoneyPathTheme {
        AppAlertDialog(
            text = "Ви впевнені що хочете обрати цей план замість поточного? Весь прогрес видалиться, це незворотня дія!",
            onConfirmClick = {}
        ) { }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ConfirmDialogPreview() {
    MoneyPathTheme {
        AppConfirmDialog(
            text = """
                    1. Зайдіть на сторінку api.monobank.ua. 
                    2. Авторизуйтесь через додаток mono.
                    3. Згенеруйте персональний токен.
                    4. Скопіюйте і вставте у додаток.
                    """.trimIndent(),
            title = "Інструкція"
        ) { }
    }
}
