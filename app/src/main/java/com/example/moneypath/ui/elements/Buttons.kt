package com.example.moneypath.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.moneypath.R
import com.example.moneypath.domain.models.TransactionType
import com.example.moneypath.ui.preview.BooleanPreviewProvider
import com.example.moneypath.ui.preview.ButtonParamsProvider
import com.example.moneypath.ui.preview.SegmentedButtonParamsProvider
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.utils.Dimensions
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.toDisplayName
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

/*
    Кнопка (classic for app)
    Параметри - функція при натисканні, текст, modifier і колір фону
 */
@Composable
fun AppButton(onClick: () -> Unit, text: String, modifier: Modifier, color: Color, enabled:Boolean = true){
    SoftLayerShadowContainer {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height((ScreenSize.height * 0.07f).coerceAtLeast(45.dp))
                .shadowsPlus(
                    type = ShadowsPlusType.SoftLayer,
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = 4.dp,
                    offset = DpOffset(x = 2.dp, y = 2.dp),
                    spread = 1.dp,
                    shape = RoundedCornerShape(25.dp),
                    isAlphaContentClip = true
                )
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(15.dp)),
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
}

data class FabAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
//FAB menu
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppFabMenu(modifier: Modifier = Modifier, dimensions: Dimensions,
               onAddWalletClick: ()-> Unit,
               onAddTransactionClick: ()-> Unit,
               onAddMonoClick: ()-> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val actions = listOf(
        FabAction(
            "Додати гаманець",
            ImageVector.vectorResource(R.drawable.wallet_icon),
            onAddWalletClick
        ),
        FabAction(
            "Додати гаманець mono",
            ImageVector.vectorResource(R.drawable.mono_wallet_icon),
            onAddMonoClick
        ),
        FabAction(
            "Додати транзакцію",
            ImageVector.vectorResource(R.drawable.transaction_icon),
            onAddTransactionClick
        )
    )
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { expanded = it },
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.tertiary,
                    finalColor = Color(0xFF204FF8)
                ),
                containerSize = { dimensions.buttonSize }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (expanded) R.drawable.close_icon else R.drawable.add_icon,
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSize * 0.9f),
                    contentDescription = null
                )
            }
        }
    ) {
        actions.forEach { item ->
            FloatingActionButtonMenuItem(
                onClick = { item.onClick() },
                text = { Text(item.label, style = MaterialTheme.typography.bodyMedium) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconSize * 0.9f)
                    )
                },
                modifier = Modifier.height(dimensions.buttonSize),
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppSplitButton(modifier: Modifier = Modifier, isPlanned: Boolean,
                   onSettingClick:()-> Unit, onDeleteClick:()-> Unit,
                   onAdditionalClick: ()-> Unit , onClick: ()-> Unit) {
    var checked by remember {mutableStateOf(false)}
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { onClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_edit),
                    contentDescription = null,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = if (isPlanned) "Оновити план" else "Створити план",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                val rotate by animateFloatAsState(
                    targetValue = if (checked) 180f else 0f
                )
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.outline_keyboard_arrow_up_24
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(SplitButtonDefaults.TrailingIconSize)
                        .graphicsLayer {
                            rotationZ = rotate
                        }
                )
            }
        }

    )
    DropdownMenu(
        expanded = checked,
        onDismissRequest = { checked = false },
        offset = DpOffset(y = (-10).dp, x = 0.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        val items = listOf(
            "Переглянути налаштування" to onSettingClick,
            "Переглянути додаткові плани" to onAdditionalClick,
            "Видалити план" to onDeleteClick
        )
        items.forEach { item ->
            Column {
                DropdownMenuItem(
                    text = {
                        Text(
                            item.first,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color(0xFF1444F1),
                    ),
                    onClick = {
                        item.second()
                        checked = false
                    }
                )
                Line()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScrollToTopButton(modifier: Modifier = Modifier, onClick: ()-> Unit) {
    IconButton(
        onClick = onClick,
        shape = IconButtonDefaults.mediumRoundShape,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.arrow_appward),
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TransactionSegmentedButton(modifier: Modifier = Modifier, selectedType: TransactionType, onClick: (TransactionType)-> Unit) {
    SingleChoiceSegmentedButtonRow {
        TransactionType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                selected = selectedType == type,
                onClick = { onClick(type) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = TransactionType.entries.size
                ),
                label = {
                    Text(
                        type.toDisplayName(), style = MaterialTheme.typography.bodyLarge,
                        modifier = modifier.padding(end = 15.dp),
                    )
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    inactiveContainerColor = when (selectedType) {
                        TransactionType.Income -> MaterialTheme.colorScheme.secondary
                        TransactionType.Transfer -> MaterialTheme.colorScheme.inverseOnSurface
                        TransactionType.Expense -> MaterialTheme.colorScheme.onSurface
                    },
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveContentColor = MaterialTheme.colorScheme.primary,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    inactiveBorderColor = MaterialTheme.colorScheme.primary
                ),
                icon = {
                    val visible = type == selectedType
                    val scale by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 500)
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 500)
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                alpha = alpha
                            )
                    )

                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppButtonGroup(modifier: Modifier = Modifier,  isPositive: Boolean, onSelectionChange: (Boolean)-> Unit) {
    ButtonGroup(
        modifier = modifier.height(35.dp),
        overflowIndicator = {}
    ) {
        this.toggleableItem(
            checked = isPositive,
            onCheckedChange = { onSelectionChange(true) },
            label = "Позитивне",
            weight = 1f
        )
        this.toggleableItem(
            checked = !isPositive,
            onCheckedChange = { onSelectionChange(false) },
            label = "Негативне",
            weight = 1f,
        )
    }

}

@Preview(showBackground = true, group = "IconButton")
@Composable
private fun ScrollToTopButtonPreview() {
    MoneyPathTheme {
        Box(Modifier
            .wrapContentSize()
            .padding(10.dp)){
            ScrollToTopButton {  }
        }
    }
}

@Preview(showBackground = true, group = "ButtonGroup")
@Composable
private fun ButtonGroupPreview() {
    MoneyPathTheme {
        Box(
            Modifier.wrapContentSize().background(MaterialTheme.colorScheme.background).padding(10.dp)
        ) {
            AppButtonGroup(isPositive = true) { }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, group = "AppButton")
@Composable
private fun AppButtonTwoPreview() {
    MoneyPathTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            AppButton(
                {},
                "Видалити",
                modifier = Modifier
                    .padding(bottom = 15.dp, start = 15.dp, end = 7.dp, top = 10.dp)
                    .weight(0.5f),
                MaterialTheme.colorScheme.surface
            )
            AppButton(
                {},
                "Зберегти",
                modifier = Modifier
                    .padding(bottom = 15.dp, start = 7.dp, end = 15.dp, top = 10.dp)
                    .weight(0.5f),
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Preview(showBackground = true, group = "FabMenu")
@Composable
private fun AppFabMenuPreview() {
    MoneyPathTheme {
        Box(modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.BottomEnd)
        {
            AppFabMenu(
                dimensions = Dimensions(),
                onAddWalletClick = {},
                onAddTransactionClick = {},
                onAddMonoClick = {}
            )
        }
    }
}

@Preview(showBackground = true, group = "SplitButton")
@Composable
private fun AppSplitButtonPreview(
    @PreviewParameter(BooleanPreviewProvider::class, limit = 2) enabled: Boolean
) {
    MoneyPathTheme {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentSize()
        )
        {
            AppSplitButton(
                isPlanned = enabled,
                onSettingClick = {},
                onDeleteClick = {},
                onAdditionalClick = {}
            ) { }
        }
    }
}

@Preview(showBackground = true, group = "SegmentedButton")
@Composable
private fun SegmentedButtonPreview(
    @PreviewParameter(SegmentedButtonParamsProvider::class)
    data: Pair<Color, TransactionType>
) {
    MoneyPathTheme {
        Box(Modifier
            .wrapContentSize()
            .background(data.first)){
            TransactionSegmentedButton(
                selectedType = data.second
            ) { }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, group = "AppButton")
@Composable
private fun AppButtonPreview(
    @PreviewParameter(ButtonParamsProvider::class)
    data:Triple<Color, Boolean, String>
) {
    val(color, enabled, text) = data
    MoneyPathTheme {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            AppButton(
                onClick = {},
                text = text,
                modifier = Modifier.fillMaxWidth(0.9f),
                color = color,
                enabled = enabled
            )
        }
    }
}

