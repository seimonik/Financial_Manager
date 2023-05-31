package com.example.financialmanager.data.local

import android.content.Context
import androidx.room.*
import com.example.financialmanager.data.local.dao.TransactionDao
import com.example.financialmanager.data.local.entity.TransactionEntity


@Database(
    entities = [
        TransactionEntity::class
    ],
    version = 1
)
abstract class FinanceManagerDatabase : RoomDatabase() {

    abstract val transactionDao: TransactionDao

    companion object {
        const val DATABASE_NAME = "financeManagerEdu_db"

        @Volatile private var instance: FinanceManagerDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            FinanceManagerDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}