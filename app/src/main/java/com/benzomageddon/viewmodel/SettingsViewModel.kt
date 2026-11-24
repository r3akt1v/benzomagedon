package com.benzomageddon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benzomageddon.data.repository.RatesRepository
import com.benzomageddon.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val amountInput: String = "",
    val rates: Map<String, Double> = emptyMap(),
    val transactionsCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val amountInput = MutableStateFlow("")
    private val count = transactionRepository.observeAll()

    val uiState: StateFlow<SettingsUiState> = combine(
        amountInput,
        ratesRepository.rates,
        count
    ) { input, rates, transactions ->
        SettingsUiState(
            amountInput = input,
            rates = rates,
            transactionsCount = transactions.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    init {
        viewModelScope.launch { ratesRepository.refresh() }
    }

    fun onAmountChange(value: String) {
        amountInput.value = value.filter { it.isDigit() || it == '.' || it == ',' }
    }

    fun clearAllData() {
        viewModelScope.launch {
            transactionRepository.clearAll()
        }
    }
}
