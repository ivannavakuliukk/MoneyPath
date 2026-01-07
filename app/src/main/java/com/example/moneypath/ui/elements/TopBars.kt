package com.example.moneypath.ui.elements

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.rememberAsyncImagePainter
import com.example.moneypath.R
import com.example.moneypath.ui.preview.TopBarParamsProvider
import com.example.moneypath.ui.theme.MoneyPathTheme
import com.example.moneypath.utils.Dimensions
import com.example.moneypath.utils.ScreenSize
import com.example.moneypath.utils.formatMonthYear
import com.example.moneypath.utils.getMonthBounds
import java.util.Calendar

// Верхня панель
@Composable
fun MainTopAppBar(userName: String, url: Uri?, dimensions: Dimensions) {
    Row(
        modifier = Modifier.Companion
            .wrapContentHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.055f))
        if (url != null) {
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier.Companion
                    .clip(CircleShape)
                    .aspectRatio(1f)
                    .weight(if (dimensions.screenSize == "Compact") 0.075f else 0.097f)
            )
        } else {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(0.097f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (userName.firstOrNull()?.uppercaseChar() ?: "?").toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.03f))
        Column(modifier = Modifier.weight(0.72f)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
            Text(
                text = "Привіт!\uD83D\uDC4B Раді Вас бачити!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .weight(0.06f)
        ) {
            Image(
                painter = painterResource(R.drawable.bell),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.weight(0.055f))
    }
}

// Top bar з якого відкривається docked date picker
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTopApp(dates: Pair<Long, Long>?, onDateRangeSelected: (Long, Long)-> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height((ScreenSize.height * 0.097f).coerceAtLeast(50.dp))
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var showDatePickerDocked by remember { mutableStateOf(false) }
        val (startDateSec, endDateSec) = dates ?: getMonthBounds()
        val todayDate = Calendar.getInstance()
        // Визначаєм можливі дати - не більше за сьогодні
        val selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayDate.timeInMillis
            }
        }
        val datePickerState = rememberDatePickerState(
            selectableDates = selectableDates,
            initialSelectedDateMillis = startDateSec * 1000
        )
        // відстежуємо натискання користувачем на дату
        LaunchedEffect(datePickerState.selectedDateMillis) {
            if (showDatePickerDocked && datePickerState.selectedDateMillis != null) {
                val selectedRangeInMillis =
                    getMonthBounds(datePickerState.selectedDateMillis ?: 0L)
                onDateRangeSelected(
                    selectedRangeInMillis.first,
                    selectedRangeInMillis.second
                )
                showDatePickerDocked = false
            }
        }
        Text(
            text = formatMonthYear(startDateSec, endDateSec),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Box(
            modifier = Modifier.Companion
                .fillMaxHeight()
                .wrapContentWidth()
                .clickable { showDatePickerDocked = !showDatePickerDocked }
        ) {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .padding(start = 5.dp)
                    .align(Alignment.Center)
            )
        }
        if(showDatePickerDocked){
            Popup(
                onDismissRequest = { showDatePickerDocked = false },
                alignment = Alignment.TopStart
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (ScreenSize.height * 0.07f).coerceAtLeast(30.dp))
                        .wrapContentHeight()
                        .background(Color.Transparent)
                        .scale(0.9f)
                        .shadow(5.dp)
                ) {
                    Column {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            title = null,
                            headline = null,
                            colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
                                dayContentColor = MaterialTheme.colorScheme.secondary,
                                disabledDayContentColor = MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.5f
                                ),
                                selectedDayContentColor = MaterialTheme.colorScheme.background,
                                selectedDayContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(
                                    alpha = 0.2f
                                ),
                                todayContentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                }
            }
        }
    }
}

/*
    Верхня панель
    Параметри - заголовок, колір, функція при натисканні на іконку
 */
@Composable
fun MyTopAppBar(background: Color, title: String, onClick: () -> Unit){
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .height((ScreenSize.height * 0.097f).coerceAtLeast(50.dp))
            .background(background)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.Companion.align(Alignment.Companion.Center)
        )
        Row(
            modifier = Modifier.Companion.align(Alignment.Companion.CenterEnd)
        ) {
            IconButton(
                onClick = { onClick() },
                modifier = Modifier.Companion.width((ScreenSize.width * 0.041f).coerceAtLeast(13.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.cancel_white),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.Companion.width((ScreenSize.width * 0.055f).coerceAtLeast(10.dp)))

        }

    }
}

@Composable
fun MyTopAppBarTwoLines(background: Color, title: String, text: String, onClick: () -> Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height((ScreenSize.height * 0.097f).coerceAtLeast(60.dp))
        .background(background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.Companion.padding(bottom = 3.dp)
            )
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary),
                )
            }
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ){
            IconButton(
                onClick = {onClick()},
                modifier = Modifier.width((ScreenSize.width*0.041f).coerceAtLeast(13.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.cancel_white),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width((ScreenSize.width * 0.055f).coerceAtLeast(10.dp)))

        }

    }
}

@Composable
fun MyTopAppBarNoIcon(title: String, background: Color){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height((ScreenSize.height * 0.097f).coerceAtLeast(50.dp))
        .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MyTopAppBarTwoLinesNoIcon(background: Color, title: String, text: String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height((ScreenSize.height * 0.097f).coerceAtLeast(60.dp))
        .background(background)
    ) {
        Column(modifier = Modifier
            .align(Alignment.Center)
            .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Box(modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary),
                )
            }
        }

    }
}

@Preview(showBackground = true, group = "Main", widthDp = 360)
@Preview(showBackground = true, group = "Main", widthDp = 600, fontScale = 1.67f)
@Composable
private fun MainTopAppBarPreview() {
    MoneyPathTheme {
        MainTopAppBar(
            "Гість",
            null,
            Dimensions()
        )
    }
}

@Preview(showBackground = true, widthDp = 360, group = "OneLine")
@Composable
private fun TopAppBarPreview() {
    MoneyPathTheme {
        MyTopAppBar(
            background = MaterialTheme.colorScheme.surface,
            title = "Додати транзакцію"
        ) { }
    }
}

@Preview(showBackground = true, widthDp = 360, group = "OneLine")
@Composable
private fun TopAppBarNoIconPreview() {
    MoneyPathTheme {
        MyTopAppBarNoIcon(
            background = MaterialTheme.colorScheme.tertiary,
            title = "План витрат"
        )
    }
}

@Preview(showBackground = true, widthDp = 360, group = "TwoLines")
@Composable
private fun TopAppBarTwoLinesPreview() {
    MoneyPathTheme {
        MyTopAppBarTwoLines(
            background = MaterialTheme.colorScheme.background,
            title = "Налаштування плану",
            text = "Вибір типу плану"
        ) { }
    }
}

@Preview(showBackground = true, widthDp = 360, group = "TwoLines")
@Composable
private fun TopAppBarTwoLinesNoIconPreview() {
    MoneyPathTheme {
        MyTopAppBarTwoLinesNoIcon(
            background = MaterialTheme.colorScheme.background,
            title = "План витрат",
            text = "Для досягнення цілі"
        )
    }
}

@Preview(showBackground = true, widthDp = 360, group = "Categories", showSystemUi = false)
@Composable
private fun CategoriesTopBarPreview(
    @PreviewParameter(TopBarParamsProvider::class)
    dates: Pair<Long, Long>?
) {
    MoneyPathTheme {
        CategoriesTopApp(
            dates
        ) { _, _ -> }
    }
}
