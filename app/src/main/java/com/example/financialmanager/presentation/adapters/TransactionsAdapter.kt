package com.example.financialmanager.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.financialmanager.domain.TransactionEntity

import com.example.financialmanager.R
import com.example.financialmanager.databinding.RecyclerViewTransactionListItemBinding
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.presentation.extensions.dateToString
import java.util.Date

class TransactionsAdapter(
    private val transactions: List<TransactionEntity>
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerViewTransactionListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewTransactionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val transaction = transactions[position]
            when (transaction.type) {
                TransactionType.Income -> {
                    tvAmount.setTextColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.green)
                    )
                    tvAmount.text = "+Р${transaction.amount}"
                }
                TransactionType.Expense -> {
                    tvAmount.setTextColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.red)
                    )
                    tvAmount.text = "-Р${transaction.amount}"
                }
            }

            if (!transaction.imagePath.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(transaction.imagePath)
                    .centerCrop()
                    .into(ivItem)
            }

            tvDate.text = Date(transaction.createdAt).dateToString("dd MMMM")
            tvTitle.text = transaction.name
        }
    }

    override fun getItemCount(): Int {
        return transactions.count()
    }
}