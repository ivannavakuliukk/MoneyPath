package com.example.moneypath.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.PieSlice
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.ui.elements.CategoriesTopApp
import com.example.moneypath.ui.elements.CategoryDonutChart
import com.example.moneypath.ui.elements.ContainerForDataBox
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarNoIcon
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.elements.ScrollToTopButton
import com.example.moneypath.ui.preview.CategoriesBoxProvider
import com.example.moneypath.ui.preview.DonutChartParamsProvider
import com.example.moneypath.ui.preview.TransactionInfo
import com.example.moneypath.ui.preview.TransactionInfoProvider
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.ui.viewmodel.CategoriesViewModel
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.cardShadow
import com.example.moneypath.utils.transactionWordForm
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

@Composable
fun CategoriesScreen(navController: NavController, viewModel: CategoriesViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val columnState = rememberLazyListState()
    val showScrollToTopButton by remember {
        derivedStateOf {
            columnState.firstVisibleItemIndex > 0
        }
    }
    var scrollToFirstItem by remember { mutableStateOf(false) }
    LaunchedEffect(scrollToFirstItem) {
        if(scrollToFirstItem){
            columnState.animateScrollToItem(index = 0)
            scrollToFirstItem = false
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    SoftLayerShadowContainer {
        Scaffold(
            topBar = {
                if (firstVisibleIndex == 0 ) {
                    CategoriesTopApp(state.dates) { startDate,
                                                    endDate ->
                        viewModel.onDateRangeSelected(startDate, endDate)
                    }
                } else {
                    MyTopAppBarNoIcon("План витрат", MaterialTheme.colorScheme.tertiaryContainer)
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
            },
            floatingActionButton = {
                if(showScrollToTopButton){
                    ScrollToTopButton {
                        scrollToFirstItem = true
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            if (state.isLoading) LoadingDataScreen()
            else {
                LazyColumn(
                    state = columnState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(innerPadding)
                ) {
                    item {
                        val chartDataList = listOf(state.categoriesChartData, state.planChartData)
                        Column(
                            Modifier
                                .wrapContentHeight()
                                .wrapContentWidth()
                                .background(
                                    if (firstVisibleIndex == 0) {
                                        MaterialTheme.colorScheme.background
                                    } else {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    }
                                )
                        ) {
                            LazyRow(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
                            ) {
                                items(chartDataList.size) { index ->
                                    CategoriesPieChart(chartDataList[index], index)
                                }
                            }
                            PagerIndicator(
                                totalPages = 2,
                                currentPage = firstVisibleIndex,
                                background = Color.Transparent
                            )
                        }
                    }
                    item{
                       ButtonToFormScreen(
                           index = firstVisibleIndex,
                           isPlanned = state.isPlanned) {navController.navigate("form")}
                    }
                    item{
                        IncomeExpensesBox(state.incomeAmount, state.expensesAmount)
                    }
                    item {
                        CategoriesBox(state.transactionList, state.planAllocation, state.isPlanned)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesPieChart(chartData: List<PieSlice>, index: Int) {
    Box(Modifier.fillMaxWidth()) {
        val bottomPadding = if (chartData.isNotEmpty()) 65.dp else 25.dp
        val topPadding = if(chartData.isNotEmpty()) 60.dp else 30.dp
        CategoryDonutChart(
            chartData,
            Modifier
                .size(231.dp)
                .padding(
                    bottom = bottomPadding, top = topPadding
                )
                .align(Alignment.Center),
            index = index
        )
    }
}

@Composable
fun CategoriesBox(transactionList: Map<String, List<Transaction>>, planAllocation: Map<String, Double>, isPlanned: Boolean) {
    ContainerForDataBox(showTitle = false) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((ScreenSize.height * 0.065f).coerceAtLeast(40.dp))
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Список категорій",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        if(transactionList.isNotEmpty()) {
            val planAllocation = planAllocation
            // Створюємо відсортований список категорій за сумою amount
            val sortedTransactionList = transactionList
                .entries
                .sortedByDescending { (_, transactions) ->
                    transactions.sumOf { abs(it.amount) }
                }

            sortedTransactionList.forEachIndexed { index, (categoryId, transactions) ->
                val category = findCategoryById(categoryId)
                PlannedCategoryLine(
                    iconRes = category.iconRes,
                    name = category.name,
                    amount = transactions.sumOf { abs(it.amount) },
                    transactionSize = transactions.size,
                    planAmount = if (isPlanned) planAllocation[categoryId] ?: 0.0 else null
                )
            }
            Line()
        }else{
            Line()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((ScreenSize.height * 0.11f).coerceAtLeast(75.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Немає транзакцій за ці дати",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Line()
        }
    }
}

@Composable
fun ButtonToFormScreen(isPlanned: Boolean, index: Int, onClick:()->Unit){
    // Кнопка до форми для плану
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawBehind {
                val half = size.height / 2
                drawRect(
                    color = if (index == 0) Color(0xFF55D6BE) else Color(0xFF4FC3F7),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, half)
                )
                drawRect(
                    color = Color.White,
                    topLeft = Offset(0f, half),
                    size = Size(size.width, half)
                )
            }
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(15.dp),
                    vertical = 7.dp
                )
                .wrapContentHeight()
                .cardShadow()
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(
                    horizontal = (ScreenSize.width * 0.035f).coerceAtLeast(10.dp),
                    vertical = 5.dp
                )
                .clickable {
                    onClick()
                }
        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if(isPlanned) "Змінити план бюджету" else "Розрахувати план бюджету",
                    style = MaterialTheme.typography.titleMedium
                )
                Image(
                    painter = painterResource(R.drawable.arrow_),
                    contentDescription = null,
                    modifier = Modifier
                        .width(50.dp)
                        .height(30.dp)
                )
            }
        }
    }
}

@Composable
fun IncomeExpensesBox(incomeAmount: Double, expensesAmount: Double){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(15.dp), vertical = 7.dp)
            .wrapContentHeight()
            .cardShadow()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = (ScreenSize.width * 0.035f).coerceAtLeast(10.dp), vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "Доходи",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = incomeAmount.toString() + "₴",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        Line(MaterialTheme.colorScheme.onTertiary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "Витрати",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = expensesAmount.toString() + "₴",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Line(MaterialTheme.colorScheme.onSurface)
        Box(Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlannedCategoryLine(
    iconRes: Int,
    name: String,
    amount: Double,
    planAmount: Double?,
    transactionSize: Int
) {
    Line()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var progress: Float = 0.0f
        var color: Color = MaterialTheme.colorScheme.background
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(35.dp)
        )
        Column(
            modifier = Modifier
                .weight(0.65f)
                .padding(horizontal = 6.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Text(
                text = transactionSize.toString() + " " + transactionWordForm(transactionSize),
                style = MaterialTheme.typography.displayLarge.copy(
                    MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.6f
                    )
                ),
                modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
                maxLines = 2
            )
            if(planAmount != null) {
                // Прогрес бар
                when {
                    planAmount == 0.0 && amount == 0.0 -> {
                        progress = 0f
                    }

                    planAmount == 0.0 && amount > 0.0 -> {
                        progress = 1f
                        color = Color.Red
                    }

                    amount <= planAmount -> {
                        progress = (amount / planAmount).toFloat()
                    }

                    else -> {
                        progress = 1f
                        color = Color.Red
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = color,
                    trackColor = Color.LightGray,
                    strokeCap = StrokeCap.Round,
                    gapSize = 2.dp
                )
                Text(
                    text = (progress * 100).roundToInt().toString() + "%",
                    style = MaterialTheme.typography.displaySmall.copy(
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.6f
                        )
                    ),
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }
        }
        Column(modifier = Modifier.weight(0.35f)) {
            Text(
                text = amount.toString() + "₴",
                style = MaterialTheme.typography.headlineSmall,
                color = if(progress == 1f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            if(planAmount!=null) {
                Text(
                    text = planAmount.toString() + "₴",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.8f
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CategoryPieChartPreview(
    @PreviewParameter(DonutChartParamsProvider::class)
    data: Pair<List<PieSlice>, Int>
) {
    MoneyPathTheme {
        Box(Modifier
            .wrapContentSize()
            .background(
                if (data.second == 0) MaterialTheme.colorScheme.background else Color(0xFF4FC3F7)
            )) {
            CategoriesPieChart(data.first, data.second)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ButtonToFormPreview() {
    MoneyPathTheme {
        ButtonToFormScreen(
            true,
            0
        ) { }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun IncomeExpensesBoxPreview() {
    MoneyPathTheme {
        IncomeExpensesBox(
            incomeAmount = 10000.0,
            expensesAmount = 524.5
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CategoryLinePreview(
    @PreviewParameter(TransactionInfoProvider::class)
    info: TransactionInfo
) {
    MoneyPathTheme {
        PlannedCategoryLine(
            iconRes = info.icon,
            name = info.name,
            amount = info.amount,
            planAmount = info.planAmount,
            transactionSize = info.transactionSize,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CategoriesBoxPreview(
    @PreviewParameter(CategoriesBoxProvider::class)
    data: Triple<Map<String, List<Transaction>>, Map<String, Double>, Boolean>
) {
    MoneyPathTheme {
        CategoriesBox(
            data.first,
            data.second,
            data.third
        )
    }
}

