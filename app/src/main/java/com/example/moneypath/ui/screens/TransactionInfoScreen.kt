package com.example.moneypath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.ui.elements.AppAlertDialog
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.AppInputRow
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBar
import com.example.moneypath.ui.viewmodel.TransactionInfoViewModel
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.displaySign
import com.example.moneypath.utils.formattedDate
import com.example.moneypath.utils.toDisplayName
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import kotlinx.coroutines.delay

@Composable
fun TransactionInfoScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionInfoViewModel = hiltViewModel()
){
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }
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
    LaunchedEffect(state.successTransaction){
        if(!state.successTransaction){
            delay(1500)
            navController.popBackStack()
        }
    }
    SoftLayerShadowContainer {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MyTopAppBar(
                    MaterialTheme.colorScheme.background,
                    "Інформація про транзакцію"
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
                            showDialog = true
                        },
                        text = "Видалити",
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(
                            horizontal = ScreenSize.width * 0.055f,
                        ).padding(bottom = 15.dp, top = 10.dp),
                        enabled = state.walletName != "mono"
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
        )
        { innerPadding ->
            if(showDialog){
                AppAlertDialog(
                    text = "Ви впевнені, що хочете видалити транзакцію ?",
                    onCancelClick =  {showDialog = false},
                    onConfirmClick = { viewModel.deleteTransaction()
                        navController.popBackStack() }
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to MaterialTheme.colorScheme.background,
                            0.5f to MaterialTheme.colorScheme.background,
                            0.5f to MaterialTheme.colorScheme.primary,
                            1.0f to MaterialTheme.colorScheme.primary
                        )
                    )
                    .padding(innerPadding)
            ) {
                item {
                    if(state.isLoadingTransaction){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight(0.5f),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(70.dp)

                            )
                        }
                    }else {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .border(0.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(0.dp))
                            .padding(5.dp),
                            contentAlignment = Alignment.Center,
                        ){
                            Text(
                                text = state.transaction.type.toDisplayName(),
                                style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.primary)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(horizontal = ScreenSize.width * 0.055f)
                                .padding(top = 17.dp, bottom = 30.dp)
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
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(ScreenSize.width * 0.035f)
                        ) {
                            // Сума
                            val sign = if(state.transaction.type == TransactionType.Income){
                                "+"
                            }else if(state.transaction.description == "Зовнішній(витрата)"){
                                "-"
                            }else ""

                            AppInputRow(
                                iconRes = R.drawable.amount,
                                text = "Сума"
                            ) {
                                PageText(
                                    sign + state.transaction.amount + "₴",
                                    Modifier.weight(0.56f),
                                    MaterialTheme.typography.headlineSmall.copy(MaterialTheme.colorScheme.onTertiary)
                                )
                            }

                            // Гаманець
                            AppInputRow(
                                iconRes = R.drawable.wallet,
                                text = "Гаманець"
                            ) { PageText(state.walletName, Modifier.weight(0.56f)) }

                            // Гаманець-отримувач(якщо є)
                            if (state.transaction.walletIdTo.isNotEmpty()) {
                                AppInputRow(
                                    iconRes = R.drawable.wallet_receiver,
                                    text = "Гаманець отримувач"
                                ) { PageText(state.walletToName, Modifier.weight(0.56f)) }
                            }

                            // Категорія
                            val selectedCategory = findCategoryById(state.transaction.categoryId)
                            AppInputRow(
                                iconRes = selectedCategory.iconRes,
                                text = "Категорія"
                            ) { PageText(selectedCategory.name, Modifier.weight(0.56f)) }

                            // Дата
                            AppInputRow(
                                iconRes = R.drawable.calendar,
                                text = "Дата"
                            ) {
                                PageText(
                                    formattedDate(state.transaction.date),
                                    Modifier.weight(0.56f)
                                )
                            }

                            // Опис
                            AppInputRow(
                                iconRes = R.drawable.name,
                                text = "Опис"
                            ) {
                                PageText(state.transaction.description.ifEmpty { "---" }, Modifier.weight(0.56f),
                                    MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.inverseSurface))
                            }
                            Line()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun PageText(text: String, modifier: Modifier,  style: TextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onTertiary)){
    Text(
        text = text,
        style = style,
        textAlign = TextAlign.Right,
        modifier = modifier.padding(end = 5.dp),
        maxLines = 3
    )
}