package com.benzomageddon.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benzomageddon.data.Category
import com.benzomageddon.data.PaymentType
import com.benzomageddon.data.Transaction
import com.benzomageddon.data.TransactionType
import com.benzomageddon.data.repository.CategoryRepository
import com.benzomageddon.data.repository.TransactionRepository
import com.benzomageddon.dayStart
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TransactionFormUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Int? = null,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val paymentType: PaymentType = PaymentType.CASH,
    val categories: List<Category> = emptyList(),
    val isEdit: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val editId: Int? = savedStateHandle.get<Int>("id")?.takeIf { it > 0 }

    private val state = MutableStateFlow(TransactionFormUiState(isEdit = editId != null))
    val uiState: StateFlow<TransactionFormUiState> = state

    private val selectedType = MutableStateFlow(TransactionType.EXPENSE)

    val categories: StateFlow<List<Category>> = combine(
        selectedType,
        categoryRepository.observeAll()
    ) { type, all -> all.filter { it.type == type } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val existing = editId?.let { transactionRepository.getById(it) }
            if (existing != null) {
                selectedType.value = existing.type
                state.value = state.value.copy(
                    amount = existing.amount.toString(),
                    type = existing.type,
                    categoryId = existing.categoryId,
                    date = Instant.ofEpochMilli(existing.date)
                        .atZone(ZoneId.systemDefault()).toLocalDate(),
                    note = existing.note,
                    paymentType = existing.paymentType
                )
            }
        }
    }

    fun onAmountChange(value: String) {
        state.value = state.value.copy(amount = value.filter { it.isDigit() || it == '.' || it == ',' })
    }

    fun onTypeChange(type: TransactionType) {
        selectedType.value = type
        state.value = state.value.copy(type = type, categoryId = null)
    }

    fun onCategorySelect(id: Int) {
        state.value = state.value.copy(categoryId = id)
    }

    fun onDateChange(date: LocalDate) {
        state.value = state.value.copy(date = date)
    }

    fun onNoteChange(note: String) {
        state.value = state.value.copy(note = note)
    }

    fun onPaymentTypeChange(paymentType: PaymentType) {
        state.value = state.value.copy(paymentType = paymentType)
    }

    fun delete(onDone: () -> Unit) {
        val id = editId ?: return
        viewModelScope.launch {
            transactionRepository.getById(id)?.let { transactionRepository.delete(it) }
            onDone()
        }
    }

    fun save(onDone: () -> Unit) {
        val s = state.value
        val amount = s.amount.replace(',', '.').toDoubleOrNull()
        when {
            amount == null || amount <= 0 -> {
                state.value = s.copy(error = "Введите сумму больше нуля")
                return
            }
            s.categoryId == null -> {
                state.value = s.copy(error = "Выберите категорию")
                return
            }
        }
        viewModelScope.launch {
            val transaction = Transaction(
                id = editId ?: 0,
                amount = amount,
                categoryId = s.categoryId,
                date = dayStart(s.date),
                type = s.type,
                note = s.note.trim(),
                paymentType = s.paymentType
            )
            if (editId != null) transactionRepository.update(transaction)
            else transactionRepository.add(transaction)
            onDone()
        }
    }
}
