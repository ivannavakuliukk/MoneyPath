package com.example.moneypath.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.R
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.models.WalletSource
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.ui.elements.AppDatePickerDialog
import com.example.moneypath.ui.elements.AppDialog
import com.example.moneypath.ui.elements.AppSnackBar
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.elements.StatelessBottomBar
import com.example.moneypath.ui.elements.StatelessNavigationDrawer
import com.example.moneypath.ui.elements.StatelessNavigationRail
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.ui.viewmodel.MainScreenViewModel
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.formattedDate
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt


/*
    Сторінка "Головна сторінка"
    Параметр - NavController
    DI - MainScreenViewModel
 */

@Composable
fun MainScreen(navController: NavController, viewModel: MainScreenViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }

    MainScreenStateless(
        modifier = Modifier,
        state, snackBarHostState,
        clearError = {viewModel.clearError()}, onErrorChange = {viewModel.onErrorChange(it)},
        onAddTransactionClick = {navController.navigate("addtransaction/${state.date}/${state.isGoal}")},
        onTransactionClick = {navController.navigate(("transactioninfo/$it"))},
        onDateChange = {viewModel.onDateChange(it)},
        onWalletClick = { navController.navigate("editwallet/$it")},
        onWalletAddClick = { navController.navigate("addwallet") }
    )
}

@Composable
fun MainScreenStateless(modifier: Modifier = Modifier,
                        state: MainScreenViewModel.UiState, snackBarHostState: SnackbarHostState,
                        clearError: ()->Unit = {}, onErrorChange: (String?) -> Unit = {},
                        onAddTransactionClick: ()-> Unit = {}, onWalletClick: (String)-> Unit = {},
                        onWalletAddClick: ()-> Unit = {}, onDateChange: (Long) -> Unit = {},
                        onTransactionClick: (String) -> Unit = {}){
    SoftLayerShadowContainer {
        Scaffold(
            modifier = modifier,
            topBar = { MainTopAppBar(state.userName, state.userPhotoUrl) },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, snackbar = { data ->
                    AppSnackBar(data)
                })
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .clickable {
                            if (state.wallets.isEmpty()) {
                                onErrorChange("Щоб додати транзакцію, додайте хоча б один гаманець")
                            } else {
                                onAddTransactionClick()
                            }
                        }
                        .height(ScreenSize.height * 0.085f)
                        .width(ScreenSize.height * 0.085f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }, floatingActionButtonPosition = FabPosition.End
        )
        { innerPadding->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
            ) {
                item {
                    WalletsLazyRowWithIndicator(wallets = state.wallets, {onWalletClick(it)}, onWalletAddClick, state.isWalletsLoading) {
                        onErrorChange(it)
                    }
                }
                item {
                    if(state.isGoal && state.goalTransactionsAmount!=null){
                        GoalBox(state.goalName, state.goalTransactionsAmount, state.goalAmount)
                    }
                }
                item {
                    TransactionBox(state.date, state.transactions,state.isTransactionLoading, {onTransactionClick(it)},
                        {onDateChange(it)}, state.wallets)
                }
            }
            LaunchedEffect(state.error) {
                state.error?.let {
                    snackBarHostState.showSnackbar(it)
                    clearError()
                }
            }
        }
    }
}


// Верхня панель
@Composable
fun MainTopAppBar(userName: String, url: Uri?) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background).padding(10.dp)
        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.055f))
        if (url != null) {
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .weight(0.097f)
            )
        } else {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(0.097f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (userName.firstOrNull()?.uppercaseChar() ?: "?").toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.03f))
        Column(modifier = Modifier.weight(0.758f)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
            Text(
                text = "Привіт!\uD83D\uDC4B Раді Вас бачити!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .weight(0.06f)
        ) {
            Image(
                painter = painterResource(R.drawable.bell),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.weight(0.055f))
    }

}


