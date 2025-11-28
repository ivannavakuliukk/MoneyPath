package com.example.moneypath.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.AppDatePicker
import com.example.moneypath.ui.elements.AppInputRow
import com.example.moneypath.ui.elements.ContainerForDataBox
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBar
import com.example.moneypath.ui.elements.WalletDropDownMenu
import com.example.moneypath.ui.viewmodel.AddTransactionViewModel
import com.example.moneypath.utils.AppTextFieldColors
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.backgroundColor
import com.example.moneypath.utils.displaySign
import com.example.moneypath.utils.toDisplayName
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

/*
    Сторінка додавання транзакції
    di - AddTransactionViewModel
    параметри - navController
 */
@Composable
fun AddTransactionScreen(navController: NavController, date: Long, isGoal:Boolean, viewModel: AddTransactionViewModel = hiltViewModel()){
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(date) {
        viewModel.onDateChange(date)
    }
    LaunchedEffect(isGoal) {
        viewModel.onGoalChange(isGoal)
    }
    // Показ помилок через Snack bar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    // очищаємо гаманець отримувач
    LaunchedEffect (state.type){
        if(state.type != TransactionType.Transfer || state.description!="Внутрішній"){
            viewModel.onWalletIdToChange("")
        }
    }

    // Автоматичний вихід при успішній транзакції
    LaunchedEffect(state.success) {
        if (state.success) {
            navController.popBackStack()
        }
    }
        SoftLayerShadowContainer {
            Scaffold(
                topBar = {
                    MyTopAppBar(
                        background = state.type.backgroundColor(),
                        title = "Додати транзакцію"
                    ) { navController.popBackStack() }
                },
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
                                viewModel.addTransaction()
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
                    })
                }
            ) { innerPadding ->
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(innerPadding)
                ) {
                    item {
                        TransactionInputSection(
                            state = state,
                            onTypeChange = { selectedType ->
                                viewModel.onTypeChange(selectedType)
                            },
                            onAmountChange = { selectedAmount ->
                                viewModel.onAmountChange(selectedAmount)
                            }
                        )
                    }
                    item {
                        if (state.type == TransactionType.Income || state.type == TransactionType.Expense) {
                            IncomeExpensesDataBox(
                                state,
                                onWalletIdChange = { selectedWalletId ->
                                    viewModel.onWalletIdChange(selectedWalletId)
                                },
                                onCategoryIdChange = { viewModel.onCategoryIdChange(it) },
                                onDateChange = { viewModel.onDateChange(it) },
                                onDescriptionChange = { viewModel.onDescriptionChange(it) }
                            )
                        } else {
                            TransferDataBox(
                                state,
                                onWalletIdChange = { viewModel.onWalletIdChange(it) },
                                onDescriptionChange = { viewModel.onDescriptionChange(it) },
                                onWalletIdToChange = { viewModel.onWalletIdToChange(it) },
                                onDateChange = {viewModel.onDateChange(it)}
                            )
                        }
                    }

                }

            }

        }
}



