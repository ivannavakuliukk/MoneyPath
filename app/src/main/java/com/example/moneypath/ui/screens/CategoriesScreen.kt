package com.example.moneypath.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.PieSlice
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.ui.elements.BottomNavigationBar
import com.example.moneypath.ui.elements.CategoryDonutChart
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarNoIcon
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.viewmodel.CategoriesViewModel
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.formatMonthYear
import com.example.moneypath.utils.getMonthBounds
import com.example.moneypath.utils.transactionWordForm
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CategoriesScreen(navController: NavController, viewModel: CategoriesViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    SoftLayerShadowContainer {
        Scaffold(
            topBar = {
                if (firstVisibleIndex == 0) {
                    CategoriesTopApp(state) {startDate, endDate ->viewModel.onDateRangeSelected(startDate, endDate)}
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
            }
        ) { innerPadding ->
            if (state.isLoading) LoadingDataScreen()
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(innerPadding)
                ) {
                    item {
                        val chartDataList = listOf(state.categoriesChartData, state.planChartData)
                        Column(
                            Modifier.wrapContentHeight().wrapContentWidth().background(
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
                       ButtonToFormScreen(state, navController, index = firstVisibleIndex)
                    }
                    item{
                        IncomeExpensesBox(state)
                    }
                    item {
                        CategoriesBox(state)
                    }
                }
            }
        }
    }
}


@Composable
fun CategoriesPieChart(chartData: List<PieSlice>, index: Int) {
    Box(Modifier.width(ScreenSize.width)) {
        val bottomPadding = if (chartData.isNotEmpty()) 65.dp else 25.dp
        val topPadding = if(chartData.isNotEmpty()) 60.dp else 30.dp
        CategoryDonutChart(
            chartData,
            Modifier.size(231.dp).padding(
                bottom = bottomPadding, top = topPadding
            ).align(Alignment.Center),
            index = index
        )
    }
}

@Composable
fun CategoriesBox(state: CategoriesViewModel.UiState) {
    Column(
        modifier = Modifier
            .padding(horizontal = ScreenSize.width * 0.055f)
            .padding(bottom = 30.dp, top = 15.dp)
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
            .padding(horizontal = ScreenSize.width * 0.035f)
            .padding(top = 0.dp, bottom = ScreenSize.width * 0.05f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(ScreenSize.height * 0.065f)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Список категорій",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        if(state.transactionList.isNotEmpty()) {
            val planAllocation = state.planAllocation
            // Створюємо відсортований список категорій за сумою amount
            val sortedTransactionList = state.transactionList
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
                    planAmount = if (state.isPlanned) planAllocation[categoryId] ?: 0.0 else null
                )
            }
            Line()
        }else{
            Line()
            Box(
                modifier = Modifier.fillMaxWidth().height(ScreenSize.height * 0.11f),
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
        }
    }
}

@Composable
fun ButtonToFormScreen(state: CategoriesViewModel.UiState, navController: NavController, index: Int){
    // Кнопка до форми для плану
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawBehind {
                val half = size.height / 2
                drawRect(
                    color = if(index == 0)Color(0xFF55D6BE)else Color(0xFF4FC3F7),
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
                .padding(horizontal = ScreenSize.width * 0.055f, vertical = 7.dp)
                .wrapContentHeight()
                .shadowsPlus(
                    type = ShadowsPlusType.SoftLayer,
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = 4.dp,
                    offset = DpOffset(x = 2.dp, y = 2.dp),
                    spread = 1.dp,
                    shape = RoundedCornerShape(15.dp),
                    isAlphaContentClip = true
                )
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = ScreenSize.width * 0.035f, vertical = 5.dp)
                .clickable {
                    navController.navigate("form")
                }
        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if(state.isPlanned) "Змінити план бюджету" else "Розрахувати план бюджету",
                    style = MaterialTheme.typography.titleMedium
                )
                Image(
                    painter = painterResource(R.drawable.arrow_),
                    contentDescription = null,
                    modifier = Modifier.width(50.dp).height(30.dp)
                )
            }
        }
    }
}

@Composable
fun IncomeExpensesBox(state: CategoriesViewModel.UiState){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenSize.width * 0.055f, vertical = 7.dp)
            .wrapContentHeight()
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 2.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(15.dp),
                isAlphaContentClip = true
            )
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = ScreenSize.width * 0.035f, vertical = 5.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth()
        ){
            Text(
                text = "Доходи",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = state.incomeAmount.toString() + "₴",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        Line(MaterialTheme.colorScheme.onTertiary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth()
        ){
            Text(
                text = "Витрати",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = state.expensesAmount.toString() + "₴",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Line(MaterialTheme.colorScheme.onSurface)
        Box(Modifier.height(10.dp))
    }
}

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
            modifier = Modifier.weight(0.65f).padding(horizontal = 6.dp)
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
                    modifier = Modifier.padding(top = 3.dp).fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

// Top bar з якого відкривається docked date picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTopApp(state: CategoriesViewModel.UiState, onDateRangeSelected: (Long, Long)-> Unit){
    Row(
        modifier = Modifier.fillMaxWidth().height(ScreenSize.height *0.097f).background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var showDatePickerDocked by remember { mutableStateOf(false) }
        val (startDateSec, endDateSec) = state.dates ?: getMonthBounds()
        val todayDate = Calendar.getInstance()
        // Визначаєм можливі дати - не більше за сьогодні
        val selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayDate.timeInMillis
            }
        }
        val datePickerState = rememberDatePickerState(selectableDates = selectableDates, initialSelectedDateMillis = startDateSec*1000)
        // відстежуємо натискання користувачем на дату
        LaunchedEffect(datePickerState.selectedDateMillis) {
            val selectedRangeInMillis =
                getMonthBounds(datePickerState.selectedDateMillis ?: 0L)
            onDateRangeSelected(
                selectedRangeInMillis.first,
                selectedRangeInMillis.second
            )
            showDatePickerDocked = false
        }
        Text(
            text = formatMonthYear(startDateSec, endDateSec),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .clickable { showDatePickerDocked = !showDatePickerDocked }
        ) {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxHeight(0.4f).padding(start = 5.dp).align(Alignment.Center)
            )
        }
        if(showDatePickerDocked){
            Popup(onDismissRequest = {showDatePickerDocked = false}, alignment = Alignment.TopStart)
            {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = ScreenSize.height* 0.07f).wrapContentHeight()
                        .background(Color.Transparent).scale(0.9f).shadow(5.dp)
                ){
                    Column {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            title = null,
                            headline = null,
                            colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
                                dayContentColor = MaterialTheme.colorScheme.secondary,
                                disabledDayContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                selectedDayContentColor = MaterialTheme.colorScheme.background,
                                selectedDayContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                                todayContentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                }
            }
        }
    }
}



