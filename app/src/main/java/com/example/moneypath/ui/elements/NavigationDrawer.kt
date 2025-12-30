package com.example.moneypath.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneypath.ui.theme.MoneyPathTheme

@Composable
fun AppNavigationDrawer(navController: NavController, modifier: Modifier = Modifier){
    // Визначення поточного маршруту, щоб підствічувати активну кнопку
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // stateless функція
    StatelessNavigationDrawer (
        modifier,
        currentRoute
    ) { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

}

@Composable
fun StatelessNavigationDrawer(modifier: Modifier = Modifier, currentRoute: String?, onClick: (String)-> Unit){
    PermanentNavigationDrawer(
        modifier = modifier
            .wrapContentHeight()
            .wrapContentWidth(),
        drawerContent = {
            PermanentDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.primary) {
                Column(Modifier.padding(horizontal = 10.dp)) {
                    BottomNavItem.items.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.title, style = MaterialTheme.typography.displayLarge)},
                            selected = currentRoute == item.route,
                            onClick = { onClick(item.route) },
                            icon = {
                                Icon(
                                    painterResource(item.icon),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(38.dp)
                                )
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
    ){
    }
}


@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun NavigationDrawerPreview() {
    MoneyPathTheme {
        Scaffold() { innerPadding->
            Row(Modifier.fillMaxSize()){
                StatelessNavigationDrawer (Modifier.weight(0.21f),"mainscreen") { }
                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(MaterialTheme.colorScheme.surface))
                Box(modifier = Modifier.weight(0.79f).padding(innerPadding)){
                    Text("Hello", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}