// Блок з вибором типу транзакції та введенням суми
@Composable
fun TransactionInputSection(
    state: AddTransactionViewModel.UiState,
    onTypeChange: (TransactionType)-> Unit,
    onAmountChange: (String) -> Unit
){
    Column(
        Modifier.fillMaxWidth()
            .wrapContentHeight()
            .background(state.type.backgroundColor())
            .padding(horizontal = ScreenSize.width* 0.055f)
    ) {
        TabRow(
            selectedTabIndex = state.type.ordinal,
            modifier = Modifier
                .fillMaxWidth()
                .height(ScreenSize.height * 0.05f)
                .clip(RoundedCornerShape(15.dp))
            ,
        ) {
            TransactionType.entries.forEachIndexed { index, type ->
                Tab(
                    selected = state.type == type,
                    onClick = { onTypeChange(type) },
                    text = {
                        Text(
                            text = type.toDisplayName(),
                            style = MaterialTheme.typography.bodyLarge,
                            color =
                            if(type == state.type)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.primary
                        ) },
                    modifier = Modifier.background(
                        if(type == state.type) {
                            MaterialTheme.colorScheme.primary
                        }
                        else{
                            when(state.type){
                                TransactionType.Income -> MaterialTheme.colorScheme.secondary
                                TransactionType.Transfer -> MaterialTheme.colorScheme.inverseOnSurface
                                TransactionType.Expense -> MaterialTheme.colorScheme.onSurface
                            }
                        }
                    )
                )
            }
        }
        // Поле вводу з автоматичним знаком
        Row(
            modifier = Modifier.fillMaxWidth().height(ScreenSize.height * 0.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    .padding(horizontal = 5.dp)
            ) {
                Text(
                    text = "UAH",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Center)
                )
            }

            TextField(
                value = state.type.displaySign() + state.amount.removePrefix("-"),
                onValueChange = { newValue ->
                    val cleaned = newValue.removePrefix("+").removePrefix("-")
                    if (cleaned.all { it.isDigit() || it == '.' }) {
                        onAmountChange(cleaned)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
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
}

// Блок з введенням даних для витрат і доходів
@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeExpensesDataBox(
    state: AddTransactionViewModel.UiState,
    onWalletIdChange: (String) -> Unit,
    onCategoryIdChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onDescriptionChange: (String)-> Unit
){
    ContainerForDataBox {
        // Гаманець
        var selectedWallet = state.wallets.firstOrNull { it.id == state.walletId }
        if (selectedWallet!= null){
            AppInputRow(
                iconRes = if(selectedWallet.type == WalletType.Cash) R.drawable.cash else R.drawable.card,
                text = "Гаманець"
            ) {
                // Випадаючий список
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(0.56f)
                ) {
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
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    ) {
                        state.wallets.forEach { wallet ->
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
                                    if (wallet.id == state.walletId) {
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.05f)
                                    } else
                                        Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
        // Категорія
        var selectedCategory = state.categories.firstOrNull { it.id == state.categoryId }
        if(selectedCategory!=null){
            AppInputRow(
                iconRes = selectedCategory.iconRes,
                text = "Категорія"
            ) {
                // Випадаючий список
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(0.56f)
                ) {
                        OutlinedTextField(
                            value = selectedCategory.name,
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
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    ) {
                        state.categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                onClick = {
                                    onCategoryIdChange(category.id)
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (category.id == state.categoryId) {
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.05f)
                                    } else
                                        Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
        // Дата
        AppInputRow(
            iconRes = R.drawable.calendar,
            text = "Дата"
        ) {
            AppDatePicker(
                onDateChange = onDateChange,
                modifier = Modifier.weight(0.56f),
                textFieldColors = AppTextFieldColors,
                stateDate = state.date,
                height = 0.7f
            )
        }
        // Опис
        AppInputRow(
            iconRes = R.drawable.name,
            text = "Опис"
        ) {
            OutlinedTextField(
                value = state.description,
                onValueChange = {onDescriptionChange(it)},
                placeholder = {
                    Text (
                        text = "Введіть нотатку",
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
        Line()
    }
}


@Composable
fun TransferDataBox(
    state: AddTransactionViewModel.UiState,
    onWalletIdChange: (String) -> Unit,
    onWalletIdToChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Long) -> Unit
){
    ContainerForDataBox {

        var selectedOption by remember { mutableStateOf("Option 1") }
        LaunchedEffect(Unit) {
            if(state.description.isBlank()) {
                val defaultDescription = when(selectedOption) {
                    "Option 1" -> "Зовнішній(дохід)"
                    "Option 2" -> "Зовнішній(витрата)"
                    "Option 3" -> "Внутрішній"
                    else -> "Зовнішній(дохід)"
                }
                onDescriptionChange(defaultDescription)
            }
        }
        // Очищаємо гаманець-отримувач
        LaunchedEffect(state.description) {
            if(state.description == "Зовнішній(дохід)" || state.description =="Зовнішній(витрата)"){
                onWalletIdToChange("")
            }
        }

        AppInputRow(
            iconRes = R.drawable.category_transfer,
            text = "Зовнішній переказ",
            additionText = "На мій гаманець, поза MoneyPath",
            contentInFront = {
                RadioButton(
                    selected = (selectedOption == "Option 1"),
                    onClick = {
                        onDescriptionChange("Зовнішній(дохід)")
                        onWalletIdToChange("")
                        selectedOption = "Option 1"
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        unselectedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        )
        AppInputRow(
            iconRes = R.drawable.category_transfer,
            text = "Зовнішній переказ",
            additionText = "З мого гаманця, поза MoneyPath",
            contentInFront = {
                RadioButton(
                    selected = (selectedOption == "Option 2"),
                    onClick = {
                        onDescriptionChange("Зовнішній(витрата)")
                        onWalletIdToChange("")
                        selectedOption = "Option 2"
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        unselectedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        )
        AppInputRow(
            iconRes = R.drawable.category_transfer,
            text = "Внутрішній переказ",
            additionText = "Між моїми гаманцями",
            contentInFront = {
                RadioButton(
                    selected = (selectedOption == "Option 3"),
                    onClick = {
                        onDescriptionChange("Внутрішній")
                        selectedOption = "Option 3"
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        unselectedColor = MaterialTheme.colorScheme.tertiary,
                        disabledUnselectedColor = MaterialTheme.colorScheme.inverseSurface,
                        disabledSelectedColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    enabled = state.wallets.size > 1
                )
            }
        )
        Line()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ScreenSize.height * 0.11f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =  if (selectedOption== "Option 2" || selectedOption == "Option 3")"З Гаманця" else "На гаманець" ,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(0.44f)
            )

            WalletDropDownMenu(
                stateWalletId = state.walletId,
                wallets = state.wallets,
                onWalletIdChange = { onWalletIdChange(it) }
            )
        }
        if(selectedOption == "Option 3") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScreenSize.height * 0.11f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "На гаманець",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth(0.44f)
                )

                WalletDropDownMenu(
                    stateWalletId = state.walletIdTo,
                    wallets = state.wallets,
                    onWalletIdChange = { onWalletIdToChange(it) }
                )
            }
        }

        // Дата
        AppInputRow(
            iconRes = R.drawable.calendar,
            text = "Дата"
        ) {
            AppDatePicker(
                onDateChange = onDateChange,
                modifier = Modifier.weight(0.56f),
                textFieldColors = AppTextFieldColors,
                stateDate = state.date,
                height = 0.7f
            )
        }
        Line()
    }
}