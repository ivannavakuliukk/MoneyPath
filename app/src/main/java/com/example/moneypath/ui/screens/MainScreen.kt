package com.example.moneypath.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.embedding.EmbeddingBounds
import coil.compose.rememberAsyncImagePainter
import coil.size.Dimension
import com.example.moneypath.LocalAppWindowInfo
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
import com.example.moneypath.ui.elements.InfiniteLinearWavyIndicator
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.elements.StatelessBottomBar
import com.example.moneypath.ui.elements.StatelessNavigationDrawer
import com.example.moneypath.ui.elements.StatelessNavigationRail
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.ui.viewmodel.MainScreenViewModel
import com.example.moneypath.utils.Dimensions
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.formattedDate
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.ceil
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
        onWalletAddClick = { navController.navigate("addwallet") },
        onMonoAdd = {navController.navigate("other")}
    )
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreenStateless(
    modifier: Modifier = Modifier,
    state: MainScreenViewModel.UiState,
    snackBarHostState: SnackbarHostState,
    clearError: ()->Unit = {},
    onErrorChange: (String?) -> Unit = {},
    onAddTransactionClick: ()-> Unit = {},
    onWalletClick: (String)-> Unit = {},
    onWalletAddClick: ()-> Unit = {},
    onDateChange: (Long) -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
    onMonoAdd: ()-> Unit)
{
    // розміри елементів, які залежать від екрану
    val sizeClass = LocalAppWindowInfo.current.windowSizeClass
    val dimensions = when {
        // Expanded
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
            Dimensions(
                cardHeight = 120.dp, cardWeight = 350.dp,
                screenSize = "Expanded", cornerRadius = 23.dp,
                verticalPadding = 25.dp, iconSize = 50.dp,
                horizontalPadding = 30.dp, pagerSize = 15.dp,
                progressBarHeight = 10.dp, lineWidth = 1.8.dp, buttonSize = 70.dp
            )
        // Medium
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
            Dimensions(
                cardHeight = 220.dp, cardWeight = 220.dp,
                screenSize = "Medium", cornerRadius = 20.dp,
                verticalPadding = 25.dp, iconSize = 50.dp,
                horizontalPadding = 30.dp, pagerSize = 15.dp,
                lineWidth = 1.8.dp, buttonSize = 70.dp
            )
        // Compact
        else ->
            Dimensions()
    }

    // Контент
    SoftLayerShadowContainer {
        Scaffold(
            modifier = modifier,
            topBar = { MainTopAppBar(state.userName, state.userPhotoUrl, dimensions) },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, snackbar = { data ->
                    AppSnackBar(data)
                })
            },
            floatingActionButton = {
                AppFabMenu(dimensions=dimensions,
                    onAddWalletClick =  onWalletAddClick, onAddTransactionClick = onAddTransactionClick,
                    onAddMonoClick = onMonoAdd)

            }, floatingActionButtonPosition = FabPosition.End
        )
        {innerPadding->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
            ) {
                item {
                    WalletsLazyRowWithIndicator(dimensions, wallets = state.wallets, {onWalletClick(it)}, onWalletAddClick, state.isWalletsLoading) {
                        onErrorChange(it)
                    }
                }
                item {
                    if(state.isGoal && state.goalTransactionsAmount!=null){
                        GoalBox(dimensions= dimensions,state.goalName, state.goalTransactionsAmount, state.goalAmount)
                    }
                }
                item {
                    TransactionBox(dimensions,state.date, state.transactions,state.isTransactionLoading, {onTransactionClick(it)},
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


data class FabAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppFabMenu(modifier: Modifier = Modifier, dimensions: Dimensions,
               onAddWalletClick: ()-> Unit,
               onAddTransactionClick: ()-> Unit,
               onAddMonoClick: ()-> Unit) {
    var expanded by remember {mutableStateOf(false)}
    val actions = listOf(
        FabAction("Додати гаманець", ImageVector.vectorResource(R.drawable.wallet_icon), onAddWalletClick),
        FabAction("Додати гаманець mono", ImageVector.vectorResource(R.drawable.mono_wallet_icon), onAddMonoClick),
        FabAction("Додати транзакцію", ImageVector.vectorResource(R.drawable.transaction_icon), onAddTransactionClick))
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = {expanded = it},
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.tertiary,
                    finalColor = Color(0xFF204FF8)
                ),
                containerSize = {dimensions.buttonSize}
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if(expanded) R.drawable.close_icon else R.drawable.add_icon,
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSize*0.9f),
                    contentDescription = null
                )
            }
        }
    ) {
        actions.forEach { item->
            FloatingActionButtonMenuItem(
                onClick = {item.onClick()},
                text = {Text(item.label, style = MaterialTheme.typography.bodyMedium) },
                icon = {Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(dimensions.iconSize*0.9f))},
                modifier = Modifier.height(dimensions.buttonSize),
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}



// Верхня панель
@Composable
fun MainTopAppBar(userName: String, url: Uri?, dimensions: Dimensions) {
    val sizeClass = LocalAppWindowInfo.current.windowSizeClass.windowWidthSizeClass
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
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
                    .weight(if (sizeClass == WindowWidthSizeClass.EXPANDED) 0.075f else 0.097f)
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
        Column(modifier = Modifier.weight(0.72f)) {
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
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WalletsLazyRowWithIndicator(dimensions: Dimensions, wallets: List<Wallet>, onWalletClick: (id: String) -> Unit, onWalletAddClick: () -> Unit,
                                isLoading: Boolean, onErrorChange:(String?) -> Unit){
    val listState = rememberLazyListState()
    val itemsPerPage = 2
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    var pageIndex = when (firstVisibleIndex) {
        in 0..4 -> firstVisibleIndex
        else -> (firstVisibleIndex + 1) / itemsPerPage }
    if(firstVisibleIndex == 0 && firstVisibleOffset > 90){ pageIndex = 1 }
    if(firstVisibleIndex == 1 && firstVisibleOffset > 120){ pageIndex = 2 }
    if(firstVisibleIndex == 2 && firstVisibleOffset > 120){ pageIndex = 3 }
    val totalPages = when(wallets.size ){
        2->2
        3->3
        4->4
        5->4
        else -> 0
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        WalletsTitle(dimensions)
        // Гаманці
            LazyRow(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(dimensions.horizontalPadding),
                contentPadding = PaddingValues(horizontal = ScreenSize.width * 0.05f),
                state = listState
            ) {
                items(wallets) { wallet ->
                    WalletCard(wallet = wallet, dimensions = dimensions) {
                        if (wallet.id != "mono") {
                            onWalletClick(wallet.id)
                        }
                    }
                }
                item {
                    AddWalletCard(onClick = {
                        if (wallets.size > 5) {
                            onErrorChange("Можна створити не більше трьох гаманців")
                        } else {
                            onWalletAddClick()
                        }
                    }, dimensions = dimensions)
                }
            }
            // Індикатор
            if (wallets.size > 1) {
                PagerIndicator(
                    pagerSize = dimensions.pagerSize,
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

// Заголовок гаманців
@Composable
fun WalletsTitle(dimensions: Dimensions,modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = dimensions.verticalPadding)
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.055f))
        Icon(
            painter = painterResource(R.drawable.white_wallet),
            contentDescription = null,
            modifier = Modifier.size(dimensions.iconSize),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(0.033f))
        Text(
            text = "Гаманці",
            modifier = Modifier.weight(0.912f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary)
    }
}

// Картка гаманця
@Composable
fun WalletCard(dimensions: Dimensions, wallet: Wallet, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .width(dimensions.cardWeight)
            .height(dimensions.cardHeight),
        shape = RoundedCornerShape(dimensions.cornerRadius),
        elevation = CardDefaults.cardElevation(5.dp, ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    ) {
        if(dimensions.screenSize == "Compact" || dimensions.screenSize == "Medium") {
            Column(
                modifier = Modifier
                    .padding(dimensions.verticalPadding)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter =
                        if (wallet.source == WalletSource.Api) {
                            painterResource(R.drawable.mono)
                        } else {
                            if (wallet.type == WalletType.Cash)
                                painterResource(R.drawable.cash)
                            else
                                painterResource(R.drawable.card)
                        },
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimensions.iconSize)
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
                Text(
                    text = "UAH",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            }
        }else{
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = dimensions.verticalPadding,
                        vertical = dimensions.verticalPadding * 0.5f
                    )
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.verticalPadding)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter =
                            if (wallet.source == WalletSource.Api) {
                                painterResource(R.drawable.mono)
                            } else {
                                if (wallet.type == WalletType.Cash)
                                    painterResource(R.drawable.cash)
                                else painterResource(R.drawable.card)
                            },
                        contentDescription = null,
                        modifier = Modifier
                            .size(dimensions.iconSize)
                            .padding(bottom = 2.dp)
                    )
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(Modifier
                    .fillMaxHeight()
                    .width(1.5.dp)
                    .background(MaterialTheme.colorScheme.secondary))
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                ) {
                    Text(text = "${wallet.balance}", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "UAH",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        }

    }
}

// Картка "Додати гаманець"
@Composable
fun AddWalletCard(dimensions: Dimensions, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(dimensions.cardWeight)
            .height(dimensions.cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensions.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            when (dimensions.screenSize) {
                "Compact", "Medium" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WalletAddCardContent(dimensions)
                    }
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WalletAddCardContent(dimensions, Modifier.padding(start = 15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WalletAddCardContent(dimensions: Dimensions, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.plus),
        contentDescription = null,
        modifier = Modifier.size(dimensions.iconSize)
    )
    Spacer(modifier = modifier.height(dimensions.verticalPadding))
    Text(
        text = "Додати\n гаманець",
        maxLines = 2,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
}


@Composable
fun TransactionBox(
    dimensions: Dimensions,
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
            .padding(bottom = dimensions.verticalPadding * 2, top = dimensions.horizontalPadding)
            .shadowsPlus(
                type = ShadowsPlusType.SoftLayer,
                color = Color.Black.copy(alpha = 0.25f),
                radius = 4.dp,
                offset = DpOffset(x = 2.dp, y = 2.dp),
                spread = 1.dp,
                shape = RoundedCornerShape(dimensions.cornerRadius),
                isAlphaContentClip = true
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = dimensions.horizontalPadding)
            .padding(top = 0.dp, bottom = dimensions.verticalPadding * 2)
    ){
        var showDatePicker by remember { mutableStateOf(false) }
        if(showDatePicker) {
            AppDatePickerDialog(date, onDateChange) {showDatePicker = false}
        }
        TransactionTitle(dimensions,date = date) {showDatePicker = !showDatePicker}
        Line(height = dimensions.lineWidth)
        when(dimensions.screenSize){
            "Compact" , "Medium" -> TransactionsLazyColumn(Modifier, dimensions, transactions, onTransactionClick, isTransactionLoading, wallets)
            else -> TransactionLazyGrid(Modifier, dimensions, transactions, onTransactionClick, isTransactionLoading, wallets)
        }
    }
}

@Composable
fun TransactionsLazyColumn(modifier: Modifier = Modifier, dimensions: Dimensions,
                           transactions: List<Transaction>, onTransactionClick: (String) -> Unit,
                           isTransactionLoading: Boolean, wallets: List<Wallet>) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(max = 7000.dp, min = ScreenSize.height * 0.11f),
        userScrollEnabled = false
    ) {
        if(isTransactionLoading){
            item {
                TransactionIndicator(dimensions =dimensions)
            }
        }else {
            if (transactions.isEmpty()) {
                item {
                    TransactionEmptyBox()
                }
            } else {
                items(transactions) { transaction ->
                    val transactionId = transaction.id
                    TransactionRow(
                        dimensions,
                        transaction,
                        wallets = wallets
                    ) {
                        onTransactionClick(transactionId)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionLazyGrid(modifier: Modifier = Modifier, dimensions: Dimensions,
                        transactions: List<Transaction>, onTransactionClick: (String) -> Unit,
                        isTransactionLoading: Boolean, wallets: List<Wallet>) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(max = 7000.dp, min = ScreenSize.height * 0.11f),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(dimensions.verticalPadding),
        columns = GridCells.Fixed(2)
    ){
        if(isTransactionLoading){
            item (span = { GridItemSpan(maxLineSpan) }) {
                TransactionIndicator(dimensions = dimensions)
            }
        }else if(transactions.isEmpty()) {
            item(span = {GridItemSpan(maxLineSpan)}){
                TransactionEmptyBox()
            }
        }else {
            items(transactions) { transaction ->
                val transactionId = transaction.id
                TransactionRow(
                    dimensions,
                    transaction,
                    wallets = wallets
                ) {
                    onTransactionClick(transactionId)
                }
            }
        }
    }

}

@Composable
fun TransactionTitle(dimensions: Dimensions,modifier: Modifier = Modifier, date: Long, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = dimensions.verticalPadding * 0.8f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text= "Список транзакцій",
            style = MaterialTheme.typography.headlineSmall,
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .clickable { onClick() },
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
                        .size(dimensions.iconSize)
                        .padding(start = dimensions.horizontalPadding * 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TransactionIndicator(modifier: Modifier = Modifier, dimensions: Dimensions) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(ScreenSize.height * 0.11f),
        contentAlignment = Alignment.TopCenter) {
        InfiniteLinearWavyIndicator(Modifier.height(dimensions.progressBarHeight))
    }
}

@Composable
fun TransactionEmptyBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
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
        Line(modifier = Modifier.align(Alignment.BottomCenter))
    }
}


@Composable
fun TransactionRow(
    dimensions: Dimensions,
    transaction:Transaction,
    wallets: List<Wallet>,
    onClick: () -> Unit
){
    val selectedCategory = findCategoryById(transaction.categoryId)
    val selectedWallet = wallets.first { it.id == transaction.walletId }
    val selectedWalletTo = wallets.firstOrNull{it.id == transaction.walletIdTo}
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = dimensions.verticalPadding * 0.8f)
                .clickable {
                    onClick()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(selectedCategory.iconRes),
                contentDescription = null,
                modifier = Modifier.height(dimensions.iconSize * 1.1f)
            )
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .padding(dimensions.horizontalPadding * 0.3f)
            ) {
                Text(
                    text = selectedCategory.name +
                            if (transaction.type == TransactionType.Transfer) {
                                ", " + transaction.description
                            } else {
                                ""
                            },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = selectedWallet.name +
                            if (transaction.description.isNotEmpty() && transaction.type != TransactionType.Transfer) {
                                ", " + transaction.description
                            } else if (transaction.type == TransactionType.Transfer && selectedWalletTo != null) {
                                "->${selectedWalletTo.name}"
                            } else {
                                ""
                            },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.5f
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(modifier = Modifier.weight(0.45f)) {
                val sign = if (transaction.type == TransactionType.Income) {
                    "+"
                } else if (transaction.description == "Зовнішній(витрата)") {
                    "-"
                } else ""

                Text(
                    text = sign + transaction.amount.toString() + "₴",
                    style = MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.onTertiary),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }

        }
        Line(height = dimensions.lineWidth)
    }
}

@Composable
fun GoalBox(dimensions: Dimensions, goalName: String?, goalTransactionsAmount: Double?, goalAmount: Int?){
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
                .padding(
                    horizontal = if (dimensions.screenSize == "Expanded") {
                        ScreenSize.width * 0.1f
                    } else ScreenSize.width * 0.055f,
                    vertical = 7.dp
                )
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
                .clip(RoundedCornerShape(dimensions.cornerRadius))
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
                    .padding(vertical = dimensions.verticalPadding * 0.33f)) {
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
                        .height(dimensions.progressBarHeight),
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
                onContinued()
                        },
            dismissText = "Новий план",
            onDismiss = {
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
                onDelete()
            },
            dismissText = "Новий план",
            onDismiss = {
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
                onDelete()
            },
            dismissText = "Новий план",
            onDismiss = {
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

@SuppressLint("SuspiciousIndentation")
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