package com.example.moneypath.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.BudgetPlanDB
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.ui.elements.dialogs.AppAlertDialog
import com.example.moneypath.ui.elements.CategoryHorizontalBarChart
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarTwoLinesNoIcon
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.elements.PlanContainer
import com.example.moneypath.ui.elements.dialogs.SettingDialog
import com.example.moneypath.ui.elements.AppSplitButton
import com.example.moneypath.ui.preview.PlanParamsProvider
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.ui.viewmodel.PlanScreenViewModel
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.TimeDiff
import com.example.moneypath.utils.monthWordForm
import com.example.moneypath.utils.rotateVertically
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import kotlin.math.roundToInt

@Composable
fun PlanScreen(navController: NavController, viewModel: PlanScreenViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    val totalPages = 2
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })
    var moveToSecondPage by remember { mutableStateOf(false) }
    LaunchedEffect(moveToSecondPage ) {
        if(moveToSecondPage) {
            pagerState.scrollToPage(1)
            moveToSecondPage = false
        }
    }
    LaunchedEffect (state.reloadNeeded){
        if(state.reloadNeeded){
            navController.navigate("plan") {
                popUpTo("plan") { inclusive = true }
            }
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    var showDialog by remember{ mutableStateOf(false)}
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDialog && state.setUp != null) {
        state.walletNames?.let {
            state.plan?.let { it1 ->
                SettingDialog(
                    onDismiss = { showDialog = false },
                    settings = state.setUp,
                    isGoal = state.isGoal,
                    walletNames = it,
                    it1.plan_start
                )
            }
        }
    }
    if(showDeleteDialog){
        AppAlertDialog(
            text = "Ви впевнені, що хочете видалити поточний план?",
            onConfirmClick = {viewModel.deletePlan()
                showDeleteDialog = false
            }
        ) { showDeleteDialog = false}
    }
    SoftLayerShadowContainer {
        Scaffold(
            bottomBar = {
                Column {
                    PagerIndicator(
                        totalPages = totalPages,
                        currentPage = pagerState.currentPage
                    )
                }},
            topBar = {
                MyTopAppBarTwoLinesNoIcon(
                title = if (pagerState.currentPage == 0) "План витрат" else "Альтернативні плани",
                    text = if(state.isGoal) "Для доcягнення цілі" else "Без досягнення цілі",
                background = MaterialTheme.colorScheme.background
            )
            },
            floatingActionButton = {
                AppSplitButton(
                    isPlanned = state.isPlanned,
                    onSettingClick = {
                        viewModel.getSetUp()
                        showDialog = true
                    },
                    onDeleteClick = { showDeleteDialog = true },
                    onAdditionalClick = {
                        moveToSecondPage = true
                    },
                    onClick = {
                        if (state.isWallets) {
                            navController.navigate("form")
                        } else {
                            viewModel.onErrorChange("Щоб розрахувати план, додайте хоча б один гаманець")
                        }
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
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
            if(state.isLoading){
                LoadingDataScreen()
            }else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { page ->
                    when (page) {
                        0 -> CurrentPlan(state){viewModel.deletePlan()}
                        1 -> if(state.isPlanned && state.isGoal) 
                                PlansGrid(state.plan, state.additionalPlans){viewModel.selectAdditionalPlan(it)} 
                        else OtherPlans()
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CurrentPlan(state: PlanScreenViewModel.UiState,
                 onDelete: ()-> Unit)
{
    var showDialog by remember{ mutableStateOf(false)}
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDialog && state.setUp != null) {
        state.walletNames?.let {
            state.plan?.let { it1 ->
                SettingDialog(
                    onDismiss = { showDialog = false },
                    settings = state.setUp,
                    isGoal = state.isGoal,
                    walletNames = it,
                    it1.plan_start
                )
            }
        }
    }
    if(showDeleteDialog){
        AppAlertDialog(
            text = "Ви впевнені, що хочете видалити поточний план?",
            onConfirmClick = {onDelete()
                showDeleteDialog = false
            }
        ) { showDeleteDialog = false}
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            PlanContainer {
                Text(
                    text =
                    if(!state.isPlanned){
                        "План не створено"
                    }else {
                        if (state.isGoal && state.goalAmount != null && state.plan != null) {
                            "Ціль – ${state.goalAmount} грн. за ${state.plan.months} місяців."
                        } else "План без цілі"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.surfaceDim),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .fillMaxWidth()
                )
                Line(MaterialTheme.colorScheme.surfaceDim)
                if(state.isPlanned) {
                    TimeBox(state.period)

                    PlanBarChart(state.fullCategoriesMap)

                    val categories = state.fullCategoriesMap
                    val iconsList = categories.keys.map { name ->
                        Categories.expensesCategory.find { it.name == name }?.iconRes
                            ?: Categories.otherCategory[0].iconRes
                    }
                    val income = state.plan?.income ?: 1
                    val percentList = categories.entries.map {
                        (it.value / income) * 100
                    }
                    categories.entries.toList().forEachIndexed { index, entry ->
                        CategoryLine(
                            iconsList[index],
                            entry.key,
                            entry.value,
                            String.format("%.1f", percentList[index])
                        )
                    }
                    state.plan?.let {
                        CategoryLine(
                            iconRes = R.drawable.remainder,
                            amount = it.stable_leftover,
                            name = "Залишок",
                            percent = String.format("%.1f", (it.stable_leftover/income)*100)
                        )
                    }
                    Spacer(Modifier.size(25.dp))
                    Line()
                }else {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(horizontal = 15.dp, vertical = 25.dp)) {
                        Text(
                            text = "Ви ще не планували свій бюджет ... \nтут буде наведений майбутній план витрат",
                            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.6f)),
                            modifier = Modifier.align(Alignment.TopCenter),
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            painter = painterResource(R.drawable.chart),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.3f),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(250.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryLine(iconRes: Int, name:String, amount: Double, percent: String){
    Line()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((ScreenSize.height * 0.08f).coerceAtLeast(50.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.1f)
        )
        Column(
            modifier = Modifier
                .weight(0.6f)
                .padding(horizontal = 6.dp)
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )
        }
        Column(modifier = Modifier.weight(0.4f)) {
            Text(
                text = amount.toInt().toString() + "₴" ,
                style = MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.onTertiary),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

    }
}

@Composable
fun OtherPlans(){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            PlanContainer {
                Box(modifier = Modifier
                    .height(180.dp)
                    .padding(10.dp)
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Тут наводяться альтернативні плани витрат для планування з ціллю.",
                        style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.6f)),
                        modifier = Modifier.align(Alignment.BottomCenter),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlansGrid(currentPlan: BudgetPlanDB?, additionalPlans:List<BudgetPlanDB>, onPlanClick: (Int)-> Unit){
    var showPlanDialog by remember{ mutableStateOf(false) }
    var planIndex by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    if(showPlanDialog){
        AdditionalPlanDialog(
            onConfirmClick = {
                onPlanClick(planIndex)
                showPlanDialog = false},
            onCancelClick = {showPlanDialog = false},
            name = name,
            plan = additionalPlans[planIndex]
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(15.dp))
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // Визначаємо додаткові плани, додаємо поточний наперед
        val plans = additionalPlans
        val updatedPlans = listOf(currentPlan) + plans
        // Даємо назву планам
        val names: List<String> = updatedPlans.map { plan ->
            if (plan == null || currentPlan == null) {
                ""
            } else if (plan.months == currentPlan.months) {
                "Поточний план"
            } else if (plan.months!! < currentPlan.months!!) {
                "Мінімальний план"
            } else if (plan.months == currentPlan.months * 2) {
                "Вдвічі довше"
            } else {
                val percent = ((((plan.months.toDouble() / currentPlan.months.toDouble()) - 1) * 100) / 10).roundToInt() * 10
                "На $percent% довше"
            }
        }
        itemsIndexed(updatedPlans){index, plan ->
            if(plan != null) {
                AdditionalPlanCard(
                    index, names[index], plan
                ) {
                    if (index != 0) {
                        planIndex = index - 1
                        name = names[index]
                        showPlanDialog = true
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalPlanDialog(onConfirmClick: () -> Unit, onCancelClick: ()-> Unit, plan: BudgetPlanDB, name: String) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    if(showConfirmDialog){
        AppAlertDialog(
            text = "Ви впевнені що хочете обрати цей план замість поточного? Весь прогрес видалиться, це незворотня дія!",
            onConfirmClick = {
                showConfirmDialog = false
                onConfirmClick()
            }) {showConfirmDialog = false }
    }
    AlertDialog(
        onDismissRequest = onCancelClick,
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = (ScreenSize.width * 0.055f).coerceAtLeast(15.dp))
                    .padding(bottom = 15.dp, top = 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (name == "Мінімальний план") {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
                Line()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 5.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.1f))
                        .padding(5.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            text = "Термін ${plan.months} ${
                                plan.months?.let {
                                    monthWordForm(it)
                                }
                            }",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "Щомісячні відкладення:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        text = "${plan.stable_savings?.toInt()} грн. – ${plan.goal_percent}% доходу",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 17.dp)
                        .padding(5.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 15.dp)
                    ,
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    val sortedPlan = plan.stable_months_allocation
                    .toList()
                    .sortedByDescending { it.second }
                    .toMap()
                    sortedPlan.forEach { entry ->
                        Text(
                            text = "${findCategoryById( entry.key).name} - ${entry.value.toInt()} грн",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    }
                    plan.stable_fixed.forEach { entry ->
                        Text(
                            text = "${findCategoryById( entry.key).name} - ${entry.value.toInt()} грн",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "Залишок - ${plan.stable_leftover.toInt()} грн",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onCancelClick) {
                        Text("Закрити", color = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {showConfirmDialog = true}) {
                        Text("Обрати план", color = Color.Red)
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingDataScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        ContainedLoadingIndicator(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            indicatorColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(55.dp)
        )
    }
}

@Composable
fun AdditionalPlanCard(index: Int, name: String, plan: BudgetPlanDB, onClick: ()-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        border = if (index == 0) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null,
        shape = RoundedCornerShape(13.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = if(name == "Мінімальний план"){
                    MaterialTheme.colorScheme.onSurface
                }else{
                    MaterialTheme.colorScheme.onPrimary
                },
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .padding(horizontal = 12.dp)
            )
            Line(modifier = Modifier.padding(horizontal = 12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 7.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.1f))
                    .padding(vertical = 4.dp, horizontal = 11.dp)
            ){
                Row() {
                    Image(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = null,
                        modifier = Modifier
                            .size(17.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "Термін ${plan.months} ${
                            plan.months?.let {
                                monthWordForm(it)
                            }
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
                Text(
                    text = "Щоміс. відкладення:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${plan.stable_savings?.toInt()} грн. – ${plan.goal_percent}%",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val top2 = plan.stable_months_allocation
                    .toList()
                    .sortedByDescending { it.second }
                    .take(2)
                    .toMap()

                top2.forEach { entry ->
                    Text(
                        text = "${findCategoryById( entry.key).name} - ${entry.value.toInt()} грн",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    )
                }
                Text(
                    text = "...",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TimeBox(period: TimeDiff?) {
    Box(Modifier
        .fillMaxWidth()
        .padding(vertical = 15.dp, horizontal = 25.dp)
        .border(1.dp, MaterialTheme.colorScheme.surfaceDim, RectangleShape)
        .padding(5.dp)
    ) {
        Text(
            text= "⏳ Залишилось ${period?.months} місяців та ${period?.days} днів",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surfaceDim,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PlanBarChart(fullCategoriesMap: Map<String, Double>) {
    var visible by remember { mutableStateOf(true) }
    Box(Modifier.heightIn(min = (fullCategoriesMap.size * 25).dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Колір категорії",
                modifier = Modifier.rotateVertically(false),
                style = MaterialTheme.typography.bodySmall,
                color = if (visible) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else Color.Transparent
            )
            CategoryHorizontalBarChart(fullCategoriesMap)
        }
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 25.dp),
        text = "гривень",
        style = MaterialTheme.typography.bodySmall,
        color = if (visible) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        else Color.Transparent,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, widthDp = 360, group = "Additional")
@Composable
private fun AdditionalPlanDialogPreview(
    @PreviewParameter(PlanParamsProvider::class)
    plan: BudgetPlanDB
) {
    MoneyPathTheme {
        AdditionalPlanDialog(
            onConfirmClick ={},
            onCancelClick = {},
            plan = plan,
            name = "Мінімальний план"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF55D6BE, group = "Additional")
@Composable
private fun AdditionalPlanCardPreview( @PreviewParameter(PlanParamsProvider::class)
                                       plan: BudgetPlanDB) {
    MoneyPathTheme {
        Box(Modifier
            .width(180.dp)
            .padding(10.dp)) {
            AdditionalPlanCard(
                1,
                "Мінімальний план",
                plan
            ) { }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF55D6BE, widthDp = 360,
    device = "spec:width=1080px,height=1500px,dpi=440", group = "Additional"
)
@Composable
private fun PlansGridPreview( @PreviewParameter(PlanParamsProvider::class)
                                       plan: BudgetPlanDB) {
    val additionalPlans = List(3){plan}
    MoneyPathTheme {
            PlansGrid(
                plan,
                additionalPlans
            ) { }
    }
}

@Preview(showBackground = true, widthDp = 360, group = "Additional",
    device = "spec:width=1080px,height=1500px,dpi=440"
)
@Composable
private fun OtherPlansEmptyPreview() {
    MoneyPathTheme {
        OtherPlans()
    }
}

@Preview(showBackground = true, widthDp = 360, group = "Current")
@Composable
private fun CategoryLinePreview() {
    MoneyPathTheme {
        CategoryLine(
            R.drawable.category_taxi,
            "Таксі",
            amount = 2000.0,
            percent = "34.5"
        )
    }
}

@Preview(showBackground = true, widthDp = 360, group = "Current")
@Composable
private fun TimeBoxPreview() {
    MoneyPathTheme {
        TimeBox(TimeDiff(2, 1))
    }
}




