package com.example.moneypath.ui.elements


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import com.example.moneypath.ui.screens.FormScreen
import com.example.moneypath.ui.screens.MainScreen
import com.example.moneypath.ui.screens.OtherScreen
import com.example.moneypath.ui.screens.PlanScreen
import com.example.moneypath.ui.screens.SecuritySetupScreen
import com.example.moneypath.ui.screens.TransactionInfoScreen
import com.example.moneypath.utils.slideInLeft
import com.example.moneypath.utils.slideOutRight
import java.util.Calendar

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "security",
        modifier = modifier
    ){
        // Screens нижньої панелі з bottom bar
        composable(BottomNavItem.Home.route) { MainScreen(navController) }
        composable(BottomNavItem.Categories.route) { CategoriesScreen(navController) }
        composable(BottomNavItem.Plan.route) { PlanScreen(navController) }
        composable(BottomNavItem.Other.route) { OtherScreen(navController) }

        // Інші Screens
        composable("addwallet",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight

        ) { AddWalletScreen(navController) }
        composable(
            "addtransaction/{date}/{isGoal}",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight,
            arguments = listOf(
                navArgument("date") { type = NavType.LongType },
                navArgument("isGoal") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getLong("date") ?: (Calendar.getInstance().timeInMillis / 1000)
            val isGoal = backStackEntry.arguments?.getBoolean("isGoal") ?: false
            AddTransactionScreen(navController, date, isGoal)
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
        composable(
            route = "security"
        ){SecuritySetupScreen(navController)}
        composable(
            route = "form",
            enterTransition = slideInLeft,
            exitTransition = slideOutRight,
        ) { FormScreen(navController) }
    }
}