// Гаманці
@Composable
fun WalletsLazyRowWithIndicator(wallets: List<Wallet>, onWalletClick: (id: String) -> Unit, onWalletAddClick: () -> Unit,
                                isLoading: Boolean, onErrorChange:(String?) -> Unit){
    val listState = rememberLazyListState()
    val itemsPerPage = 2
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    var pageIndex = when (firstVisibleIndex) {
        0 -> 0
        1 -> 1
        2 -> 2
        3 -> 3
        4 -> 4
        else -> (firstVisibleIndex + 1) / itemsPerPage
    }
    if(firstVisibleIndex == 0 && firstVisibleOffset > 90){
        pageIndex = 1
    }
    if(firstVisibleIndex == 1 && firstVisibleOffset > 120){
        pageIndex = 2
    }
    if(firstVisibleIndex == 2 && firstVisibleOffset > 120){
        pageIndex = 3
    }
    val totalPages = when(wallets.size ){
        2->2
        3->3
        4->4
        5->4
        else -> 0
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        // Заголовок
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .height(ScreenSize.height * 0.035f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.fillMaxWidth(0.055f))
            Icon(
                painter = painterResource(R.drawable.white_wallet),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.fillMaxWidth(0.033f))
            Text(
                text = "Гаманці",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
        }
        Spacer(
            modifier = Modifier
                .height(ScreenSize.height * 0.02f)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
        )
        // Гаманці
        if(isLoading){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScreenSize.height * 0.19f + 8.dp)
                    .background(MaterialTheme.colorScheme.background)
            ){
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier
                        .height(30.dp)
                        .align(Alignment.Center)
                )
            }
        }
        else {
            LazyRow(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(ScreenSize.width * 0.055f),
                contentPadding = PaddingValues(horizontal = ScreenSize.width * 0.055f),
                state = listState
            ) {
                items(wallets) { wallet ->
                    WalletCard(wallet = wallet) {
                        if(wallet.id != "mono") {
                            onWalletClick(wallet.id)
//                            navController.navigate("editwallet/$walletId")
                        }
                    }
                }
                item {
                    AddWalletCard(onClick = {
                        if(wallets.size > 5){
                            onErrorChange("Можна створити не більше трьох гаманців")
                        }
                        else{ onWalletAddClick() }
//                        navController.navigate("addwallet") }
                    })
                }
            }
            // Індикатор
            if (wallets.size > 1) {
                PagerIndicator(
                    totalPages = totalPages,
                    currentPage = pageIndex,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .height(12.dp)
                )
            }
        }
    }
}

// Картка гаманця
@Composable
fun WalletCard(wallet: Wallet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(ScreenSize.width * 0.38f)
            .height(ScreenSize.height * 0.19f)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 4.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(13.dp),
                isAlphaContentClip = true
            ),
        shape = RoundedCornerShape(13.dp),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick

    ) {
        Column(
            modifier = Modifier.padding(ScreenSize.width *0.04f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Image(
                painter =
                if(wallet.source == WalletSource.Api){
                    painterResource(R.drawable.mono)
                }else {
                    if (wallet.type == WalletType.Cash)
                        painterResource(R.drawable.cash)
                    else
                        painterResource(R.drawable.card)
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.23f)
                    .padding(bottom = 2.dp)
            )
            Text(
                text = wallet.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = "${wallet.balance}", style = MaterialTheme.typography.headlineSmall)
            Text(text = "UAH", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
        }
    }
}

// Картка "Додати гаманець"
@Composable
fun AddWalletCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(ScreenSize.width * 0.38f)
            .height(ScreenSize.height * 0.19f)
            .clickable { onClick() }
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 4.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(13.dp),
                isAlphaContentClip = true
            ),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation()
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.23f)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Додати гаманець",
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = ScreenSize.width *0.04f,
                        start = ScreenSize.width *0.04f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TransactionBox(
    date: Long,
    transactions: List<Transaction>,
    isTransactionLoading: Boolean,
    onTransactionClick: (String)-> Unit,
    onDateChange: (Long) -> Unit,
    wallets: List<Wallet>
){
    Column(
        modifier = Modifier
            .padding(horizontal = ScreenSize.width * 0.055f)
            .padding(bottom = 30.dp, top = 20.dp)
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
    ){
        var showDatePicker by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ScreenSize.height * 0.075f)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            Text(
                text= "Список транзакцій",
                style = MaterialTheme.typography.headlineSmall,
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .clickable { showDatePicker = !showDatePicker },
                contentAlignment = Alignment.BottomEnd
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = formattedDate(date),
                        style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.secondary)
                    )
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .padding(start = 5.dp)
                    )
                }
                if(showDatePicker) {
                    AppDatePickerDialog(date, onDateChange) {showDatePicker = false}
                }
            }
        }

        if(transactions.isEmpty()) {Line()}
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 7000.dp, min = ScreenSize.height * 0.11f),
            userScrollEnabled = false
        ) {
            if(isTransactionLoading){
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(ScreenSize.height * 0.11f),
                        contentAlignment = Alignment.BottomCenter) {
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.background,
                            trackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }
                }
            }else {
                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ScreenSize.height * 0.11f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Немає таранзакцій за обрану дату",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    items(transactions) { transaction ->
                        val transactionId = transaction.id
                        TransactionRow(
                            transaction,
                            wallets = wallets
                        ) {
                            onTransactionClick(transactionId)
//                            navController.navigate("transactioninfo/$transactionId")
                        }
                    }
                }
            }
        }
        Line()
    }
}

