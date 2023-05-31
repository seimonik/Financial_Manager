package com.example.financialmanager.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialmanager.data.local.FinanceManagerDatabase
import com.example.financialmanager.domain.TransactionEntity
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.domain.toLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsFragmentViewModel @Inject constructor(
    private val db: FinanceManagerDatabase
) : ViewModel() {
    val transactions = db.transactionDao.getAllTransactions()

    fun insertTransaction(
        name: String,
        amount: Double,
        type: TransactionType,
        imagePath: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = TransactionEntity(
                id = 0,
                categoryId = 0,
                name = name,
                amount = amount,
                type = type,
                createdAt = System.currentTimeMillis(),
                imagePath = imagePath,
            )
            val id = db.transactionDao.insertTransaction(transaction.toLocal())
        }
    }
}