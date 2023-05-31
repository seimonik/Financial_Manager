package com.example.financialmanager.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.financialmanager.databinding.FragmentChartFinanceBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_chart_finance.barChart
import java.util.ArrayList

class ChartFinanceFragment : Fragment() {
    private var _binding: FragmentChartFinanceBinding? = null
    private val binding: FragmentChartFinanceBinding get() = _binding!!

    lateinit var barList: ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barList= ArrayList()
        barList.add(BarEntry(1f,500f))
        barList.add(BarEntry(2f,100f))
        barList.add(BarEntry(3f,300f))
        barList.add(BarEntry(4f,800f))
        barList.add(BarEntry(5f,400f))
        barList.add(BarEntry(6f,1000f))
        barList.add(BarEntry(7f,800f))
        barDataSet= BarDataSet(barList,"Population")
        barData= BarData(barDataSet)
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS,250)
        barChart.data=barData
        barDataSet.valueTextColor= Color.BLACK
        barDataSet.valueTextSize=15f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}