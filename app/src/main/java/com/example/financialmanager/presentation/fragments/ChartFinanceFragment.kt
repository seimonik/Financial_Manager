package com.example.financialmanager.presentation.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.financialmanager.R
import com.example.financialmanager.databinding.FragmentChartFinanceBinding
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.presentation.util.launchWhenStarted
import com.example.financialmanager.presentation.viewmodels.TransactionsFragmentViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chart_finance.pie_chart_expenses
import kotlinx.android.synthetic.main.fragment_chart_finance.pie_chart_income
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.util.ArrayList
import java.util.Date

@AndroidEntryPoint
class ChartFinanceFragment : Fragment() {
    private var _binding: FragmentChartFinanceBinding? = null
    private val binding: FragmentChartFinanceBinding get() = _binding!!

    lateinit var pieChart: PieChart
    private val viewModel: TransactionsFragmentViewModel by viewModels()
    val entries: ArrayList<PieEntry> = ArrayList()
    val entriesExpense: ArrayList<PieEntry> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createPieChartIncome()

        createPieChartExpense()
    }

    private fun createPieChartIncome() {
        val current = LocalDate.now()
        val month = current.month.value

        val curMonths = month - 1
        val monthsAgo = if (month == 1) 12 else month - 2

        viewModel.transactions.onEach {
            val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Income }
                .sumOf { it.amount }
            val month0 = it.filter { t ->
                t.type == TransactionType.Income && Date(t.createdAt).month == curMonths
            }.sumOf { it.amount }
            val month1 = it.filter { t ->
                t.type == TransactionType.Income && Date(t.createdAt).month == monthsAgo
            }.sumOf { it.amount }

            val month0_part = (month0 / totalIncomeBalance).toFloat() * 100f
            val month1_part = (month1 / totalIncomeBalance).toFloat() * 100f
            val month2_part = 100.0f - month0_part - month1_part

            entries.add(PieEntry(month0_part))
            entries.add(PieEntry(month1_part))
            entries.add(PieEntry(month2_part))

            pieChart = pie_chart_income

            pieChart.setUsePercentValues(true) // установка отображения процентов
            pieChart.getDescription().setEnabled(false) // описание
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f) // смещение

            // установка коэффициента замедления выплывания графика (от 0 до 1)
            pieChart.setDragDecelerationFrictionCoef(0.95f)

            // задаем отверстие и цвет отверстия для круговой диаграммы
            pieChart.setDrawHoleEnabled(true)
            pieChart.setHoleColor(Color.WHITE)

            // on below line we are setting circle color and alpha
            pieChart.setTransparentCircleColor(Color.WHITE)
            pieChart.setTransparentCircleAlpha(110)

            // on  below line we are setting hole radius
            pieChart.setHoleRadius(58f)
            pieChart.setTransparentCircleRadius(52f)

            // on below line we are setting center text
            pieChart.setDrawCenterText(true)

            // on below line we are setting
            // rotation for our pie chart
            pieChart.setRotationAngle(0f)

            // enable rotation of the pieChart by touch
            pieChart.setRotationEnabled(true)
            pieChart.setHighlightPerTapEnabled(true)

            // on below line we are setting animation for our pie chart
            pieChart.animateY(1400, Easing.EaseInOutQuad)

            // on below line we are disabling our legend for pie chart
            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            // on below line we are setting pie data set
            val dataSet = PieDataSet(entries, "Mobile OS")

            // on below line we are setting icons.
            dataSet.setDrawIcons(false)

            // on below line we are setting slice for pie
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            // add a lot of colors to list
            val colors: ArrayList<Int> = ArrayList()
            colors.add(resources.getColor(R.color.purple_200))
            colors.add(resources.getColor(R.color.yellow))
            colors.add(resources.getColor(R.color.red))

            // on below line we are setting colors.
            dataSet.colors = colors

            // on below line we are setting pie data set
            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(15f)
            data.setValueTypeface(Typeface.DEFAULT_BOLD)
            data.setValueTextColor(Color.WHITE)
            pieChart.setData(data)

            // undo all highlights
            pieChart.highlightValues(null)

            // loading chart
            pieChart.invalidate()

        }.launchWhenStarted(lifecycleScope)
    }

    private fun createPieChartExpense() {
        val current = LocalDate.now()
        val month = current.month.value

        val curMonths = month - 1
        val monthsAgo = if (month == 1) 12 else month - 2

        viewModel.transactions.onEach {
            val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Expense }
                .sumOf { it.amount }
            val month0 = it.filter { t ->
                t.type == TransactionType.Expense && Date(t.createdAt).month == curMonths
            }.sumOf { it.amount }
            val month1 = it.filter { t ->
                t.type == TransactionType.Expense && Date(t.createdAt).month == monthsAgo
            }.sumOf { it.amount }

            val month0_part = (month0 / totalIncomeBalance).toFloat() * 100f
            val month1_part = (month1 / totalIncomeBalance).toFloat() * 100f
            val month2_part = 100.0f - month0_part - month1_part

            entriesExpense.add(PieEntry(month0_part))
            entriesExpense.add(PieEntry(month1_part))
            entriesExpense.add(PieEntry(month2_part))

            pieChart = pie_chart_expenses

//         on below line we are setting user percent value,
//         setting description as enabled and offset for pie chart
            pieChart.setUsePercentValues(true)
            pieChart.getDescription().setEnabled(false)
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

            // on below line we are setting drag for our pie chart
            pieChart.setDragDecelerationFrictionCoef(0.95f)

            // on below line we are setting hole
            // and hole color for pie chart
            pieChart.setDrawHoleEnabled(true)
            //pieChart.setHoleColor(Color.WHITE)
            pieChart.setHoleColor(Color.GRAY)

            // on below line we are setting circle color and alpha
            pieChart.setTransparentCircleColor(Color.WHITE)
            pieChart.setTransparentCircleAlpha(110)

            // on  below line we are setting hole radius
            pieChart.setHoleRadius(58f)
            pieChart.setTransparentCircleRadius(52f)

            // on below line we are setting center text
            pieChart.setDrawCenterText(true)

            // on below line we are setting
            // rotation for our pie chart
            pieChart.setRotationAngle(0f)

            // enable rotation of the pieChart by touch
            pieChart.setRotationEnabled(true)
            pieChart.setHighlightPerTapEnabled(true)

            // on below line we are setting animation for our pie chart
            pieChart.animateY(1400, Easing.EaseInOutQuad)

            // on below line we are disabling our legend for pie chart
            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            // on below line we are setting pie data set
            val dataSet = PieDataSet(entriesExpense, "Mobile OS")

            // on below line we are setting icons.
            dataSet.setDrawIcons(false)

            // on below line we are setting slice for pie
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            // add a lot of colors to list
            val colors: ArrayList<Int> = ArrayList()
            colors.add(resources.getColor(R.color.purple_200))
            colors.add(resources.getColor(R.color.yellow))
            colors.add(resources.getColor(R.color.red))

            // on below line we are setting colors.
            dataSet.colors = colors

            // on below line we are setting pie data set
            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(15f)
            data.setValueTypeface(Typeface.DEFAULT_BOLD)
            data.setValueTextColor(Color.WHITE)
            pieChart.setData(data)

            // undo all highlights
            pieChart.highlightValues(null)

            // loading chart
            pieChart.invalidate()

        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}