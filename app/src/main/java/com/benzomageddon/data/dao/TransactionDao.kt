package com.benzomageddon.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.benzomageddon.data.CategorySum
import com.benzomageddon.data.Transaction
import com.benzomageddon.data.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    fun observeRange(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type")
    fun observeTotalByType(type: TransactionType): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    fun observeSumByTypeBetween(start: Long, end: Long, type: TransactionType): Flow<Double>

    @Query("SELECT categoryId, SUM(amount) AS total FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end GROUP BY categoryId")
    fun observeExpenseSumsBetween(start: Long, end: Long): Flow<List<CategorySum>>

    @Query("SELECT categoryId, SUM(amount) AS total FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end GROUP BY categoryId")
    fun observeIncomeSumsBetween(start: Long, end: Long): Flow<List<CategorySum>>
}
