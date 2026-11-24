package com.benzomageddon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benzomageddon.data.Category
import com.benzomageddon.data.Transaction
import com.benzomageddon.data.TransactionType
import com.benzomageddon.data.repository.CategoryRepository
import com.benzomageddon.data.repository.RatesRepository
import com.benzomageddon.data.repository.TransactionRepository
import com.benzomageddon.currentMonth
import com.benzomageddon.dayStart
import com.benzomageddon.monthEnd
import com.benzomageddon.monthStart
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val balance: Double = 0.0,
    val monthExpense: Double = 0.0,
    val todayExpense: Double = 0.0,
    val usdRate: Double? = null,
    val eurRate: Double? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val categories: Map<Int, Category> = emptyMap()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    ratesRepository: RatesRepository
) : ViewModel() {

    private val month = currentMonth()
    private val today = LocalDate.now()

    private val totals = combine(
        transactionRepository.observeTotalByType(TransactionType.INCOME),
        transactionRepository.observeTotalByType(TransactionType.EXPENSE),
        transactionRepository.observeSumByTypeBetween(
            monthStart(month), monthEnd(month), TransactionType.EXPENSE
        ),
        transactionRepository.observeSumByTypeBetween(
            dayStart(today), dayStart(today.plusDays(1)) - 1, TransactionType.EXPENSE
        )
    ) { income, expense, monthExpense, todayExpense ->
        Quad(income, expense, monthExpense, todayExpense)
    }

    private val extras = combine(
        transactionRepository.observeAll(),
        categoryRepository.observeAll(),
        ratesRepository.rates
    ) { transactions, categories, rates -> Triple(transactions, categories, rates) }

    val uiState: StateFlow<HomeUiState> = combine(totals, extras) { quad, (transactions, categories, rateMap) ->
        HomeUiState(
            balance = quad.income - quad.expense,
            monthExpense = quad.monthExpense,
            todayExpense = quad.todayExpense,
            usdRate = rateMap["USD"],
            eurRate = rateMap["EUR"],
            recentTransactions = transactions.take(10),
            categories = categories.associateBy { it.id }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch { ratesRepository.refresh() }
    }

    private data class Quad(
        val income: Double,
        val expense: Double,
        val monthExpense: Double,
        val todayExpense: Double
    )
}
