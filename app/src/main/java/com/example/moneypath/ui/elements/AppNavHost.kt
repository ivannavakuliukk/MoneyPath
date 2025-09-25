package com.example.moneypath.ui.elements


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moneypath.ui.screens.AddTransactionScreen
import com.example.moneypath.ui.screens.AddWalletScreen
import com.example.moneypath.ui.screens.CategoriesScreen
import com.example.moneypath.ui.screens.EditWalletScreen
import com.example.moneypath.ui.screens.MainScreen
import com.example.moneypath.ui.screens.OtherScreen
import com.example.moneypath.ui.screens.PlanScreen
import com.example.moneypath.ui.screens.ReportsScreen
import com.example.moneypath.ui.screens.TransactionInfoScreen
import com.example.moneypath.utils.slideInLeft
import com.example.moneypath.utils.slideOutRight
import java.util.Calendar

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier,
    ){
        // Screens нижньої панелі з bottom bar
        composable(BottomNavItem.Home.route) { MainScreen(navController) }
        composable(BottomNavItem.Categories.route) { CategoriesScreen(navController) }
        composable(BottomNavItem.Plan.route) { PlanScreen(navController) }
        composable(BottomNavItem.Reports.route) { ReportsScreen(navController) }
        composable(BottomNavItem.Other.route) { OtherScreen(navController) }

        // Інші Screens
        composable("addwallet",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight

        ) { AddWalletScreen(navController) }
        composable("addtransaction/{date}",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight,
            arguments = listOf(navArgument("date"){type = NavType.LongType})
        ) { backStackEntry->
            val date = backStackEntry.arguments?.getLong("date")?:(Calendar.getInstance().timeInMillis/1000)
            AddTransactionScreen(navController, date)
        }
        composable(
            route ="editwallet/{walletId}",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight,
            arguments = listOf(navArgument("walletId"){type = NavType.StringType})
        ) { backStackEntry ->
            val walletId = backStackEntry.arguments?.getString("walletId")?: ""
            EditWalletScreen(navController, walletId)
        }
        composable(
            route = "transactioninfo/{transactionId}",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight,
            arguments = listOf(navArgument("transactionId"){type = NavType.StringType})
        ){
            backStackEntry->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?:""
            TransactionInfoScreen(navController, transactionId)
        }
    }
}