package com.example.moneypath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.ui.elements.AppAlertDialog
import com.example.moneypath.ui.elements.AppButton
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBar
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.ui.viewmodel.EditWalletViewModel
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

/*
    Сторінка "Редагувати гаманець"
    Параметри - NavController, walletId
    DI - EditWalletViewModel
 */

@Composable
fun EditWalletScreen(navController: NavController , walletId: String, viewModel: EditWalletViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    var showDialog by remember {mutableStateOf(false)}

    LaunchedEffect(walletId) {
        viewModel.loadWalletData(walletId)
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

    SoftLayerShadowContainer {
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            topBar = {
            MyTopAppBar(
                MaterialTheme.colorScheme.background,
                "Редагувати гаманець"
        ) { navController.popBackStack() }
        },
            bottomBar = {
                Line()
                Column(Modifier.background(MaterialTheme.colorScheme.primary)) {
                    if (state.isLoading) {
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.background,
                            trackColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    ) {
                        AppButton(
                            {
                                showDialog = true
                            },
                            "Видалити",
                            modifier = Modifier
                                .padding(
                                    bottom = 15.dp,
                                    start = ScreenSize.width * 0.055f,
                                    end = ScreenSize.width * 0.022f,
                                    top = 10.dp
                                )
                                .weight(0.5f),
                            MaterialTheme.colorScheme.surface
                        )
                        AppButton(
                            {
                                viewModel.addOrUpdateWallet(walletId)
                                navController.popBackStack()
                            },
                            "Зберегти",
                            modifier = Modifier
                                .padding(
                                    bottom = 15.dp,
                                    start = ScreenSize.width * 0.022f,
                                    end = ScreenSize.width * 0.055f,
                                    top = 10.dp
                                )
                                .weight(0.5f),
                            MaterialTheme.colorScheme.tertiary
                        )
                    }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                item {
                    Spacer(Modifier.height(30.dp))
                    DataBox(
                        state = viewModel.uiState,
                        onNameChange = {viewModel.onNameChange(it)},
                        onTypeChange = {viewModel.onTypeChange(it)},
                        onBalanceChange = {viewModel.onBalanceChange(it)}
                    )
                    Spacer(Modifier.height(15.dp))
                }

            }
        }
    }

    if (showDialog) {
        AppAlertDialog(
            text = "Ви впевнені, що хочете видалити гаманець '${viewModel.uiState.name}'?" +
                    "\nВсі транзакції цього гаманця видаляться автоматично.",
            onCancelClick =  {showDialog = false},
            onConfirmClick = { viewModel.deleteWallet(walletId)
            navController.popBackStack() }
        )
    }
}
