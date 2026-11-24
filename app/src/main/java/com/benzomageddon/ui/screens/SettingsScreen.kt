package com.benzomageddon.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.benzomageddon.ui.theme.Accent
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import com.benzomageddon.ui.theme.TextFaint
import com.benzomageddon.ui.theme.TextSecondary
import com.benzomageddon.viewmodel.SettingsViewModel
import java.util.Locale

private val converterCurrencies = listOf("USD", "EUR", "CNY", "KZT")

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    val rubAmount = state.amountInput
        .replace(',', '.')
        .toDoubleOrNull()
        ?.takeIf { it >= 0 }
        ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Text(
            "настройки",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = Accent,
            modifier = Modifier.padding(top = 20.dp, bottom = 24.dp)
        )

        // converter
        Text(
            "конвертер",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "курс на сегодня · cbr через open.er-api.com",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PlexSans,
            color = TextFaint,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        OutlinedTextField(
            value = state.amountInput,
            onValueChange = viewModel::onAmountChange,
            label = { Text("сумма в рублях") },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge.copy(fontFamily = PlexMono),
            modifier = Modifier.fillMaxWidth()
        )

        if (state.rates.isEmpty()) {
            Text(
                "нет данных о курсах — проверь интернет",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PlexSans,
                color = TextFaint,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                converterCurrencies.forEach { code ->
                    val ratePerUnit = state.rates[code]
                    CurrencyRow(
                        code = code,
                        ratePerUnit = ratePerUnit,
                        rubAmount = rubAmount,
                        hasAmount = state.amountInput.isNotBlank()
                    )
                }
            }
        }

        HorizontalDivider(color = Hairline, modifier = Modifier.padding(vertical = 28.dp))

        // data section
        Text(
            "данные",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "операций всего: ${state.transactionsCount}",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = PlexSans,
            color = TextFaint,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, com.benzomageddon.ui.theme.Expense.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { showClearDialog = true }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                "очистить все операции",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = PlexMono,
                color = com.benzomageddon.ui.theme.Expense
            )
        }

        androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 96.dp))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("очистить данные?") },
            text = { Text("все операции будут удалены безвозвратно. категории сохранятся.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showClearDialog = false
                }) { Text("удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("отмена") }
            }
        )
    }
}

@Composable
private fun CurrencyRow(code: String, ratePerUnit: Double?, rubAmount: Double, hasAmount: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                code,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                if (ratePerUnit != null) "1 $code = ${"%.2f".format(Locale.US, 1.0 / ratePerUnit)} ₽"
                else "нет курса",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = PlexMono,
                color = TextFaint
            )
        }
        Text(
            if (ratePerUnit != null && hasAmount) "%.2f".format(Locale.US, rubAmount * ratePerUnit)
            else if (ratePerUnit != null) "—"
            else "—",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = PlexMono,
            fontWeight = FontWeight.Medium,
            color = Accent
        )
    }
}
