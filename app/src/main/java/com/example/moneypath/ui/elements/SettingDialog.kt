package com.example.moneypath.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.SettingsDB
import com.example.moneypath.utils.formattedDate

@Composable
fun SettingDialog(onDismiss: ()-> Unit, settings: SettingsDB, isGoal:Boolean, walletNames: List<String>, planStart:Long){
    val textColor = MaterialTheme.colorScheme.primary
    Dialog(onDismiss){
        Scaffold(topBar = {MyTopAppBar( background = MaterialTheme.colorScheme.background, title = "Налаштування плану") { onDismiss()}})
        {paddingValues->
            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    FormContainer(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            BorderedBox {
                                Text(
                                    text = "1.",
                                    style = MaterialTheme.typography.titleSmall.copy(textColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                if (isGoal) {
                                    Column {
                                        DiffStyleLine("Ціль - ", " '${settings.goal_name}' ${settings.goal_amount} грн.")
                                        DiffStyleLine("Термін - ", "${settings.months} місяців.")
                                        DiffStyleLine("Дата початку - ", "${formattedDate(planStart/1000)} .")
                                    }
                                } else
                                    Column {
                                        DiffStyleLine("Планування без цілі", "")
                                        DiffStyleLine(
                                            "Дата початку - ",
                                            "${formattedDate(planStart/1000)} ."
                                        )
                                    }
                            }
                            BorderedBox {
                                Text(
                                    text = "2.",
                                    style = MaterialTheme.typography.titleSmall.copy(textColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Column {
                                    DiffStyleLine("Щомісячний дохід - ", "${settings.stable_income} грн.")
                                    DiffStyleLine("Дохід цього місяця - ", "${settings.current_income} грн.")
                                }
                            }

                            BorderedBox {
                                val boundsPairs: List<Pair<Int, Int>> = settings.bounds.map {
                                    Pair(it[0], it[1])
                                }
                                val sortedBounds = boundsPairs.zip(settings.priorities)
                                    .sortedBy { it.second } // сортуємо межі за пріоритетом
                                    .map { it.first }
                                val sortedCategories = settings.categories.zip(settings.priorities)
                                    .sortedBy { it.second } // сортуємо категорії за пріоритетом
                                    .map { it.first }
                                val categories = sortedCategories.map{ id->
                                    Categories.expensesCategory.find { id == it.id }?.name ?: ""
                                }
                                Text(
                                    text = "3.",
                                    style = MaterialTheme.typography.titleSmall.copy(textColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Column {
                                    DiffStyleLine("Категорії за пріоритетами:", "")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    categories.forEachIndexed { index, category ->
                                        DiffStyleLine(
                                            "${index + 1}) $category - ",
                                            "від ${sortedBounds[index].first} до ${sortedBounds[index].second} грн."
                                        )
                                    }
                                }
                            }

                            BorderedBox {
                                val fixedCategories = settings.fixed_categories.map{ id->
                                    Categories.expensesCategory.find { id == it.id }?.name ?: ""
                                }
                                Text(
                                    text = "4.",
                                    style = MaterialTheme.typography.titleSmall.copy(textColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                if (fixedCategories.isEmpty()) {
                                    DiffStyleLine("Немає фіксованих категорій", "")
                                } else {
                                    Column {
                                        DiffStyleLine("Фіксовані витрати:", "")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        fixedCategories.forEachIndexed { index, category ->
                                            DiffStyleLine(
                                                "${index + 1}) $category - ",
                                                "${settings.fixed_amount_stable[index]} грн."
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        DiffStyleLine(
                                            "Залишилось заплатити цього місяця: ",
                                            "${settings.fixed_amount_current.sum()} грн."
                                        )
                                    }
                                }
                            }
                            BorderedBox {
                                Text(
                                    text = "5.",
                                    style = MaterialTheme.typography.titleSmall.copy(textColor),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Column {
                                    DiffStyleLine("Гаманці, включені до обліку:", "")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    walletNames.forEachIndexed { index, wallet ->
                                        DiffStyleLine(
                                            "${index + 1}) $wallet",
                                            ""
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}