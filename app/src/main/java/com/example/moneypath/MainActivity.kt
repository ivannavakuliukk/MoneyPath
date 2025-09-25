package com.example.moneypath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.moneypath.data.repository.MonobankRepository
import com.example.moneypath.ui.elements.AppNavHost
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.usecase.MonobankSyncManager
import com.example.moneypath.utils.ScreenSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var syncManager: MonobankSyncManager
    @Inject lateinit var repository: MonobankRepository

    override fun onStart() {
        super.onStart()
        if(repository.hasToken()) {
            lifecycleScope.launch {
                syncManager.syncSinceLastVisit()
            }
            syncManager.startPolling()
        }
    }

    override fun onStop() {
        super.onStop()
        syncManager.stopPolling()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val config = LocalConfiguration.current
            LaunchedEffect(config) {
                ScreenSize.width = config.screenWidthDp.dp
                ScreenSize.height = config.screenHeightDp.dp
            }
            MoneyPathTheme {
                val navController = rememberNavController()
                Scaffold(Modifier.background(MaterialTheme.colorScheme.primary )) { innerPadding ->
                    AppNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

