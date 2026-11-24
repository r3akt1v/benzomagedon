package com.benzomageddon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.benzomageddon.data.PaymentType
import com.benzomageddon.data.TransactionType
import com.benzomageddon.formatShortDate
import com.benzomageddon.formatShortDate
import com.benzomageddon.ui.theme.Accent
import com.benzomageddon.ui.theme.Hairline
import com.benzomageddon.ui.theme.PlexMono
import com.benzomageddon.ui.theme.PlexSans
import com.benzomageddon.ui.theme.Raised
import com.benzomageddon.ui.theme.TextFaint
import com.benzomageddon.ui.theme.TextSecondary
import com.benzomageddon.viewmodel.TransactionFormViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionFormScreen(
    transactionId: Int?,
    onBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Text(
            if (state.isEdit) "изменить" else "новая операция",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = Accent,
            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
        )

        // type toggle — minimal text switch
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            TypeTab(
                label = "расход",
                selected = state.type == TransactionType.EXPENSE,
                onClick = { viewModel.onTypeChange(TransactionType.EXPENSE) }
            )
            TypeTab(
                label = "доход",
                selected = state.type == TransactionType.INCOME,
                onClick = { viewModel.onTypeChange(TransactionType.INCOME) }
            )
        }

        // amount — the hero of the form
        OutlinedTextField(
            value = state.amount,
            onValueChange = viewModel::onAmountChange,
            label = { Text("сумма") },
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        // categories
        Text(
            "категория",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = TextSecondary,
            modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                val selected = state.categoryId == category.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) Accent.copy(alpha = 0.15f) else Raised)
                        .border(
                            1.dp,
                            if (selected) Accent else Hairline,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.onCategorySelect(category.id) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = PlexSans,
                        color = if (selected) Accent else TextSecondary
                    )
                }
            }
        }

        // date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "дата",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = PlexMono,
                    color = TextSecondary
                )
                Text(
                    formatShortDate(state.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = PlexSans,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            TextButton(onClick = { showDatePicker = true }) { Text("изменить") }
        }

        // payment type
        Text(
            "тип платы",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PlexMono,
            color = TextSecondary,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            TypeTab(
                label = "наличные",
                selected = state.paymentType == PaymentType.CASH,
                onClick = { viewModel.onPaymentTypeChange(PaymentType.CASH) }
            )
            TypeTab(
                label = "карта",
                selected = state.paymentType == PaymentType.CARD,
                onClick = { viewModel.onPaymentTypeChange(PaymentType.CARD) }
            )
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::onNoteChange,
            label = { Text("комментарий") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // save — accent block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Accent)
                .clickable { viewModel.save(onBack) }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "сохранить",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PlexMono,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1405)
            )
        }

        if (state.isEdit) {
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("удалить операцию", color = MaterialTheme.colorScheme.error)
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("отмена", color = TextFaint)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateChange(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                    showDatePicker = false
                }) { Text("ok") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("удалить операцию?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(onBack)
                    showDeleteDialog = false
                }) { Text("удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("отмена") }
            }
        )
    }
}

@Composable
private fun TypeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Accent.copy(alpha = 0.15f) else Raised)
            .border(1.dp, if (selected) Accent else Hairline, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = PlexSans,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) Accent else TextSecondary
        )
    }
}
