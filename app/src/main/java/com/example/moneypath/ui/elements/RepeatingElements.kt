package com.example.moneypath.ui.elements

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneypath.R
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.ui.preview.WalletsProvider
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.utils.AppTextFieldColors
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.cardShadow
import com.example.moneypath.utils.formattedDate
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus

// Повторювані елементи


// Лінія
@Composable
fun Line(color: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f), modifier: Modifier = Modifier, height: Dp  = 1.dp){
    Box(modifier = modifier
        .fillMaxWidth()
        .height(height)
        .background(color))
}


/*
    Індикатор сторінок
    Параметри - кількість сторінок, поточна сторінка
 */
@Composable
fun PagerIndicator(
    pagerSize: Dp = 8.dp,
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
            .padding(top = pagerSize * 0.7f, bottom = pagerSize * 0.7f)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(pagerSize / 2)
                    .size(if (index == currentPage) pagerSize * 1.25f else pagerSize)
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
            painter = painterResource(iconRes),
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
    showTitle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
){
    Column {
        if(showTitle) {
            Text(
                text = "Відомості",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(
                    start = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp),
                    bottom = 10.dp,
                    top = 30.dp
                )
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp))
                .padding(bottom = 30.dp, top = if(!showTitle) 20.dp else 0.dp)
                .cardShadow()
                .fillMaxWidth()
                .wrapContentHeight()
                .padding((ScreenSize.width * 0.035f).coerceAtLeast(10.dp))
        ) {
            content()
        }
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
fun TextFieldForDate(
    onClick: () -> Unit,
    textFieldColors: TextFieldColors,
    modifier: Modifier,
    stateDate: Long,
    height: Float,
    iconPadding: Int? = null
){
    Box(modifier) {
        OutlinedTextField(
            value = formattedDate(stateDate),
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxHeight(height)
                .clickable { onClick() },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Вибір дати",
                    modifier =  Modifier.size(24.dp)
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
            .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(10.dp))
            .padding(bottom = 15.dp, top = 10.dp)
            .cardShadow()
            .fillMaxWidth()
            .heightIn(max = 7000.dp, min = ScreenSize.height * 0.725f)
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .padding((ScreenSize.width * 0.035f).coerceAtLeast(10.dp))
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
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth()
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.hint),
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
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .weight(0.1f)
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

@Composable
fun PlanContainer(background: Color =MaterialTheme.colorScheme.primary ,content: @Composable ColumnScope.() -> Unit){
    Column(
        modifier = Modifier
            .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(15.dp))
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
            .heightIn(max = 7000.dp, min = 500.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .padding(ScreenSize.width * 0.035f)
    ){
        content()
    }
}

@Composable
fun AppSnackBar(data: SnackbarData){
    Snackbar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = Color.White,
        snackbarData = data
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InfiniteLinearWavyIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Restart
        )
    )
    LinearWavyProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp),
        trackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
        color = MaterialTheme.colorScheme.background,
        gapSize = 2.dp,
        wavelength = 20.dp,
        waveSpeed = 30.dp
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun WalletDropdownMenuPreview(
    @PreviewParameter(WalletsProvider::class, limit = 1)
    wallets: List<Wallet>
) {
    MoneyPathTheme {
        Box(Modifier.height(65.dp)) {
            WalletDropDownMenu(
                stateWalletId = "1",
                wallets = wallets,
            ) { }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SnackBarPreview() {
    MoneyPathTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(
                message = "Тестове повідомлення"
            )
        }

        SnackbarHost(hostState = snackbarHostState) { data ->
            AppSnackBar(data = data)
        }
    }
}