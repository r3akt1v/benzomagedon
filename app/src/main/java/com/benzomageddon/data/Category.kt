package com.benzomageddon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: TransactionType,
    val color: Long = 0xFF9E9E9E,
    val icon: String = ""
)
