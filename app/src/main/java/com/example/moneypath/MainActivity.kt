package com.example.moneypath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.MonobankRepository
import com.example.moneypath.ui.elements.AppNavHost
import com.example.moneypath.ui.elements.BottomNavigationBar
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.usecase.business.MonobankSyncManager
import com.example.moneypath.utils.AppWindowInfo
import com.example.moneypath.utils.LocalAppWindowInfo
import com.example.moneypath.utils.ScreenSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var syncManager: MonobankSyncManager
    @Inject lateinit var repository: MonobankRepository
    @Inject lateinit var cryptoRepository: CryptoRepository

    override fun onStart() {
        super.onStart()
        if (repository.hasToken()) {
            lifecycleScope.launch {
                // чекаємо поки DEK стане доступним
                while (cryptoRepository.getSessionDEK() == null) {
                    delay(1000)
                }

                // DEK готовий — запускаємо синхронізацію
                syncManager.syncSinceLastVisit()
                syncManager.startPolling()
            }
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
            AppAdaptiveProvider {
                MoneyPathTheme {
                    val navDestinations = listOf("mainscreen", "profile", "plan", "other")
                    val navController = rememberNavController()

                    // підписуємось на стан навігації
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    Scaffold(Modifier.background(MaterialTheme.colorScheme.primary),
                        bottomBar = {
                            if (currentRoute in navDestinations) {
                                BottomNavigationBar(navController)
                            }
                        }) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppAdaptiveProvider(content: @Composable () -> Unit) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    CompositionLocalProvider(
        LocalAppWindowInfo provides AppWindowInfo(windowSizeClass = windowSizeClass)
    ) {
        content()
    }
}


