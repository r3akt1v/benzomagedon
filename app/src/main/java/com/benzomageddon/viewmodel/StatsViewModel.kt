package com.benzomageddon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benzomageddon.data.Category
import com.benzomageddon.data.CategorySum
import com.benzomageddon.data.repository.CategoryRepository
import com.benzomageddon.data.repository.TransactionRepository
import com.benzomageddon.data.TransactionType
import com.benzomageddon.currentMonth
import com.benzomageddon.monthEnd
import com.benzomageddon.monthStart
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class CategoryBreakdown(
    val category: Category,
    val amount: Double,
    val percent: Float
)

data class StatsUiState(
    val month: YearMonth = currentMonth(),
    val monthOffset: Int = 0,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val balance: Double = 0.0,
    val expenseBreakdown: List<CategoryBreakdown> = emptyList(),
    val incomeBreakdown: List<CategoryBreakdown> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val monthOffset = MutableStateFlow(0)

    val uiState: StateFlow<StatsUiState> = monthOffset
        .flatMapLatest { offset ->
            val ym = currentMonth().plusMonths(offset.toLong())
            val start = monthStart(ym)
            val end = monthEnd(ym)

            val sums = combine(
                transactionRepository.observeSumByTypeBetween(start, end, TransactionType.EXPENSE),
                transactionRepository.observeSumByTypeBetween(start, end, TransactionType.INCOME)
            ) { expense, income -> expense to income }

            val breakdowns = combine(
                transactionRepository.observeExpenseSumsBetween(start, end),
                transactionRepository.observeIncomeSumsBetween(start, end),
                categoryRepository.observeAll()
            ) { expSums, incSums, categories -> Triple(expSums, incSums, categories) }

            combine(sums, breakdowns) { (expense, income), (expSums, incSums, categories) ->
                StatsUiState(
                    month = ym,
                    monthOffset = offset,
                    totalExpense = expense,
                    totalIncome = income,
                    balance = income - expense,
                    expenseBreakdown = buildBreakdown(expSums, categories, expense),
                    incomeBreakdown = buildBreakdown(incSums, categories, income)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    fun previousMonth() {
        monthOffset.value -= 1
    }

    fun nextMonth() {
        if (monthOffset.value < 0) monthOffset.value += 1
    }

    private fun buildBreakdown(
        sums: List<CategorySum>,
        categories: List<Category>,
        total: Double
    ): List<CategoryBreakdown> {
        val byId = categories.associateBy { it.id }
        return sums
            .mapNotNull { sum ->
                val category = byId[sum.categoryId] ?: return@mapNotNull null
                CategoryBreakdown(
                    category = category,
                    amount = sum.total,
                    percent = if (total > 0) (sum.total / total * 100).toFloat() else 0f
                )
            }
            .sortedByDescending { it.amount }
    }
}
