package com.example.moneypath.ui.elements

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.R
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.utils.AppTextFieldColors
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.formattedDate
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import java.util.Calendar

// Повторювані елементи


// Лінія
@Composable
fun Line(color: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)){
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color))
}

/*
    Кнопка
    Параметри - функція при натисканні, текст, modifier і колір фону
 */
@Composable
fun AppButton(onClick: () -> Unit, text: String, modifier: Modifier, color: Color, enabled:Boolean = true){
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ScreenSize.height* 0.07f)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 2.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(25.dp),
                isAlphaContentClip = true)
            .clip(RoundedCornerShape(15.dp)),
        colors = ButtonColors(
            containerColor = color,
            disabledContainerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

/*
    Верхня панель
    Параметри - заголовок, колір, функція при натисканні на іконку
 */
@Composable
fun MyTopAppBar(background: Color, title: String, onClick: () -> Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(ScreenSize.height *0.097f)
        .background(background)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ){
            IconButton(
                onClick = {onClick()},
                modifier = Modifier.width(ScreenSize.width*0.041f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.cancel_white),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(ScreenSize.width* 0.055f))

        }

    }
}

@Composable
fun MyTopAppBarTwoLines(background: Color, title: String, text: String, onClick: () -> Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(ScreenSize.height *0.097f)
        .background(background)
    ) {
        Column(modifier = Modifier.align(Alignment.Center).wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Box(modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(5.dp))
                .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary),
                )
            }
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ){
            IconButton(
                onClick = {onClick()},
                modifier = Modifier.width(ScreenSize.width*0.041f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.cancel_white),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(ScreenSize.width* 0.055f))

        }

    }
}


@Composable
fun MyTopAppBarNoIcon(title: String, background: Color){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(ScreenSize.height *0.097f)
        .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

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
                Column(Modifier.padding(15.dp)) {
                    Text(
                        text = "Підтвердження",
                        style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onPrimary),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 1.dp, start = 5.dp, end = 5.dp),
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

/*
    Індикатор сторінок
    Параметри - кількість сторінок, поточна сторінка
 */
@Composable
fun PagerIndicator(
    totalPages: Int,
    currentPage: Int,
    background: Color = MaterialTheme.colorScheme.background
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .wrapContentHeight()
            .padding(top = 6.dp, bottom = 6.dp)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

@Composable
fun AppInputRow(
    iconRes:Int,
    text: String,
    additionText: String = "",
    contentInFront: @Composable RowScope.() ->Unit = {},
    content : @Composable RowScope.() ->Unit = {},
){
    Line()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ScreenSize.height * 0.11f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        contentInFront()
        Image(
            painter = rememberAsyncImagePainter(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(0.45f)
        )
        Column(Modifier.weight(0.32f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 5.dp)
            )
            if(additionText.isNotEmpty()) {
                Text(
                    text = additionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp)
                )
            }
        }
        content()
    }
}

@Composable
fun ContainerForDataBox(
    content: @Composable ColumnScope.() -> Unit
){
    Text(
        text = "Відомості",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = ScreenSize.width*0.055f, bottom = 10.dp, top = 30.dp)
    )
    Column(
        modifier = Modifier
            .padding(horizontal = ScreenSize.width * 0.055f)
            .padding(bottom = 30.dp)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 2.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(15.dp),
                isAlphaContentClip = true
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(ScreenSize.width * 0.035f)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDropDownMenu(
    stateWalletId: String,
    wallets: List<Wallet>,
    onWalletIdChange: (String)-> Unit
){
    // Випадаючий список
    var selectedWallet = wallets.firstOrNull { it.id == stateWalletId }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        if (selectedWallet != null) {
            OutlinedTextField(
                value = selectedWallet.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .menuAnchor(),
                colors = AppTextFieldColors,
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(15.dp),
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            wallets.forEach { wallet ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    onClick = {
                        onWalletIdChange(wallet.id)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (wallet.id == stateWalletId) {
                            MaterialTheme.colorScheme.background.copy(alpha = 0.05f)
                        } else
                            Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun AppDatePicker(
    onDateChange: (Long) -> Unit,
    textFieldColors: TextFieldColors,
    modifier: Modifier,
    stateDate: Long,
    height: Float,
    iconPadding: Int? = null
){
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        timeInMillis = stateDate * 1000
    }
    val todayDate = Calendar.getInstance()
    val maxDateMillis = todayDate.timeInMillis

    Box(modifier) {
        OutlinedTextField(
            value = formattedDate(stateDate),
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxHeight(height)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedCal = Calendar.getInstance()
                            selectedCal.set(year, month, dayOfMonth)
                            onDateChange(selectedCal.timeInMillis / 1000) // секунди
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                    ).apply {
                        datePicker.maxDate = maxDateMillis
                        setTitle(null)
                    }.show()
                },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Вибір дати",
                    modifier = if(iconPadding!=null){
                        Modifier.padding(start = iconPadding.dp)
                    }else Modifier
                )
            },
            colors = textFieldColors,
            enabled = false,
            shape = RoundedCornerShape(15.dp)
        )
    }
}

@Composable
fun FormContainer(background: Color =MaterialTheme.colorScheme.primary ,content: @Composable ColumnScope.() -> Unit){
    Column(
        modifier = Modifier
            .padding(horizontal = ScreenSize.width * 0.055f)
            .padding(bottom = 15.dp, top = 10.dp)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 2.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(15.dp),
                isAlphaContentClip = true
            )
            .fillMaxWidth()
            .heightIn(max = 7000.dp, min = ScreenSize.height * 0.725f)
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .padding(ScreenSize.width * 0.035f)
    ){
        content()
    }
}

@Composable
fun FormTitle(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun TitleRow(text: String){
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp).wrapContentHeight().fillMaxWidth()
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.hint),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
fun InputRowWithIcon(iconRes: Int, value: String, onValueChange: (String)->Unit, text: String, isDecimal: Boolean = true){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ScreenSize.height * 0.13f)
            .padding(bottom = 10.dp)
        ,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = rememberAsyncImagePainter(iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp).weight(0.1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {onValueChange(it)},
            placeholder = {
                Text (
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    overflow = TextOverflow.Visible
                )
            },
            textStyle = MaterialTheme.typography.bodySmall,
            colors = AppTextFieldColors,
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if(isDecimal) KeyboardType.Number else KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxHeight(0.65f)
                .weight(0.9f)
        )
    }
}

@Composable
fun Title(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 10.dp, top = 5.dp)
    )
    Line()
}

@Composable
fun DiffStyleLine(text1: String, text2: String){
    Text(text=
        buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.primary).toSpanStyle())
            {append(text1)}
            withStyle(MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.inversePrimary).toSpanStyle())
            {append(text2)}
        },
        lineHeight = 25.sp
    )
}

@Composable
fun BorderedBox(content: @Composable RowScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RectangleShape)
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
            content = content
        )
    }
}