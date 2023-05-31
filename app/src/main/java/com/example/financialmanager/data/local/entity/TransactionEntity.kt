package com.example.financialmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financialmanager.domain.enums.TransactionType

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var categoryId: Int,
    var name: String,
    val amount: Double,
    var type: TransactionType,
    val createdAt: Long,
    val imagePath: String? = null,
)