package com.example.moneypath.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.moneypath.ui.elements.BottomNavigationBar

@Composable
fun CategoriesScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { innerPadding->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            Text("Categories Screen")
        }
    }
}