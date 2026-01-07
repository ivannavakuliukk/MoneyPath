package com.example.moneypath.ui.elements.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneypath.ui.elements.navigation.BottomNavItem
import com.example.moneypath.ui.theme.MoneyPathTheme


// Нижня панель навігації
// Передаємо NavController, щоб кнопки могли перемикати екран
@Composable
fun BottomNavigationBar(navController: NavController){
    // Визначення поточного маршруту, щоб підствічувати активну кнопку
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // stateless функція
    StatelessBottomBar(
        currentRoute
    ) { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                // не створювати новий екземпляр екрана, якщо він вже у стеку зверху
                launchSingleTop = true
                // відновлює стан попереднього екрану, якщо користувач повертається назад
                restoreState = true
            }
        }
    }

}

@Composable
fun StatelessBottomBar(currentRoute: String?, onClick: (String)-> Unit){
    NavigationBar(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        containerColor = Color.White
    ){

        // Кнопка навігації
        BottomNavItem.Companion.items.forEach { item->
            NavigationBarItem(
                icon = {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
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
                onClick = {onClick(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
                )
            )
        }
    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AppBottomBarPreview() {
    MoneyPathTheme {
        Scaffold(bottomBar = {StatelessBottomBar("mainscreen") { }}) {innerPadding->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)){}
        }
    }
}



