package com.example.moneypath.ui.elements

import com.example.moneypath.R

sealed class BottomNavItem(
    val title: String,
    val icon: Int,
    val route: String
    ) {
        data object Home : BottomNavItem("Головна", R.drawable.home, "mainscreen")
        data object Categories : BottomNavItem("Категорії", R.drawable.category, "profile")
        data object Plan: BottomNavItem("План", R.drawable.plan, "plan")
        data object Other: BottomNavItem("Інше", R.drawable.other, "other")

    companion object {
        val items by lazy {
            listOf(
                Home,
                Categories,
                Plan,
                Other
            )
        }
    }
}
