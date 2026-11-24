package com.benzomageddon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.benzomageddon.components.TransactionRow
import com.benzomageddon.ui.theme.Accent
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import com.benzomageddon.ui.theme.TextFaint
import com.benzomageddon.ui.theme.TextSecondary
import com.benzomageddon.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    onTransactionClick: (Int) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val monthLabel = state.month.format(
        DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
    ).replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // month switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::previousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    "Предыдущий месяц",
                    tint = TextSecondary
                )
            }
            Text(
                monthLabel,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium
            )
            IconButton(
                onClick = viewModel::nextMonth,
                enabled = state.monthOffset < 0
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    "Следующий месяц",
                    tint = if (state.monthOffset < 0) TextSecondary else TextFaint
                )
            }
        }

        // weekday header
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс").forEach { d ->
                Text(
                    d,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = PlexMono,
                    color = TextFaint
                )
            }
        }

        val cells = buildCalendarCells(state.month)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(cells) { cell ->
                if (cell == null) {
                    Box(Modifier.aspectRatio(1f))
                } else {
                    val dayTransactions = state.daysInMonth[cell].orEmpty()
                    val isSelected = state.selectedDay == cell
                    val isToday = cell == LocalDate.now()
                    val hasOps = dayTransactions.isNotEmpty()
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Accent.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .then(
                                if (isToday && !isSelected)
                                    Modifier.border(1.dp, Hairline, CircleShape)
                                else Modifier
                            )
                            .clickable { viewModel.selectDay(cell) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                cell.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = PlexMono,
                                fontWeight = if (isSelected || isToday) FontWeight.Medium else FontWeight.Normal,
                                color = when {
                                    isSelected -> Accent
                                    isToday -> MaterialTheme.colorScheme.onSurface
                                    else -> TextSecondary
                                }
                            )
                            if (hasOps) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 3.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> Accent
                                                dayTransactions.any {
                                                    it.type == com.benzomageddon.data.TransactionType.EXPENSE
                                                } -> com.benzomageddon.ui.theme.Expense
                                                else -> com.benzomageddon.ui.theme.Income
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // selected day
        state.selectedDay?.let { day ->
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    day.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = PlexMono,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                HorizontalDivider(color = Hairline)
                if (state.selectedTransactions.isEmpty()) {
                    Text(
                        "нет операций",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = PlexSans,
                        color = TextFaint,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column {
                        state.selectedTransactions.forEach { transaction ->
                            val category = state.categories[transaction.categoryId]
                            TransactionRow(
                                amount = transaction.amount,
                                isExpense = transaction.type == com.benzomageddon.data.TransactionType.EXPENSE,
                                note = transaction.note,
                                date = transaction.date,
                                categoryName = category?.name,
                                categoryColor = Color(category?.color ?: 0xFF9AA7B8),
                                onClick = { onTransactionClick(transaction.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildCalendarCells(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val offset = first.dayOfWeek.value - 1
    val days = month.lengthOfMonth()
    val cells = mutableListOf<LocalDate?>()
    repeat(offset) { cells.add(null) }
    for (d in 1..days) cells.add(month.atDay(d))
    return cells
}
