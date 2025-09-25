package com.example.moneypath.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.R
import com.example.moneypath.SignInActivity
import com.example.moneypath.ui.elements.AppAlertDialog
import com.example.moneypath.ui.elements.BottomNavigationBar
import com.example.moneypath.ui.elements.Line
import com.example.moneypath.ui.elements.MyTopAppBarNoIcon
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
            bottomBar = { BottomNavigationBar(navController) },
            topBar = { MyTopAppBarNoIcon("Інше", MaterialTheme.colorScheme.background) },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        contentColor = Color.White,
                        snackbarData = data
                    )
                })
            }
        )
        { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = ScreenSize.width * 0.055f)
                        .padding(top = 20.dp, bottom = 30.dp)
                        .shadowsPlus(
                            type = ShadowsPlusType.SoftLayer,
                            color = Color.Black.copy(alpha = 0.25f),
                            radius = 4.dp,
                            offset = DpOffset(x = 2.dp, y = 2.dp),
                            spread = 1.dp,
                            shape = RoundedCornerShape(15.dp),
                            isAlphaContentClip = true
                        )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(15.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(ScreenSize.width * 0.035f)
                ) {
                    Line(MaterialTheme.colorScheme.background)
                    // Заголовок
                    Row(
                        modifier = Modifier.fillMaxWidth().height(ScreenSize.height * 0.07f).background(MaterialTheme.colorScheme.background.copy(alpha = 0.05f)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.mono),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(0.6f)
                        )
                        Text(
                            text = "Підключення до Monobank",
                            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    Line(MaterialTheme.colorScheme.background)
                    // Інструкція
                    ClickableRow(text = "Як отримати токен") { showInstructionDialog = true }
                    // Введення токену
                    Line()
                    ClickableRow(
                        text = "Токен Monobank Api",
                        enabled = !state.hasToken,
                        content = {
                            if (!state.hasToken) {
                                Row() {
                                    Text(
                                        text = "Введіть токен",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            MaterialTheme.colorScheme.inverseSurface
                                        )
                                    )
                                    Image(
                                        painter = rememberAsyncImagePainter(R.drawable.arrow_rigth),
                                        contentDescription = null,
                                        modifier = Modifier.height(15.dp).padding(start = 5.dp)
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
                        showInputDialog = true
                    }
                    Line()
                    // Видалення
                    ClickableRow(text = "Видалити токен", enabled = state.hasToken) { showDeleteDialog = true }

                    Line()
                    Spacer(Modifier.height(20.dp))
                    Line(MaterialTheme.colorScheme.background)

                    // Заголовок
                    Row(
                        modifier = Modifier.fillMaxWidth().height(ScreenSize.height * 0.07f).background(MaterialTheme.colorScheme.background.copy(alpha = 0.05f)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.user),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(0.4f)
                        )
                        Text(
                            text = "Акаунт",
                            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    Line(MaterialTheme.colorScheme.background)

                    ClickableRow(
                        text = "Вийти з акаунту",
                    ) {showLogOutDialog = true}
                    Line()

                    ClickableRow(
                        text = "Видалити акаунт",
                    ) {showDeleteAccountDialog = true}
                    Line()


                    // Вспливаючі вікна
                    if (showInstructionDialog) {
                        AlertDialog(
                            onDismissRequest = { showInstructionDialog = false },
                            content = {
                                Card(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                                ){
                                    Column(Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Інструкція",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                MaterialTheme.colorScheme.onPrimary
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                        )
                                        Text(
                                            text = "1. Зайдіть на сторінку api.monobank.ua.\n" +
                                            "2. Авторизуйтесь через додаток mono.\n" +
                                            "3. Згенеруйте персональний токен.\n" +
                                            "4. Скопіюйте і вставте у додаток.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                            ),
                                            lineHeight = 22.sp,
                                            modifier = Modifier.padding(bottom = 1.dp, start = 5.dp, end = 5.dp
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        TextButton(onClick = { showInstructionDialog = false }) {
                                            Text("Зрозуміло", color = MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center)
                                        }
                                    }
                                }

                            }
                        )
                    }
                    if (showInputDialog) {
                        var input by remember { mutableStateOf("") }
                        AlertDialog(
                            onDismissRequest = { showInputDialog = false },
                            content = {
                                Card(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Column(Modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Введіть токен",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                MaterialTheme.colorScheme.onPrimary
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(bottom = 16.dp)
                                        )
                                        TextField(
                                            value = input,
                                            onValueChange = { input = it },
                                            placeholder = { Text("Токен") },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f),
                                                unfocusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f)
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            TextButton(onClick = { showInputDialog = false }) {
                                                Text(
                                                    "Скасувати",
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            TextButton(onClick = {
                                                viewModel.onTokenChange(input)
                                                viewModel.saveToken()
                                                showInputDialog = false
                                            }) {
                                                Text("Зберегти", color = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
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
}


@Composable
fun ClickableRow(text: String, enabled: Boolean = true, content : @Composable RowScope.() ->Unit = {Image(
    painter = rememberAsyncImagePainter(R.drawable.arrow_rigth),
    contentDescription = null,
    modifier = Modifier.height(15.dp).padding(start = 5.dp)
)}, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .height(ScreenSize.height * 0.085f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        content()
    }
}