@Composable
fun TransactionRow(
    transaction:Transaction,
    wallets: List<Wallet>,
    onClick: () -> Unit
){
    val selectedCategory = findCategoryById(transaction.categoryId)
    val selectedWallet = wallets.first { it.id == transaction.walletId }
    val selectedWalletTo = wallets.firstOrNull{it.id == transaction.walletIdTo}
    Line()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ScreenSize.height * 0.11f)
            .clickable {
                onClick()
            }
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
                painter = painterResource(selectedCategory.iconRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.13f)
        )
        Column(
            modifier = Modifier
                .weight(0.65f)
                .padding(horizontal = 6.dp)
        ){
            Text(
                text = selectedCategory.name +
                        if(transaction.type == TransactionType.Transfer){
                            ", " + transaction.description
                        }else{
                            ""
                        }
                ,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )
            Text(
                text = selectedWallet.name +
                        if(transaction.description.isNotEmpty()&& transaction.type!= TransactionType.Transfer){
                            ", " + transaction.description
                        } else if(transaction.type== TransactionType.Transfer && selectedWalletTo!=null){
                            "->${selectedWalletTo.name}"
                        } else {
                            ""
                               }
                ,
                style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)),
                maxLines = 1,
                modifier = Modifier.padding(top = 1.dp),
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(modifier = Modifier.weight(0.35f)) {
            val sign = if(transaction.type == TransactionType.Income){
                "+"
            }else if(transaction.description == "Зовнішній(витрата)"){
                "-"
            }else ""

            Text(
                text = sign + transaction.amount.toString() + "₴" ,
                style = MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.onTertiary),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

    }
}

