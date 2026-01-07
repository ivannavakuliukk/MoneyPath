package com.example.moneypath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.domain.repository.MonobankRepository
import com.example.moneypath.ui.elements.navigation.AppNavHost
import com.example.moneypath.ui.elements.navigation.AppNavigationDrawer
import com.example.moneypath.ui.elements.navigation.AppNavigationRail
import com.example.moneypath.ui.elements.navigation.BottomNavigationBar
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.domain.usecase.business.MonobankSyncManager
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

                    val sizeClass = LocalAppWindowInfo.current.windowSizeClass
                    when  {
                        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                            Scaffold(
                                Modifier.fillMaxSize()
                            ) { innerPadding ->
                                Row(Modifier.fillMaxSize()) {
                                    if (currentRoute in navDestinations) {
                                        AppNavigationDrawer(
                                            modifier = Modifier.weight(0.25f),
                                            navController = navController
                                        )
                                        Box(
                                            modifier = Modifier.fillMaxHeight().width(1.5.dp)
                                                .background(MaterialTheme.colorScheme.background)
                                        )
                                        AppNavHost(
                                            navController,
                                            modifier = Modifier.weight(0.75f)
                                        )
                                    } else {
                                        AppNavHost(navController)
                                        Spacer(modifier = Modifier.padding(innerPadding))
                                    }
                                }
                            }
                        }sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                            Scaffold(Modifier.fillMaxSize()
                            ) {innerPadding->
                                Row(Modifier.fillMaxSize()) {
                                    Spacer(Modifier.padding(innerPadding))
                                    if(currentRoute in navDestinations) {
                                        AppNavigationRail(navController)
                                        Box(modifier = Modifier.width(1.5.dp).fillMaxHeight())
                                    }
                                    AppNavHost(navController,)
                                }
                            }
                        }else ->{
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
    }
}


// Зберігаємо SizeClass для всього додатку
data class AppWindowInfo(
    val windowSizeClass: WindowSizeClass
)

// CompositionLocal для доступу з будь-якого Composable
val LocalAppWindowInfo = staticCompositionLocalOf<AppWindowInfo> {
    error("No AppWindowInfo provided")
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


