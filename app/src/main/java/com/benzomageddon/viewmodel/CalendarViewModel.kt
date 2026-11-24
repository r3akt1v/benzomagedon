package com.benzomageddon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benzomageddon.data.Category
import com.benzomageddon.data.Transaction
import com.benzomageddon.data.repository.CategoryRepository
import com.benzomageddon.data.repository.TransactionRepository
import com.benzomageddon.currentMonth
import com.benzomageddon.dayStart
import com.benzomageddon.monthEnd
import com.benzomageddon.monthStart
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class CalendarUiState(
    val month: YearMonth = currentMonth(),
    val monthOffset: Int = 0,
    val daysInMonth: Map<LocalDate, List<Transaction>> = emptyMap(),
    val selectedDay: LocalDate? = null,
    val selectedTransactions: List<Transaction> = emptyList(),
    val categories: Map<Int, Category> = emptyMap()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    private val monthOffset = MutableStateFlow(0)
    private val selectedDay = MutableStateFlow<LocalDate?>(null)

    private val transactionsFlow = monthOffset.flatMapLatest { offset ->
        val ym = currentMonth().plusMonths(offset.toLong())
        transactionRepository.observeRange(monthStart(ym), monthEnd(ym))
    }

    val uiState: StateFlow<CalendarUiState> = combine(
        monthOffset,
        transactionsFlow,
        selectedDay,
        categoryRepository.observeAll()
    ) { offset, transactions, day, categories ->
        val ym = currentMonth().plusMonths(offset.toLong())
        val days = transactions.groupBy {
            java.time.Instant.ofEpochMilli(it.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        }
        val selected = day?.let { days[it] } ?: emptyList()
        CalendarUiState(
            month = ym,
            monthOffset = offset,
            daysInMonth = days,
            selectedDay = day,
            selectedTransactions = selected.sortedByDescending { it.date },
            categories = categories.associateBy { it.id }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun previousMonth() {
        monthOffset.value -= 1
        selectedDay.value = null
    }

    fun nextMonth() {
        if (monthOffset.value < 0) {
            monthOffset.value += 1
            selectedDay.value = null
        }
    }

    fun selectDay(date: LocalDate) {
        selectedDay.value = if (selectedDay.value == date) null else date
    }
}
