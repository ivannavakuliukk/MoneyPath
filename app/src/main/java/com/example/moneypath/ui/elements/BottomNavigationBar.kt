package com.example.moneypath.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter



// Нижня панель навігації
// Передаємо NavController, щоб кнопки могли перемикати екран
@Composable
fun BottomNavigationBar(navController:NavController) {

    //Список елементів панелі
    val items = listOf(
        BottomNavItem.Categories,
        BottomNavItem.Plan,
        BottomNavItem.Home,
        BottomNavItem.Reports,
        BottomNavItem.Other
    )

    NavigationBar(
        modifier = Modifier.fillMaxHeight(0.09f).fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primary
    ){

        // Визначення поточного маршруту, щоб підствічувати активну кнопку
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Кнопка навігації
        items.forEach { item->
            NavigationBarItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            rememberAsyncImagePainter(item.icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(0.4f),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // не створювати новий екземпляр екрана, якщо він вже у стеку зверху
                            launchSingleTop = true
                            // відновлює стан попереднього екрану, якщо користувач повертається назад
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
                )
            )
        }
    }
}