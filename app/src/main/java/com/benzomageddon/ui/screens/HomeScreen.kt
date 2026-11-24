package com.benzomageddon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.benzomageddon.components.TransactionRow
import com.benzomageddon.formatMoney
import com.benzomageddon.ui.theme.Accent
import com.benzomageddon.ui.theme.Expense
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.Income
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import com.benzomageddon.ui.theme.TextFaint
import com.benzomageddon.ui.theme.TextSecondary
import com.benzomageddon.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val categoryPalette = listOf(
    0xFF7FB5FF, 0xFF5BD6A0, 0xFFF5B841, 0xFFE58BB1, 0xFFB79CFF,
    0xFF6FD3E7, 0xFFF0925F, 0xFF9AA7B8, 0xFF8FD18F, 0xFFD98CB3,
    0xFF82B4F6, 0xFFC0C8D4
)

@Composable
fun categoryColor(colorLong: Long, id: Int): Color =
    if (colorLong != 0L) Color(colorLong) else Color(categoryPalette[id % categoryPalette.size])

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val todayLabel = LocalDate.now().format(
        DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Accent,
                contentColor = Color(0xFF1A1405)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "benzomageddon",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = PlexMono,
                        fontWeight = FontWeight.Medium,
                        color = Accent
                    )
                    Text(
                        todayLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = PlexMono,
                        color = TextFaint
                    )
                }
            }

            // balance hero
            item {
                Text(
                    "Баланс",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = PlexMono,
                    color = TextSecondary
                )
                Text(
                    formatMoney(state.balance),
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = PlexMono,
                    fontWeight = FontWeight.Medium,
                    color = if (state.balance < 0) Expense else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // today / month stats
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    StatCell(
                        label = "сегодня",
                        value = state.todayExpense,
                        isExpense = true,
                        modifier = Modifier.weight(1f)
                    )
                    StatCell(
                        label = "за месяц",
                        value = state.monthExpense,
                        isExpense = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Hairline, modifier = Modifier.padding(top = 20.dp))
            }

            // rates
            if (state.usdRate != null || state.eurRate != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        state.usdRate?.let { RateLabel("USD", it) }
                        state.eurRate?.let { RateLabel("EUR", it) }
                    }
                    HorizontalDivider(color = Hairline, modifier = Modifier.padding(top = 16.dp))
                }
            }

            // recent
            item {
                Text(
                    "операции",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = PlexMono,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    Text(
                        "пока пусто — добавь первую операцию",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = PlexSans,
                        color = TextFaint,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(state.recentTransactions, key = { it.id }) { transaction ->
                    val category = state.categories[transaction.categoryId]
                    TransactionRow(
                        amount = transaction.amount,
                        isExpense = transaction.type == com.benzomageddon.data.TransactionType.EXPENSE,
                        note = transaction.note,
                        date = transaction.date,
                        categoryName = category?.name,
                        categoryColor = categoryColor(
                            category?.color ?: 0xFF9AA7B8,
                            transaction.categoryId
                        ),
                        onClick = { onEditClick(transaction.id) }
                    )
                }
            }

            item { androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 96.dp)) }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    value: Double,
    isExpense: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = TextFaint
        )
        Text(
            formatMoney(value),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = PlexMono,
            fontWeight = FontWeight.Medium,
            color = if (isExpense) Expense else Income,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun RateLabel(code: String, ratePerRub: Double) {
    Text(
        "$code ${String.format(java.util.Locale.US, "%.2f", 1.0 / ratePerRub)} ₽",
        style = MaterialTheme.typography.labelMedium,
        fontFamily = PlexMono,
        color = TextFaint
    )
}
