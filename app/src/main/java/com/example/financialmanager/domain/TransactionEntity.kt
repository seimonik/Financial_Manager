package com.example.financialmanager.domain

import com.example.financialmanager.domain.enums.TransactionType

data class TransactionEntity (
    val id: Int,
    val categoryId: Int,
    val name: String,
    val amount: Double,
    val type: TransactionType,
    val createdAt: Long,
    val imagePath: String? = null,
)