package com.benzomageddon.data.repository

import com.benzomageddon.data.Category
import com.benzomageddon.data.TransactionType
import com.benzomageddon.data.dao.CategoryDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    fun observeAll(): Flow<List<Category>> = categoryDao.observeAll()

    suspend fun ensureDefaults() {
        if (categoryDao.count() > 0) return
        DEFAULT_CATEGORIES.forEach { categoryDao.insert(it) }
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category(name = "Продукты", type = TransactionType.EXPENSE, color = 0xFF43A047),
            Category(name = "Транспорт", type = TransactionType.EXPENSE, color = 0xFF1E88E5),
            Category(name = "Жильё", type = TransactionType.EXPENSE, color = 0xFF8E24AA),
            Category(name = "Развлечения", type = TransactionType.EXPENSE, color = 0xFFFB8C00),
            Category(name = "Одежда", type = TransactionType.EXPENSE, color = 0xFFE53935),
            Category(name = "Здоровье", type = TransactionType.EXPENSE, color = 0xFF00ACC1),
            Category(name = "Подписки", type = TransactionType.EXPENSE, color = 0xFF6D4C41),
            Category(name = "Прочее", type = TransactionType.EXPENSE, color = 0xFF757575),
            Category(name = "Зарплата", type = TransactionType.INCOME, color = 0xFF2E7D32),
            Category(name = "Подработка", type = TransactionType.INCOME, color = 0xFF1565C0),
            Category(name = "Подарки", type = TransactionType.INCOME, color = 0xFFAD1457),
            Category(name = "Прочее", type = TransactionType.INCOME, color = 0xFF546E7A)
        )
    }
}
