package com.example.moneypath.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
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
import com.example.moneypath.ui.theme.MoneyPathTheme

@Composable
fun AppNavigationRail(navController: NavController){
    // Визначення поточного маршруту, щоб підствічувати активну кнопку
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // stateless функція
    StatelessNavigationRail (
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
fun StatelessNavigationRail(currentRoute: String?, onClick: (String)-> Unit){
    NavigationRail(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth(),
        containerColor = Color.White
    ){
        // Кнопка навігації
        BottomNavItem.items.forEach { item->
            NavigationRailItem(
                icon = {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                selected = currentRoute == item.route,
                onClick = {onClick(item.route)
                },
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
                ),
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp)
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait"
)
@Composable
fun BottomBarPreview() {
    MoneyPathTheme {
        Scaffold() { innerPadding->
            Row(Modifier.fillMaxSize()){
                StatelessNavigationRail("mainscreen") { }
                Box(modifier = Modifier.fillMaxHeight().width(0.5.dp).background(MaterialTheme.colorScheme.surface))
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)){
                    Text("Hello", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
