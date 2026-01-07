package com.example.moneypath.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.R
import com.example.moneypath.SignInActivity
import com.example.moneypath.ui.elements.AppSnackBar
import com.example.moneypath.ui.elements.ContainerForDataBox
import com.example.moneypath.ui.elements.dialogs.AppAlertDialog
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarNoIcon
import com.example.moneypath.ui.elements.dialogs.AppConfirmDialog
import com.example.moneypath.ui.elements.dialogs.AppInputDialog
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.ui.viewmodel.OtherViewModel
import com.example.moneypath.utils.ScreenSize
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.common.shadowsPlus
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(navController: NavController, viewModel: OtherViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showInstructionDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember{ mutableStateOf(false) }

    LaunchedEffect(state.shouldNavigateToSignIn) {
        if (state.shouldNavigateToSignIn) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    SoftLayerShadowContainer {
        Scaffold(
            topBar = { MyTopAppBarNoIcon("Інше", MaterialTheme.colorScheme.background) },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, snackbar = { data ->
                    AppSnackBar(data)
                })
            }
        )
        { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.primary)
                    .verticalScroll(scrollState)
            ) {
                OtherScreenContent(
                    state.hasToken,
                    {showInstructionDialog = true},
                    {showInputDialog = true},
                    {showDeleteDialog = true},
                    {showDeleteAccountDialog = true}
                ) {showLogOutDialog = true }
                // Вспливаючі вікна
                if (showInstructionDialog) {
                    AppConfirmDialog(
                        text = """
                            1. Зайдіть на сторінку api.monobank.ua. 
                            2. Авторизуйтесь через додаток mono.
                            3. Згенеруйте персональний токен.
                            4. Скопіюйте і вставте у додаток.
                        """.trimIndent(),
                        title = "Інструкція"
                    ) { showInstructionDialog = false }
                }
                if (showInputDialog) {
                    AppInputDialog(
                        onClick = { it ->
                            viewModel.onTokenChange(it)
                            viewModel.saveToken()
                            showInputDialog = false
                        },
                        onDismiss = { showInputDialog = false },
                        text = "Введіть токен"
                    )
                }
                if (showDeleteDialog) {
                    AppAlertDialog(
                        text = "Ви впевнені що хочете відв'язати Monobank Api? Всі транзакції автоматично видаляться з історії",
                        onConfirmClick = {
                            viewModel.clearToken()
                            showDeleteDialog = false
                        },
                        onCancelClick = { showDeleteDialog = false }
                    )
                }
                if(showLogOutDialog){
                    AppAlertDialog(
                        text = "Ви впевнені що хочете вийти з акаунту?",
                        onCancelClick = {showLogOutDialog = false},
                        onConfirmClick = {viewModel.logOut()}
                    )
                }
                if(showDeleteAccountDialog){
                    AppAlertDialog(
                        text = "Ви впевнені, що хочете видалити акаунт. Це незворотня дія. Всі дані будуть видалені!",
                        onConfirmClick = {viewModel.deleteAccount()},
                        onCancelClick = {showDeleteAccountDialog = false}
                    )
                }
            }
        }
    }
}

@Composable
fun OtherScreenContent(
    hasToken: Boolean,
    onInstructionClick: ()-> Unit,
    onTokenClick:()-> Unit,
    onDeleteTokenClick: ()-> Unit,
    onDeleteAccountClick: ()-> Unit,
    onLogOutClick: ()-> Unit
) {
    ContainerForDataBox(showTitle = false) {

        //Заголовок
        GreenTitleWithIcon("Підключення до Monobank", R.drawable.mono)

        // Інструкція
        ClickableRow(text = "Як отримати токен") { onInstructionClick() }

        // Введення токену
        ClickableRow(
            text = "Токен Monobank Api",
            enabled = !hasToken,
            content = {
                if (!hasToken) {
                    Row() {
                        Text(
                            text = "Введіть токен",
                            style = MaterialTheme.typography.bodySmall.copy(
                                MaterialTheme.colorScheme.inverseSurface
                            )
                        )
                        Image(
                            painter = painterResource(R.drawable.arrow_rigth),
                            contentDescription = null,
                            modifier = Modifier
                                .height(15.dp)
                                .padding(start = 5.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Токен введено ✔",
                        style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.inverseSurface)
                    )
                }
            }
        ) {
            onTokenClick()
        }

        // Видалення
        ClickableRow(text = "Видалити токен", enabled = hasToken) { onDeleteTokenClick() }
        Spacer(Modifier.height(20.dp))

        // Заголовок
        GreenTitleWithIcon("Акаунт", R.drawable.user)

        // Вийти з акаунту
        ClickableRow(
            text = "Вийти з акаунту",
        ) {onLogOutClick() }

        // Видалити акаунт
        ClickableRow(
            text = "Видалити акаунт",
        ) { onDeleteAccountClick() }

    }
}


@Composable
fun ClickableRow(text: String, enabled: Boolean = true, content : @Composable RowScope.() -> Unit =
    { Image(
        painter = painterResource(R.drawable.arrow_rigth),
        contentDescription = null,
        modifier = Modifier
            .height(15.dp)
            .padding(start = 5.dp))
    }, onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .clickable(enabled = enabled, onClick = onClick)
                .height((ScreenSize.height * 0.085f).coerceAtLeast(55.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text)
            content()
        }
        Line()
    }
}

@Composable
fun GreenTitleWithIcon(text: String, icon: Int) {
    Line(MaterialTheme.colorScheme.background)
    // Заголовок
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((ScreenSize.height * 0.07f).coerceAtLeast(55.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(0.6f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
            modifier = Modifier.padding(start = 5.dp)
        )
    }
    Line(MaterialTheme.colorScheme.background)
}

@Preview(showBackground = true, widthDp = 360, backgroundColor = 0xFF55D6BE)
@Composable
private fun ClickableRowPreview() {
    MoneyPathTheme {
        Box(Modifier
            .wrapContentSize()
            .padding(10.dp)) {
            ClickableRow("Видалити акаунт") { }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, backgroundColor = 0xFFFFFFFF)
@Composable
private fun GreenTitlePreview() {
    MoneyPathTheme {
        Box(Modifier
            .wrapContentSize()
            .padding(10.dp)) {
            GreenTitleWithIcon(
                "Підключення до Monobank",
                R.drawable.mono
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OtherScreenContentPreview() {
    MoneyPathTheme {
        OtherScreenContent(
            hasToken = true,
            onInstructionClick = {},
            onTokenClick = {},
            onDeleteTokenClick = {},
            onDeleteAccountClick = {}
        ) { }
    }
}