@Composable
fun GoalBox(goalName: String?, goalTransactionsAmount: Double?, goalAmount: Int?){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawBehind {
                val half = size.height / 2
                drawRect(
                    color = Color(0xFF55D6BE),
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
        Column (
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

        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)) {
                Text(
                    text = "Ціль – \"${goalName}\"",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${goalTransactionsAmount?.toInt()?.let { abs(it) }} з $goalAmount грн.",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)) {
                val planAmount = goalAmount
                val amount = goalTransactionsAmount?.let { abs(it) }
                var progress: Float = 0F
                var color = MaterialTheme.colorScheme.background
                if (amount != null) {
                    when {
                        planAmount?.toDouble() == 0.0 && amount == 0.0 -> {
                            progress = 0f
                        }
                        planAmount?.toDouble() == 0.0 && amount > 0.0 -> {
                            progress = 1f
                            color = Color.Red
                        }
                        amount <= planAmount!! -> {
                            progress = (amount / planAmount).toFloat()
                        }
                        else -> {
                            progress = 1f
                            color = Color.Red
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(end = 10.dp)
                        .fillMaxWidth()
                        .height(6.dp),
                    color = color,
                    trackColor = Color.LightGray,
                    strokeCap = StrokeCap.Round,
                    gapSize = 2.dp
                )
                Text(
                    text = (progress*100).roundToInt().toString() + "%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if(progress == 1F) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.background
                )
            }

        }
    }
}

@Composable
fun ObserveDialogs(goalTransactionsAmount: Double?, isContinued:Boolean,
                   goalAmount: Int?, planEnd: Long?, onContinued: ()-> Unit,
                   onDelete: ()-> Unit){
    // Обробка випадків - термін минув, або ціль досягнуто
    var showGoalReachedDialog by remember{ mutableStateOf(false) }
    var showTermReachedDialog by remember { mutableStateOf(false) }
    var showTermGoalReachedDialog by remember { mutableStateOf(false) }
    // Ціль досягнуто, термін не пройшов
    LaunchedEffect(goalTransactionsAmount) {
        if(!isContinued) {
            if (goalAmount != null && goalTransactionsAmount != null && planEnd != null) {
                if (abs(goalTransactionsAmount) >= goalAmount && planEnd > Calendar.getInstance().timeInMillis) {
                    showGoalReachedDialog = true
                }
            }
        }
    }
    if(showGoalReachedDialog){
        AppDialog(
            imageRes = R.drawable.happy_bunny,
            title = "Вітаємо!!",
            message = "Ви виконали поставлену ціль, хоч час ще залишився!\n Хочете створити новий план чи залишитись на поточному?",
            confirmText = "Поточний",
            onConfirm = {
//                viewModel.setContinued()
//                showGoalReachedDialog = false
                onContinued()
                        },
            dismissText = "Новий план",
            onDismiss = {
//                viewModel.deletePlan()
//                navController.navigate("form"){
//                    popUpTo(0){inclusive = true}
//                }
                onDelete()
            },
            cancelable = false
        )
    }
    LaunchedEffect(goalTransactionsAmount) {
        if(goalAmount != null && goalTransactionsAmount != null && planEnd != null){
            if (abs( goalTransactionsAmount) < goalAmount && planEnd <= Calendar.getInstance().timeInMillis) {
                showTermReachedDialog = true
            }else if((abs( goalTransactionsAmount) >=goalAmount && planEnd <= Calendar.getInstance().timeInMillis)){
                showTermGoalReachedDialog = true
            }
        }
    }
    if(showTermReachedDialog){
        AppDialog(
            imageRes = R.drawable.sad_cat,
            title = "На жаль...",
            message = "Ви не виконали поставлену ціль, а час плану вже минув. Давайте створимо новий?",
            confirmText = "Зрозуміло",
            onConfirm = {
//                viewModel.deletePlan()
//                navController.navigate("mainscreen"){
//                    popUpTo(0){inclusive = true}
//                }
                onDelete()
            },
            dismissText = "Новий план",
            onDismiss = {
//                viewModel.deletePlan()
//                navController.navigate("form"){
//                    popUpTo(0){inclusive = true}
//                }
                onDelete()
            },
            cancelable = false
        )
    }
    if(showTermGoalReachedDialog){
        AppDialog(
            imageRes = R.drawable.happy_bunny,
            title = "Вітаємо",
            message = "Ви виконали поставлену ціль, та й термін вже минув. Давайте створимо новий план?",
            confirmText = "Зрозуміло",
            onConfirm = {
//                viewModel.deletePlan()
//                navController.navigate("mainscreen"){
//                    popUpTo(0){inclusive = true}
//                }
                onDelete()
            },
            dismissText = "Новий план",
            onDismiss = {
//                viewModel.deletePlan()
//                navController.navigate("form"){
//                    popUpTo(0){inclusive = true}
//                }
                onDelete()
            },
            cancelable = false
        )
    }
}


fun previewUiStateExample(): MainScreenViewModel.UiState {
    val transactions = listOf(
        Transaction(
            id = "1",
            date = 1766851200,
            amount = 100.0,
            type = TransactionType.Expense,
            categoryId = "food",
            walletId = "1"
        )
    )
    val wallets = listOf(
        Wallet(
            id = "1",
            name = "Cash",
            type = WalletType.Cash,
            balance = 1000.0
        )
    )
    return MainScreenViewModel.UiState(
        wallets = wallets,
        transactions = transactions
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainScreenCompact() {
    val state = previewUiStateExample()
    MoneyPathTheme {
        Scaffold(Modifier.fillMaxSize(),
            bottomBar = { StatelessBottomBar("mainscreen") { } }
        ) {innerPadding->
            Spacer(Modifier.padding(innerPadding))
            MainScreenStateless(
                state = state,
                snackBarHostState = remember { SnackbarHostState() }
            ) { }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait"
)
@Composable
private fun MainScreenMedium() {
    val state = previewUiStateExample()
    MoneyPathTheme {
        Scaffold(Modifier.fillMaxSize()
        ) {innerPadding->
            Row(Modifier.fillMaxSize()) {
                Spacer(Modifier.padding(innerPadding))
                StatelessNavigationRail("mainscreen") { }
                MainScreenStateless(
                    state = state,
                    snackBarHostState = remember { SnackbarHostState() }
                ) { }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun MainScreenExpanded() {
    val state = previewUiStateExample()
        MoneyPathTheme {
            Scaffold(
                Modifier.fillMaxSize()
            ) { innerPadding ->
                Row(Modifier.fillMaxSize()) {
                    Spacer(Modifier.padding(innerPadding))
                    StatelessNavigationDrawer(modifier = Modifier.weight(0.25f), "mainscreen") { }
                    MainScreenStateless(
                        modifier = Modifier.weight(0.75f),
                        state = state,
                        snackBarHostState = remember { SnackbarHostState() }
                    ) { }
                }
            }
        }
}