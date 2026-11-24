package com.benzomageddon.data.converter

import androidx.room.TypeConverter
import com.benzomageddon.data.PaymentType
import com.benzomageddon.data.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromPaymentType(value: PaymentType): String = value.name

    @TypeConverter
    fun toPaymentType(value: String): PaymentType = PaymentType.valueOf(value)
}
