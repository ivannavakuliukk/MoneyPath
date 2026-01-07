package com.example.moneypath.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.AppInputRow
import com.example.moneypath.ui.elements.dialogs.BalanceDialog
import com.example.moneypath.ui.elements.ContainerForDataBox
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBar
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.ui.viewmodel.AddWalletViewModel
import com.example.moneypath.utils.AppTextFieldColors
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

/*
    Сторінка "Додати гаманець"
    Параметр - NavController
    DI - AddWalletViewModel
 */

@Composable
fun AddWalletScreen(navController: NavController, viewModel: AddWalletViewModel = hiltViewModel())  {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    LaunchedEffect(state.success) {
        if (state.success) {
            navController.navigate("mainscreen")
        }
    }
    SoftLayerShadowContainer {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {MyTopAppBar(
                MaterialTheme.colorScheme.background,
                "Додати гаманець"
            ) { navController.popBackStack()}},
            bottomBar = {
                Column {
                    if (state.isLoading) {
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.background,
                            trackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }
                    Line()
                    AppButton(
                        onClick = {
                            viewModel.addOrUpdateWallet()
                        },
                        text = "Зберегти",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(
                            horizontal = ScreenSize.width * 0.055f,
                        ).padding(bottom = 15.dp, top = 10.dp)
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, snackbar = { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    contentColor = Color.White,
                    snackbarData = data
                )
            }) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(paddingValues = innerPadding)
            ) {
                item {
                    TextBox()
                }
                item {
                    DataBox(
                        state = state,
                        onNameChange = { viewModel.onNameChange(it) },
                        onTypeChange = { viewModel.onTypeChange(it) },
                        onBalanceChange = { viewModel.onBalanceChange(it) }
                    )
                }

            }

        }
    }
}


@Composable
fun TextBox(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                top = ScreenSize.height * 0.042f,
                bottom = ScreenSize.height * 0.042f,
                start = ScreenSize.width * 0.055f,
                end = ScreenSize.width * 0.055f
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(ScreenSize.width * 0.035f)
    ){
        Image(
            painter = painterResource(R.drawable.hint),
            contentDescription = null,
            modifier = Modifier.size(ScreenSize.width*0.06f)
        )
        Text(
            text = "Не додавайте вручну карту monobank. У Money Path гаманець даного банку додається автоматично, після того, як Ви вкажете Monobank Api в налаштуваннях.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 5.dp)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataBox(
    state:AddWalletViewModel.UiState,
    onNameChange:(String) -> Unit,
    onBalanceChange: (Double) -> Unit,
    onTypeChange: (WalletType) -> Unit
){
    var showDialog by remember{ mutableStateOf(false)}

    val walletTypeLabels = mapOf(
        WalletType.Cash to "Готівка",
        WalletType.Card to "Карта"
    )

    if (showDialog) {
        BalanceDialog(
            onDismiss = { showDialog = false },
            onSave = { onBalanceChange(it)},
            balance = state.balance
        )
    }

    ContainerForDataBox {
        // Назва
        AppInputRow(
            iconRes = R.drawable.name,
            text = "Назва"
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = {onNameChange(it)},
                placeholder = {
                    Text (
                        text = "Введіть назву",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        overflow = TextOverflow.Visible
                    )
                },
                textStyle = MaterialTheme.typography.bodySmall,
                colors = AppTextFieldColors,
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .weight(0.56f)
            )
        }
        // Тип
        AppInputRow(
            iconRes = R.drawable.type,
            text = "Тип"
        ) {
            // Випадаючий список
            var expanded by remember{ mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {expanded = !expanded},
                modifier = Modifier.weight(0.56f)
            ) {
                OutlinedTextField(
                    value = walletTypeLabels[state.type]?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded)},
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .menuAnchor(),
                    colors = AppTextFieldColors,
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(15.dp),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                ) {
                    WalletType.entries.forEach { walletType->
                        DropdownMenuItem(
                            text = { Text(
                                text = walletTypeLabels[walletType]?: walletType.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )},
                            onClick = {
                                onTypeChange(walletType)
                                expanded = false
                            },
                            modifier = Modifier.background(
                                if (walletType == state.type){
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.05f)
                                }else
                                    Color.Transparent
                            )
                        )
                    }
                }
            }
        }
        // Баланс
        AppInputRow(
            iconRes = R.drawable.balance,
            text = "Баланс"
        ) {
            Button(
                onClick = {showDialog = true},
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .weight(0.56f)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f))
                    .border(width = 1.dp, shape = RoundedCornerShape(15.dp), color = MaterialTheme.colorScheme.onPrimary),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f),
                    contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    disabledContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.03f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                ),
                contentPadding = PaddingValues(15.dp)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${state.balance}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                        Text(
                            text = "UAH",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.arrow_rigth),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight(0.85f)
                    )
                }
            }
        }
        Line()
    }
}