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
import com.example.financialmanager.data.local.converters.TimeConverter
import com.example.financialmanager.databinding.FragmentChartFinanceBinding
import com.example.financialmanager.domain.TransactionEntity
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.domain.toDomain
import com.example.financialmanager.presentation.adapters.TransactionsAdapter
import com.example.financialmanager.presentation.util.launchWhenStarted
import com.example.financialmanager.presentation.viewmodels.TransactionsFragmentViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chart_finance.pie_chart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
//import kotlinx.android.synthetic.main.fragment_chart_finance.barChart
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import kotlin.time.milliseconds

@AndroidEntryPoint
class ChartFinanceFragment : Fragment() {
    private var _binding: FragmentChartFinanceBinding? = null
    private val binding: FragmentChartFinanceBinding get() = _binding!!

//    lateinit var barList: ArrayList<BarEntry>
//    lateinit var barDataSet: BarDataSet
//    lateinit var barData: BarData

    lateinit var pieChart: PieChart
    private val viewModel: TransactionsFragmentViewModel by viewModels()
    val entries: ArrayList<PieEntry> = ArrayList()
    var listTransEntity : List<TransactionEntity> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initTabContent(TransactionType.Income)

        val current = LocalDate.now()
        val month = current.month.value
//        var month0_part: Float = 0.0f
//        var month1_part = 0.0f
//        var month2_part = 0.0f
//        var totalIncomeBalance: Double = 1.0
//        var month0: Double = 0.0
//        var month1: Double=0.0

        viewModel.transactions.onEach {
            listTransEntity = it.filter { entity -> entity.type == TransactionType.Income }
                .map { it.toDomain() }.sortedByDescending { it.id }
            val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Income }
                .sumOf { it.amount }
            val month0 = it.filter { t ->
                t.type == TransactionType.Income && month - Date(t.createdAt).month == 1
            }.sumOf { it.amount }
            val month1 = it.filter { t ->
                t.type == TransactionType.Income && month - Date(t.createdAt).month == 2
            }.sumOf { it.amount }

            val month0_part = (month0 / totalIncomeBalance).toFloat()*100f
            val month1_part = (month1 / totalIncomeBalance).toFloat()*100f
            val month2_part = 100.0f - month0_part - month1_part

            entries.add(PieEntry(month0_part))
            entries.add(PieEntry(month1_part))
            entries.add(PieEntry(month2_part))

            pieChart = pie_chart

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

            // on below line we are creating array list and
            // adding data to it to display in pie chart
//        entries.add(PieEntry(70f))
//        entries.add(PieEntry(20f))
//        entries.add(PieEntry(10f))

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
//        barList= ArrayList()
//        barList.add(BarEntry(1f,500f))
//        barList.add(BarEntry(2f,100f))
//        barList.add(BarEntry(3f,300f))
//        barList.add(BarEntry(4f,800f))
//        barList.add(BarEntry(5f,400f))
//        barList.add(BarEntry(6f,1000f))
//        barList.add(BarEntry(7f,800f))
//        barDataSet= BarDataSet(barList,"Population")
//        barData= BarData(barDataSet)
//        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS,250)
//        barChart.data=barData
//        barDataSet.valueTextColor= Color.BLACK
//        barDataSet.valueTextSize=15f
    }

//    private fun initTabContent() {
//        val current = LocalDate.now()
//        val month = current.month.value
//        //val year = current.year
//
//        var month0 = 0.0
//        var month1 = 0.0
//        var month2 = 0.0
//
//        viewModel.transactionIncome.onEach {
//            it.forEach {
//                val curDate = Date(it.createdAt)
//                val curMonth = curDate.month
//
//                when(curMonth-month){
//                    0 -> {
//                        month0 += it.amount
//                    }
//                    1 -> {
//                        month1 += it.amount
//                    }
//                    2 -> {
//                        month2 += it.amount
//                    }
//                }
//            }
//        }
//        var sum = month0 + month1 + month2
//        var month0_part = month0 / sum
//        var month1_part = month1 / sum
//        var month2_part = 100.0 - month0_part - month1_part
//        entries.add(PieEntry(month0_part.toFloat()))
//        entries.add(PieEntry(month1_part.toFloat()))
//        entries.add(PieEntry(month2_part.toFloat()))
//    }

    private fun initTabContent(type: TransactionType?) {
        val current = LocalDate.now()
        val month = current.month.value

        viewModel.transactions.onEach {
            when(type) {
                TransactionType.Income -> {
                    listTransEntity = it.filter { entity -> entity.type == TransactionType.Income }
                            .map { it.toDomain() }.sortedByDescending { it.id }
                    val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Income }
                        .sumOf { it.amount }
                    val month0 = it.filter { t -> t.type == TransactionType.Income &&
                            month - Date(t.createdAt).month == 0 }
                        .sumOf { it.amount }
                    val month1 = it.filter { t -> t.type == TransactionType.Income &&
                            month - Date(t.createdAt).month == 1 }
                        .sumOf { it.amount }
//                    val month2 = it.filter { t -> t.type == TransactionType.Income &&
//                            Date(t.createdAt).month -month == 2 }
//                        .sumOf { it.amount }

//                    var month0_part = (month0 / totalIncomeBalance)*100
//                    var month1_part = (month1 / totalIncomeBalance)
//                    var month2_part = 100.0 - month0_part - month1_part
                    var month0_part = 50.0f
                    var month1_part = 35.5f
                    var month2_part = 14.5f

                    entries.add(PieEntry(month0_part))
                    entries.add(PieEntry(month1_part))
                    entries.add(PieEntry(month2_part))

                    //binding.tvTotalIncomeExpense.text = "Р${totalIncomeBalance}"
                }
                TransactionType.Expense -> {
                    listTransEntity = it.filter { entity -> entity.type == TransactionType.Expense }
                        .map { it.toDomain() }.sortedByDescending { it.id }
                    val totalIncomeBalance = it.filter { t -> t.type == TransactionType.Expense }
                        .sumOf { it.amount }
                    val month0 = it.filter { t -> t.type == TransactionType.Expense &&
                            Date(t.createdAt).month -month == 0 }
                        .sumOf { it.amount }
                    val month1 = it.filter { t -> t.type == TransactionType.Expense &&
                            Date(t.createdAt).month -month == 1 }
                        .sumOf { it.amount }
                    val month2 = it.filter { t -> t.type == TransactionType.Expense &&
                            Date(t.createdAt).month -month == 2 }
                        .sumOf { it.amount }

                    var month0_part = month0 / totalIncomeBalance
                    var month1_part = month1 / totalIncomeBalance
                    var month2_part = 100.0 - month0_part - month1_part

                    entries.add(PieEntry(month0_part.toFloat()))
                    entries.add(PieEntry(month1_part.toFloat()))
                    entries.add(PieEntry(month2_part.toFloat()))
                }
            }
        }//.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}