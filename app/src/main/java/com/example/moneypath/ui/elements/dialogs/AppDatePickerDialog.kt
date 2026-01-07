package com.example.moneypath.ui.elements.dialogs

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.example.moneypath.ui.theme.MoneyPathTheme
import java.util.Calendar

// modal date picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(chosenDate: Long, onDateChange: (Long) -> Unit, onDismiss: ()-> (Unit)){
    val todayDate = Calendar.getInstance()
    // Визначаєм можливі дати - не більше за сьогодні
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= todayDate.timeInMillis
        }
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDates,
        initialSelectedDateMillis = chosenDate * 1000
    )
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onDateChange(
                    datePickerState.selectedDateMillis?.div(1000)
                        ?: (todayDate.timeInMillis / 1000)
                )
                onDismiss()
            }) {
                Text("OK", color = MaterialTheme.colorScheme.secondary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Скасувати", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.Companion.scale(0.9f)
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primary,
                headlineContentColor = MaterialTheme.colorScheme.secondary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
                dayContentColor = MaterialTheme.colorScheme.secondary,
                disabledDayContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                selectedDayContentColor = MaterialTheme.colorScheme.background,
                selectedDayContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                todayContentColor = MaterialTheme.colorScheme.onPrimary,
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerDialogPreview() {
    MoneyPathTheme {
        AppDatePickerDialog(
            chosenDate = 1767689773,
            onDateChange = {}
        ) { }
    }
}