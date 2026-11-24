package com.benzomageddon.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.benzomageddon.formatMoney
import com.benzomageddon.formatShortDate
import com.benzomageddon.ui.theme.Expense
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.Income
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import kotlin.math.min

data class ChartSegment(
    val label: String,
    val value: Double,
    val color: Color
)

@Composable
fun PieChart(
    segments: List<ChartSegment>,
    modifier: Modifier = Modifier
) {
    val total = segments.sumOf { it.value }.coerceAtLeast(0.0)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (total <= 0) return@Canvas
            val strokeWidth = min(size.minDimension * 0.14f, 40f)
            val radius = (min(size.minDimension, size.maxDimension) - strokeWidth) / 2f
            var startAngle = -90f
            segments.forEach { segment ->
                val sweep = (segment.value / total * 360.0).toFloat()
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                formatMoney(total),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "всего",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransactionRow(
    amount: Double,
    isExpense: Boolean,
    note: String,
    date: Long,
    categoryName: String?,
    categoryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Canvas(Modifier.size(8.dp)) {
                    drawCircle(color = categoryColor)
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        categoryName ?: "Без категории",
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = PlexSans,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (note.isNotBlank()) {
                        Text(
                            note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Text(
                        formatShortDate(date),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = PlexMono,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = (if (isExpense) "−" else "+") + " " + formatMoney(amount),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium,
                color = if (isExpense) Expense else Income
            )
        }
        HorizontalDivider(
            color = Hairline,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}
