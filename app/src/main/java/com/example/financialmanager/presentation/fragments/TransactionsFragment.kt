package com.example.financialmanager.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.financialmanager.R
import com.example.financialmanager.databinding.FragmentTransactionsBinding
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.domain.toDomain
import com.example.financialmanager.presentation.adapters.TransactionsAdapter
import com.example.financialmanager.presentation.util.launchWhenStarted
import com.example.financialmanager.presentation.viewmodels.TransactionsFragmentViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TransactionsFragment : Fragment(), CoroutineScope by MainScope() {
    companion object {
        private val TAB_ALL = 0
        private val TAB_INCOME = 1
        private val TAB_EXPENSE = 2
    }

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabContent(null)
        viewModel.transactions.onEach {
            binding.recyclerViewTransactions.adapter = TransactionsAdapter(
                transactions = it.map { entity -> entity.toDomain() }
            )
        }.launchWhenStarted(lifecycleScope = lifecycleScope)

        binding.btnCreateTransaction.setOnClickListener {
            val fragment = CreateTransactionFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()
        }

        binding.btnChart.setOnClickListener {
            val fragment = ChartFinanceFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()
        }

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    TAB_ALL -> {
                        initTabContent(null)
                    }
                    TAB_INCOME -> {
                        initTabContent(TransactionType.Income)
                    }
                    TAB_EXPENSE -> {
                        initTabContent(TransactionType.Expense)
                    }
                    else -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

//        val transactions: MutableList<TransactionEntity> = mutableListOf()
//        for (i in 0 until 15) {
//            transactions.add(
//                TransactionEntity(
//                    id = i,
//                    categoryId = i,
//                    name = "SomeName",
//                    amount = (0..100000).random().toDouble(),
//                    type = if (i % 2 == 0) TransactionType.Income else TransactionType.Expense,
//                    createdAt = System.currentTimeMillis()
//                )
//            )
//        }
//        binding.recyclerViewTransactions.adapter = TransactionsAdapter(transactions)
    }

    private fun initTabContent(type: TransactionType?) {
        showProgressBar()
        viewModel.transactions.onEach {
            when(type) {
                TransactionType.Income -> {
                    binding.recyclerViewTransactions.adapter = TransactionsAdapter(
                        transactions = it.filter { entity -> entity.type == TransactionType.Income }
                            .map { it.toDomain() }.sortedByDescending { it.id }
                    )
                    val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Income }
                        .sumOf { it.amount }
                    binding.tvTotalIncomeExpense.text = "Р${totalIncomeBalance}"
                }
                TransactionType.Expense -> {
                    binding.recyclerViewTransactions.adapter = TransactionsAdapter(
                        transactions = it.filter { entity -> entity.type == TransactionType.Expense }
                            .map { it.toDomain() }.sortedByDescending { it.id }
                    )
                    val totalExpenseBalance = it.filter { t -> t.type == TransactionType.Expense }
                        .sumOf { it.amount }
                    binding.tvTotalIncomeExpense.text = "Р${totalExpenseBalance}"
                }
                null -> {
                    binding.recyclerViewTransactions.adapter = TransactionsAdapter(
                        transactions = it.map { entity -> entity.toDomain() }
                            .sortedByDescending { transaction -> transaction.id }
                    )
                }
            }

            val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Income }.sumOf { it.amount }
            val totalExpenseBalance = it.filter { t -> t.type == TransactionType.Expense }.sumOf { it.amount }
            binding.tvCurrentBalance.text = "Р${totalIncomeBalance - totalExpenseBalance}"
            hideProgressBar()
        }.launchWhenStarted(lifecycleScope)
    }

    private fun showProgressBar() {
        binding.progressBar.progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.progressBarLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}