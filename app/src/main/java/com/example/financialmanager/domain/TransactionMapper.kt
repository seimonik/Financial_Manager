package com.example.financialmanager.domain

fun com.example.financialmanager.data.local.entity.TransactionEntity.toDomain(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        amount = this.amount,
        type = this.type,
        createdAt = this.createdAt,
        imagePath = this.imagePath,
    )
}

fun TransactionEntity.toLocal(): com.example.financialmanager.data.local.entity.TransactionEntity {
    return com.example.financialmanager.data.local.entity.TransactionEntity(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        amount = this.amount,
        type = this.type,
        createdAt = this.createdAt,
        imagePath = this.imagePath,
    )
}