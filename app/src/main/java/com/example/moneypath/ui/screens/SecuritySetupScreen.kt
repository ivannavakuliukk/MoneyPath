package com.example.moneypath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneypath.ui.viewmodel.SecuritySetupViewModel

@Composable
fun SecuritySetupScreen(navController: NavController, viewModel: SecuritySetupViewModel = hiltViewModel()){
    val state = viewModel.uiState
    LaunchedEffect(state.successKey) {
        if(state.successKey){
            navController.navigate("mainscreen")
        }
    }
    when (state.keyStatus) {
        SecuritySetupViewModel.KeyStatus.FIRST_LAUNCH -> {
            // Екран створення PIN з повторенням
            PinInputScreen(
                requireConfirmation = true,
                onPinEntered = { pin ->
                    viewModel.onPasswordChange(pin)
                    viewModel.savePassword()
                    viewModel.initializeKeys()
                },
                state = state
            )
        }

        SecuritySetupViewModel.KeyStatus.MISSING_PIN -> {
            // Екран введення PIN без повторення
            PinInputScreen(
                requireConfirmation = false,
                onPinEntered = { pin ->
                    viewModel.onPasswordChange(pin)
                    viewModel.savePassword()
                    viewModel.retrieveKeys()
                },
                state = state
            )
        }
        SecuritySetupViewModel.KeyStatus.READY -> {
            viewModel.retrieveKeys()
        }

        SecuritySetupViewModel.KeyStatus.MISSING_SALT -> {
            ErrorScreen(
                message = "Не вдалося завантажити ключові дані",
                onRetry = { viewModel.loadKeyMaterial() }
            )
        }

        SecuritySetupViewModel.KeyStatus.UNKNOWN -> {
            LoadingScreen()
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(message)
    }

}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
            modifier = Modifier
                .height(30.dp)

        )
    }

}

@Composable
fun PinInputScreen(
    pinLength: Int = 5,
    requireConfirmation: Boolean,
    onPinEntered: (String) -> Unit,
    onSkipToMain: () -> Unit = {},
    state: SecuritySetupViewModel.UiState
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(state.error) }

    val activeInput = if (!isConfirm) pin else confirmPin

    // Заголовок залежно від стану
    val title = when {
        !requireConfirmation -> "Введіть ваш PIN"
        requireConfirmation && !isConfirm -> "Створіть PIN"
        else -> "Підтвердіть PIN"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        if(state.isLoadingPin){
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                modifier = Modifier
                    .height(20.dp).padding(top = 40.dp, bottom = 20.dp)
            )
        }else {
            // Заголовок
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
            )
        }

        // Крапочки
        Row(horizontalArrangement = Arrangement.Center) {
            repeat(pinLength) { index ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(6.dp)
                        .background(
                            color = if (activeInput.length > index) Color.Black else Color.Gray.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }

        if (!error.isNullOrEmpty()) {
            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Панель цифр
        NumberPad(
            onNumberClick = { number ->
                if (activeInput.length < pinLength) {
                    if (!isConfirm) pin += number else confirmPin += number
                }
            },
            onDeleteClick = {
                if (!isConfirm) {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                } else {
                    if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Логіка переходів
    LaunchedEffect(pin, confirmPin, isConfirm) {
        if (!requireConfirmation && pin.length == pinLength) {
            onPinEntered(pin)
        } else if (requireConfirmation && !isConfirm && pin.length == pinLength) {
            isConfirm = true
        } else if (requireConfirmation && isConfirm && confirmPin.length == pinLength) {
            if (pin == confirmPin) {
                onPinEntered(pin)
            } else {
                error = "Неправильно введений пін"
                pin = ""
                confirmPin = ""
                isConfirm = false
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.forEach { row ->
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, CircleShape)
                            .clickable {
                                when (item) {
                                    "" -> {}
                                    "⌫" -> onDeleteClick()
                                    else -> onNumberClick(item)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
