package com.benzomageddon.data.repository

import com.benzomageddon.data.CategorySum
import com.benzomageddon.data.Transaction
import com.benzomageddon.data.TransactionType
import com.benzomageddon.data.dao.TransactionDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun observeAll(): Flow<List<Transaction>> = transactionDao.observeAll()

    fun observeRange(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.observeRange(start, end)

    suspend fun getById(id: Int): Transaction? = transactionDao.getById(id)

    suspend fun add(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)

    suspend fun clearAll() = transactionDao.deleteAll()

    fun observeTotalByType(type: TransactionType): Flow<Double> =
        transactionDao.observeTotalByType(type)

    fun observeSumByTypeBetween(start: Long, end: Long, type: TransactionType): Flow<Double> =
        transactionDao.observeSumByTypeBetween(start, end, type)

    fun observeExpenseSumsBetween(start: Long, end: Long): Flow<List<CategorySum>> =
        transactionDao.observeExpenseSumsBetween(start, end)

    fun observeIncomeSumsBetween(start: Long, end: Long): Flow<List<CategorySum>> =
        transactionDao.observeIncomeSumsBetween(start, end)
}
