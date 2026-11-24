package com.benzomageddon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

enum class PaymentType { CASH, CARD }

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val categoryId: Int,
    val date: Long,
    val type: TransactionType,
    val note: String = "",
    val paymentType: PaymentType = PaymentType.CASH
)
