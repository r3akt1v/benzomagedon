package com.benzomageddon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.benzomageddon.components.ChartSegment
import com.benzomageddon.components.PieChart
import com.benzomageddon.formatMoney
import com.benzomageddon.ui.theme.Expense
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.Income
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import com.benzomageddon.ui.theme.TextFaint
import com.benzomageddon.ui.theme.TextSecondary
import com.benzomageddon.viewmodel.CategoryBreakdown
import com.benzomageddon.viewmodel.StatsViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val monthLabel = state.month.format(
        DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
    ).replaceFirstChar { it.uppercase() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // month switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::previousMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Предыдущий месяц",
                        tint = TextSecondary
                    )
                }
                Text(
                    monthLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = PlexMono,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = viewModel::nextMonth,
                    enabled = state.monthOffset < 0
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Следующий месяц",
                        tint = if (state.monthOffset < 0) TextSecondary else TextFaint
                    )
                }
            }
        }

        // totals row
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                TotalCell("доходы", state.totalIncome, Income, Modifier.weight(1f))
                TotalCell("расходы", state.totalExpense, Expense, Modifier.weight(1f))
                TotalCell("баланс", state.balance, MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
            }
            HorizontalDivider(color = Hairline, modifier = Modifier.padding(top = 20.dp))
        }

        // expense breakdown
        item {
            Text(
                "расходы по категориям",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = PlexMono,
                color = TextSecondary,
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
            )
            if (state.expenseBreakdown.isEmpty()) {
                Text(
                    "нет данных за этот месяц",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PlexSans,
                    color = TextFaint
                )
            } else {
                PieChart(
                    segments = state.expenseBreakdown.map { b ->
                        ChartSegment(b.category.name, b.amount, Color(b.category.color))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                state.expenseBreakdown.forEach { BreakdownRow(it) }
            }
        }

        // income breakdown
        item {
            Text(
                "доходы по категориям",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = PlexMono,
                color = TextSecondary,
                modifier = Modifier.padding(top = 28.dp, bottom = 4.dp)
            )
            if (state.incomeBreakdown.isEmpty()) {
                Text(
                    "нет данных за этот месяц",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PlexSans,
                    color = TextFaint,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                state.incomeBreakdown.forEach { BreakdownRow(it) }
            }
        }

        item { androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 96.dp)) }
    }
}

@Composable
private fun BreakdownRow(item: CategoryBreakdown) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(Modifier.size(8.dp)) {
            drawCircle(color = Color(item.category.color))
        }
        Text(
            item.category.name,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = PlexSans,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            formatMoney(item.amount),
            style = MaterialTheme.typography.titleSmall,
            fontFamily = PlexMono,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "  ${item.percent.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontFamily = PlexMono,
            color = TextFaint
        )
    }
}

@Composable
private fun TotalCell(label: String, value: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = PlexMono,
            color = TextFaint
        )
        Text(
            formatMoney(value),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = PlexMono,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
