package com.example.moneypath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.R
import com.example.moneypath.ui.elements.BottomNavigationBar
import com.example.moneypath.ui.elements.MyTopAppBarNoIcon
import com.example.moneypath.ui.elements.PagerIndicator
import com.example.moneypath.ui.viewmodel.MainScreenViewModel
import com.example.moneypath.ui.viewmodel.PlanScreenViewModel
import com.example.moneypath.utils.ScreenSize
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

@Composable
fun PlanScreen(navController: NavController, viewModel: PlanScreenViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
//            viewModel.clearError()
        }
    }
    val totalPages = 2
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })
    SoftLayerShadowContainer {
        Scaffold(
            bottomBar = {BottomNavigationBar(navController)},
            topBar = {MyTopAppBarNoIcon(
                title = if (pagerState.currentPage == 0) "Поточний план" else "Альтернативні плани",
                background = MaterialTheme.colorScheme.background
            )},
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
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> CurrentPlan(state, navController)
                        1 -> OtherPlans(state)
                    }
                }
                PagerIndicator(
                    totalPages = totalPages,
                    currentPage = pagerState.currentPage
                )
            }
        }
    }
}

@Composable
fun CurrentPlan(state: PlanScreenViewModel.UiState, navController: NavController){
    LazyColumn(modifier = Modifier
        .padding(horizontal = ScreenSize.width * 0.055f)
        .padding(vertical = 30.dp)
        .border(width = 1.dp, color = MaterialTheme.colorScheme.surface)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(horizontal = ScreenSize.width * 0.035f)
        .padding(top = 0.dp, bottom = ScreenSize.width * 0.05f)
    )
    {
        item {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = {navController.navigate("form")})
            ) {
                Text(
                    text = if(state.isPlanned) "Перерахувати план" else "Згенерувати план",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun OtherPlans(state: PlanScreenViewModel.UiState){
    LazyColumn(modifier = Modifier
        .padding(horizontal = ScreenSize.width * 0.055f)
        .padding(vertical = 10.dp)
        .border(width = 1.dp, color = MaterialTheme.colorScheme.surface)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(horizontal = ScreenSize.width * 0.035f)
        .padding(top = 0.dp, bottom = ScreenSize.width * 0.05f)
    ){
        item{
            if(!state.isPlanned){
                Text(text = "Ви ще не маєте планів, згенеруйте")
            }
        }
    }
}