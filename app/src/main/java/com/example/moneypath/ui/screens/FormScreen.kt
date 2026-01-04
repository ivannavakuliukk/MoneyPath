package com.example.moneypath.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.contextmenu.data.TextContextMenuSeparator.key
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.Category
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.AppInputRow
import com.example.moneypath.ui.elements.BorderedBox
import com.example.moneypath.ui.elements.DiffStyleLine
import com.example.moneypath.ui.elements.FormContainer
import com.example.moneypath.ui.elements.FormTitle
import com.example.moneypath.ui.elements.InputRowWithIcon
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarTwoLines
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.elements.Title
import com.example.moneypath.ui.elements.TitleRow
import com.example.moneypath.ui.viewmodel.FormScreenViewModel
import com.example.moneypath.utils.GreenTextFieldColors
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.monthWordForm
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun FormScreen(navController: NavController, viewModel: FormScreenViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Повний набір кроків — 0..8 (9 сторінок) у випадку isGoal == true
    // Якщо isGoal == false — пропускаються кроки 1 і 8 (7 сторінок)
    val visibleSteps = if (state.isGoal) {
        (0..9).toList()
    } else {
        listOf(0, 2, 3, 4, 5, 6, 7, 9)
    }
    val currentIndicatorIndex = visibleSteps.indexOf(state.currentStep).coerceAtLeast(0)


    SoftLayerShadowContainer {
        Scaffold(
            bottomBar = {
                Column(Modifier.fillMaxWidth().wrapContentHeight().background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))) {
                    if(state.currentStep!=9) {
                        PagerIndicator(
                            totalPages = visibleSteps.size,
                            currentPage = currentIndicatorIndex,
                            background = Color.Transparent
                        )
                    }else{
                        Box(modifier = Modifier.fillMaxWidth().height(26.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    ) {
                        AppButton(
                            { viewModel.previousStep() },
                            text = if(state.currentStep == 9) "До налаштувань" else "Назад",
                            modifier = Modifier
                                .padding(
                                    bottom = 10.dp, start = ScreenSize.width * 0.055f,
                                    end = ScreenSize.width * 0.022f)
                                .weight(0.5f),
                            MaterialTheme.colorScheme.tertiary
                        )
                        AppButton(
                            { viewModel.onNextClick() },
                            text = if(state.currentStep == 9) "Розрахувати" else "Далі",
                            modifier = Modifier
                                .padding(
                                    bottom = 10.dp, start = ScreenSize.width * 0.022f,
                                    end = ScreenSize.width * 0.055f)
                                .weight(0.5f),
                            MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            },
            topBar = {
                MyTopAppBarTwoLines(
                    title =  "Налаштування плану",
                    text = when (state.currentStep) {
                        0 -> "Вибір типу плану"
                        1 -> "Мета накопичення"
                        2 -> "Дохід"
                        3 -> "Категорії"
                        4 -> "Межі"
                        5 -> "Пріоритети"
                        6 -> "Фіксовані витрати"
                        7 -> "Гаманці"
                        8 -> "Термін накопичення"
                        9 -> "Перевірка"
                        else -> ""
                },
                background = MaterialTheme.colorScheme.background)
                {navController.popBackStack()}
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
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(innerPadding).background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    when(state.currentStep){
                        0-> Step1Screen(state.isGoal){viewModel.updateIsGoal(it)}
                        1-> Step2Screen(state.goalName, state.goalAmount,
                            {viewModel.updateGoalName(it)},
                            {viewModel.updateGoalAmount(it)}
                        )
                        2-> Step3Screen(state.stableIncome, state.currentIncome,
                            {viewModel.updateStableIncome(it)},
                            {viewModel.updateCurrentIncome(it)}
                        )
                        3-> Step4Screen(state.categories) {viewModel.toggleCategory(it) }
                        4-> Step5Screen(state.categories, state.bounds)
                        { index, low, high -> viewModel.updateBoundForCategory(index, low, high) }
                        5-> Step6Screen(state.categories, state.newOrderCategories){viewModel.updateNewOrderCategories(it)}
                        6-> Step7Screen(state.fixedCategories, state.fixedAmountCurrent)
                        { index, amount-> viewModel.updateFixedAmountCurrent(index, amount)}
                        7-> Step8Screen(state.userWallets, state.wallets) {viewModel.updateWallets(it) }
                        8-> Step9Screen(state.minMonth, state.months , state.goalAmount, state.stableIncome) {viewModel.updateMonths(it) }
                        9-> SummaryScreen(state, navController)
                    }
                }
            }
        }
    }
}

// Екран 1 - з метою чи без
@Composable
fun Step1Screen(selectedOption: Boolean, onOptionSelected: (Boolean) -> Unit){
    FormContainer{
        Text(
            text = "Як ви хочете планувати бюджет?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 10.dp, top = 5.dp)
        )
        AppInputRow(
            iconRes = R.drawable.goal,
            text = "План з ціллю",
            additionText = "Мета - накопичити певну суму за визначений термін",
            contentInFront = {
                RadioButton(
                    selected = selectedOption,
                    onClick = {onOptionSelected(true) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        unselectedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        )
        AppInputRow(
            iconRes = R.drawable.hand,
            text = "План без цілі",
            additionText = "Простий оптимальний розподіл без строків та накопичень",
            contentInFront = {
                RadioButton(
                    selected = !selectedOption,
                    onClick = {onOptionSelected(false) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.tertiary,
                        unselectedColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        )
        Line()
    }
}

// Екран 2 - Ціль
@Composable
fun Step2Screen(name: String?, amount: Int?, onNameChanged: (String?)-> Unit, onAmountChanged: (Int?)-> Unit){
    FormContainer {
        Title("Вкажіть назву та суму фінансової мети")
        TitleRow("Алгоритм розрахує мінімальний термін накопичень та кількість щомісячних відкладень")
        FormTitle("Що плануєте придбати або досягти?")
        InputRowWithIcon(
            iconRes = R.drawable.category_goal,
            text = "Введіть назву...",
            value = name ?: "",
            onValueChange = { onNameChanged(it) },
            isDecimal = false
        )
        FormTitle("Яку суму хочете відкласти?")
        InputRowWithIcon(
            iconRes = R.drawable.hryvnia_yellow,
            text = "Введіть суму...",
            value = amount?.toString() ?: "",
            onValueChange = { onAmountChanged(it.toIntOrNull() ?: 0)}
        )
    }
}

// Екран 3 - Дохід
@Composable
fun Step3Screen(income1: Int, income2: Int, onIncome1Changed: (Int)-> Unit, onIncome2Changed: (Int) -> Unit){
    var isChecked by remember { mutableStateOf(false) }
    FormContainer {
        Title("Вкажіть щомісячний дохід")
        TitleRow("Це необхідно для визначення доступного бюджету")
        FormTitle("Яка сума вашого щомісячного доходу?")
        InputRowWithIcon(
            iconRes = R.drawable.hryvnia_green,
            text = "Введіть суму...",
            value = income1.toString() ,
            onValueChange = { onIncome1Changed(it.toIntOrNull() ?: 0) }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = "Отримаю ще дохід цього місяця",
                style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.secondary)
            )
            Box(Modifier.size(30.dp)) {
                Switch(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.scale(0.6f)
                )
            }
        }
        if(isChecked) {
            FormTitle("Яку суму доходу ви отримаєте цього місяця?")
            InputRowWithIcon(
                iconRes = R.drawable.hryvnia_green,
                text = "Введіть суму...",
                value = income2.toString(),
                onValueChange = { onIncome2Changed(it.toIntOrNull() ?: 0) }
            )
        }
    }
}

// Екран 4 - Категорії
@Composable
fun Step4Screen(selectedCategories: List<String>, onCategorySelected: (String)-> Unit){
    val categories = Categories.expensesCategory.dropLast(1)
    FormContainer {
        Title("Виберіть категорії витрат")
        TitleRow("На що ви витрачаєте гроші? Оберіть основні категорії, решта буде в 'Інше'")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().heightIn(max = 7000.dp, min = ScreenSize.height * 0.7f),
            contentPadding = PaddingValues(0.dp),
            userScrollEnabled = false
        ) {
            items(categories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            onCategorySelected(category.name)
                        }
                        .padding(2.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 4.dp,
                                color = if (selectedCategories.contains(category.name))
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(category.iconRes),
                            contentDescription = category.name,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)),
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Екран 5 - Межі витрат
@Composable
fun Step5Screen(categoriesNames: List<String>, bounds: List<Pair<Int, Int>>,
                onBoundsChange: (index:Int, low:Int?, high:Int?)->Unit)
{
    val categories = categoriesNames.mapNotNull { name ->
        Categories.expensesCategory.find { it.name == name }
    }
    FormContainer {
        Title("Вкажіть межі витрат")
        TitleRow("Вкажіть мінімум - суму, без якої неможливо обійтись, та максимум - витрати, з якими вам комфортно. min=max - фіксована категорія.")
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { index, category ->
                val (low, high) = bounds.getOrNull(index) ?: (0 to 0)
                Line()
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(category.iconRes),
                            contentDescription = category.name,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    Icon(
                        painter = painterResource(if(high == low && high!=0) R.drawable.lock else R.drawable.unlock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(15.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 15.dp)) {
                    OutlinedTextField(
                        value = low.toString(),
                        onValueChange = { onBoundsChange(index, it.toIntOrNull(), high) },
                        label = { Text("min") },
                        modifier = Modifier.weight(1f).height(53.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            cursorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.surface
                        ),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = high.toString(),
                        onValueChange = { onBoundsChange(index, low, it.toIntOrNull()) },
                        label = { Text("max") },
                        modifier = Modifier.weight(1f).height(53.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = GreenTextFieldColors,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Step6Screen(
    categoriesNames: List<String>,
    newOrderedCategories: List<String>,
    onReorder: (List<String>) -> Unit
) {
    val categoryList = remember {
        mutableStateListOf<Category>().apply {
            val base = if (newOrderedCategories.size != categoriesNames.size) {
                categoriesNames
            } else {
                newOrderedCategories
            }
            addAll(
                base.mapNotNull { name ->
                    Categories.expensesCategory.find { it.name == name }
                }
            )
        }
    }

    FormContainer {
        Title("Розставте пріоритети")
        TitleRow("Обирайте що для вас важливіше: чим більший пріоритет тим більше коштів отримає категорія.")

        val lazyListState = rememberLazyListState()
        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            categoryList.apply {
                add(to.index, removeAt(from.index))
            }
            onReorder(categoryList.map { it.name })
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 15.dp)
                .padding(bottom = 15.dp),
        ) {
            items(categoryList, key = { it.name }) { category ->
                ReorderableItem(reorderableLazyListState, key = category.name) { isDragging ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 4.dp)
                            .shadowsPlus(
                                type = ShadowsPlusType.SoftLayer,
                                color = Color.Black.copy(alpha = 0.25f),
                                radius = 4.dp,
                                offset = DpOffset(x = 2.dp, y = 2.dp),
                                spread = 1.dp,
                                shape = RectangleShape,
                                isAlphaContentClip = true
                            )
                            .background(
                                if (isDragging) MaterialTheme.colorScheme.background.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.primary
                            )
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(category.iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.drag),
                                contentDescription = "Move",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}


// Екран 7 - Фіксовані витрати
@Composable
fun Step7Screen(fixedCategoriesNames: List<String>, fixedAmount: List<Int>,
                onAmountChanged: (index:Int, amount:Int?) -> Unit){
    val fixedCategories = fixedCategoriesNames.mapNotNull { name ->
        Categories.expensesCategory.find { it.name == name }
    }
    FormContainer {
        Title("Вкажіть фіксовані витрати цього місяця")
        TitleRow("Знизу наведені фіксовані категорії витрат, які ви вказали. Введіть суму, яку вам залишилось оплатити цього місяця")
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fixedCategories.forEachIndexed { index, category ->
                val amount = fixedAmount.getOrNull(index) ?: 0
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(category.iconRes),
                            contentDescription = category.name,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.lock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.size(15.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(horizontal = 15.dp)) {
                    OutlinedTextField(
                        value = amount.toString(),
                        onValueChange = { onAmountChanged(index, it.toIntOrNull()) },
                        label = null,
                        modifier = Modifier.weight(1f).height(45.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
                            cursorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                            focusedLabelColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
                Line()
            }
        }
    }
}

// Екран 8 - Гаманці
@Composable
fun Step8Screen(wallets: List<Wallet>, selected: List<Wallet>, onWalletSelected: (Wallet)-> Unit){
    FormContainer {
        Title("Оберіть гаманці")
        TitleRow("Оберіть гаманці, доходи та витрати яких будуть враховуватись у плануванні та відстеженні прогресу")
        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            wallets.forEach { wallet ->
                val isChecked = selected.contains(wallet)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onWalletSelected(wallet) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            uncheckedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            checkmarkColor = MaterialTheme.colorScheme.onTertiary,

                        )
                    )
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if(isChecked)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 3.dp)
                    )
                }
            }


        }
    }
}

// Екран 9 - Кількість місяців
@Composable
fun Step9Screen(minMonth: Int?, months:Int?, goal: Int?, income: Int, onSelect: (Int)-> Unit){
    val minTerm = minMonth ?: 1
    val terms = listOf(
        minTerm,
        minTerm.times(1.3).roundToInt(),
        minTerm.times(1.5).roundToInt(),
        minTerm.times(2)
    )
    var selectedTerm by remember { mutableStateOf(minMonth) }
    var customSelected by remember { mutableStateOf(false) }
    var customTerm by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if(months == null){
            onSelect(minTerm)
        }
    }
    if(months != null && !terms.contains(months)){
        customSelected = true
        customTerm = months.toString()
    } else customTerm = ""


    FormContainer {
        Title("Оберіть термін накопичення фінансової цілі")
        TitleRow("Програма розрахувала для вас мінімальний можливий термін накопичення - обрати менший неможливо")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .border(1.dp, MaterialTheme.colorScheme.surface, RectangleShape)
                .padding(5.dp),
        ) {
            Text(
                text = "Мінімальний термін - $minMonth ${minMonth?.let { monthWordForm(it) }}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 15.dp)
        ) {
            // Варіант: свій термін
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (customSelected) MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                        else Color.Transparent)
                    .clickable { customSelected = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = customSelected,
                        onClick = { customSelected = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            unselectedColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Text(
                        text = "Свій термін",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (customSelected) {
                    OutlinedTextField(
                        value = customTerm,
                        onValueChange = { onSelect(it.toIntOrNull() ?: 0) },
                        placeholder = { Text(text="Введіть к-сть місяців...", style = MaterialTheme.typography.bodySmall)},
                        modifier = Modifier.padding(horizontal = 25.dp).padding(bottom = 2.dp).fillMaxWidth().height(45.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        colors = GreenTextFieldColors,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            }
            // Стандартні варіанти
            val termsToShow = if (minTerm<=3) {
                listOf(terms.first(), terms.last())
            } else {
                terms
            }
            termsToShow.forEachIndexed { index, term ->
                val saving = goal?.div(term)
                val percent = saving?.toDouble()?.div(income.toDouble())?.times(100)?.roundToInt()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (term == selectedTerm && !customSelected)
                                MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        )
                        .clickable {
                            selectedTerm = term
                            customSelected = false
                            onSelect(term)
                        }
                ) {
                    RadioButton(
                        selected = term == selectedTerm && !customSelected,
                        onClick = {
                            selectedTerm = term
                            customSelected = false
                            onSelect(term)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            unselectedColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Column (
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "$term ${monthWordForm(term)}"+
                                    when(index){
                                        0-> " (мінімум)"
                                        1-> " (на 30% довше)"
                                        2-> " (на 50% довше)"
                                        3-> " (вдвічі довше)"
                                        else -> ""
                                    },
                            style = MaterialTheme.typography.bodyLarge,
                            color = when(index) {
                                0 -> MaterialTheme.colorScheme.onSurface
                                1, 2 -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.surfaceDim
                            }
                        )
                        Text(
                            text = "Кількість відкладень: ≈ $saving - $percent% бюджету",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryScreen(state: FormScreenViewModel.UiState, navController: NavController){
    val textColor = MaterialTheme.colorScheme.primary
    // Спостерігаємо за isSuccess
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate("plan") {
                // щоб не повертатися назад на цю сторінку
                popUpTo("form") { inclusive = true }
            }
        }
    }
    if(state.isLoading){
        LoadingIndicator("Ваш план завантажується...")
    }else {
        FormContainer(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)) {
            Text(
                text = "Перевірте правильність введення даних",
                style = MaterialTheme.typography.headlineSmall.copy(textColor),
                modifier = Modifier.padding(bottom = 15.dp, top = 5.dp)
            )
            Line(textColor)
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                BorderedBox {
                    Text(
                        text = "1.",
                        style = MaterialTheme.typography.titleSmall.copy(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (state.isGoal) {

                        Column {
                            DiffStyleLine("Ціль - ", "'${state.goalName}' ${state.goalAmount} грн.")
                            DiffStyleLine("Термін - ", "${state.months} місяців.")
                        }
                    } else
                        DiffStyleLine("Планування без цілі", "")
                }
                BorderedBox {
                    Text(
                        text = "2.",
                        style = MaterialTheme.typography.titleSmall.copy(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Column {
                        DiffStyleLine("Щомісячний дохід - ", "${state.stableIncome} грн.")
                        DiffStyleLine("Дохід цього місяця - ", "${state.currentIncome} грн.")
                    }
                }

                BorderedBox {
                    val sortedBounds = state.bounds.zip(state.priorities)
                        .sortedBy { it.second } // сортуємо межі за пріоритетом
                        .map { it.first }
                    Text(
                        text = "3.",
                        style = MaterialTheme.typography.titleSmall.copy(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Column {
                        DiffStyleLine("Категорії за пріоритетами:", "")
                        Spacer(modifier = Modifier.height(8.dp))
                        state.newOrderCategories.forEachIndexed { index, category ->
                            DiffStyleLine(
                                "${index + 1}) $category - ",
                                "від ${sortedBounds[index].first} до ${sortedBounds[index].second} грн."
                            )
                        }
                    }
                }

                BorderedBox {
                    Text(
                        text = "4.",
                        style = MaterialTheme.typography.titleSmall.copy(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (state.fixedCategories.isEmpty()) {
                        DiffStyleLine("Немає фіксованих категорій", "")
                    } else {
                        Column {
                            DiffStyleLine("Фіксовані витрати:", "")
                            Spacer(modifier = Modifier.height(8.dp))
                            state.fixedCategories.forEachIndexed { index, category ->
                                DiffStyleLine(
                                    "${index + 1}) $category - ",
                                    "${state.fixedAmountStable[index]} грн."
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            DiffStyleLine(
                                "Залишилось заплатити цього місяця: ",
                                "${state.fixedAmountCurrent.sum()} грн."
                            )
                        }
                    }
                }
                BorderedBox {
                    Text(
                        text = "5.",
                        style = MaterialTheme.typography.titleSmall.copy(textColor),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Column {
                        DiffStyleLine("Гаманці, включені до обліку:", "")
                        Spacer(modifier = Modifier.height(8.dp))
                        state.wallets.forEachIndexed { index, wallet ->
                            DiffStyleLine(
                                "${index + 1}) ${wallet.name}",
                                ""
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun LoadingIndicator(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ScreenSize.height * 0.725f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                strokeWidth = 4.dp,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
