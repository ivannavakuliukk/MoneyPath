package com.example.moneypath.ui.elements.dialogs

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneypath.ui.preview.DialogParameterProvider
import com.example.moneypath.ui.preview.DialogParams
import com.example.moneypath.ui.theme.MoneyPathTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInputDialog(onClick: (String) -> Unit, onDismiss: ()-> Unit, text: String){
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        content = {
            Card(
                modifier = Modifier.Companion
                    .padding(15.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    Modifier.Companion.padding(15.dp),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium.copy(
                            MaterialTheme.colorScheme.onPrimary
                        ),
                        textAlign = TextAlign.Companion.Center,
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    DialogTextField(
                        "Токен",
                        input
                    ) { it -> input = it}
                    Spacer(modifier = Modifier.Companion.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.Companion.fillMaxWidth()
                    ) {
                        TextButton(onClick = { onDismiss() }) {
                            Text(
                                "Скасувати",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Spacer(modifier = Modifier.Companion.width(8.dp))
                        TextButton(onClick = {
                            onClick(input)
                        }) {
                            Text("Зберегти", color = Color.Companion.Red)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DialogTextField(placeholder: String, value: String, onValueChange: (String)-> Unit) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(placeholder) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(
                alpha = 0.1f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(
                alpha = 0.1f
            )
        )
    )
}

@Composable
fun AppImageDialog(
    imageRes: Int? = null, // GIF або PNG
    title: String? = null,
    message: String? = null,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    cancelable: Boolean = false,
) {
    Dialog(
        onDismissRequest = { if (cancelable) onDismiss?.invoke() },
        properties = DialogProperties(
            dismissOnClickOutside = cancelable,
            dismissOnBackPress = cancelable
        )
    ) {
        Box(
            modifier = Modifier.Companion
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(15.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            if (imageRes != null) {
                                setImageResource(imageRes)
                            }
                            val drawable = drawable
                            if (drawable is Animatable) drawable.start()
                        }
                    },
                    modifier = Modifier.Companion.size(140.dp)
                )
                title?.let {
                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                }
                message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.7f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))
                }
                if (dismissText == null) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(confirmText, color = MaterialTheme.colorScheme.background)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDismiss?.invoke() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(dismissText, color = MaterialTheme.colorScheme.background,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        OutlinedButton(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(confirmText, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun InputDialogPreview() {
    MoneyPathTheme {
        AppInputDialog(
            onClick = {},
            onDismiss = {},
            text = "Введіть токен"
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DialogTextFieldPreview() {
    MoneyPathTheme {
        Box(
            Modifier
                .wrapContentSize()
                .padding(10.dp)
                .background(
                    MaterialTheme.colorScheme.primary
                )
        ){
            DialogTextField(
                "Токен",
                ""
            ) { }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ImageDialogPreview(
    @PreviewParameter(DialogParameterProvider::class)
    content: DialogParams
) {
    MoneyPathTheme {
        AppImageDialog(
            confirmText = content.confirmText,
            dismissText = content.dismissText,
            message = content.message,
            imageRes = content.image,
            title = content.title,
            onConfirm = {}
        )
    }
}
