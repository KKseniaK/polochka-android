package com.hse.polochka.feature.analytics.presentation.screen

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityAnalyticsBinding
import com.hse.polochka.databinding.ItemAnalyticsInsightBinding
import com.hse.polochka.feature.analytics.domain.model.AnalyticsMetric
import com.hse.polochka.feature.analytics.domain.model.AnalyticsSummary
import com.hse.polochka.feature.analytics.presentation.model.AnalyticsChartEntry
import com.hse.polochka.feature.analytics.presentation.state.AnalyticsUiState
import com.hse.polochka.feature.analytics.presentation.viewmodel.AnalyticsViewModel
import com.hse.polochka.feature.analytics.presentation.viewmodel.AnalyticsViewModelFactory
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment(R.layout.activity_analytics) {

    private var _binding: ActivityAnalyticsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: AnalyticsViewModel by viewModels {
        AnalyticsViewModelFactory(requireContext())
    }
    private val selectedMonth: Calendar = Calendar.getInstance()
    private var showPercentChart = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityAnalyticsBinding.bind(view)

        setupCharts()
        setupChartModeButtons()
        setupMonthSelector()
        observeAnalytics()
        loadAnalytics()
    }

    private fun setupMonthSelector() {
        binding.previousMonthButton.setOnClickListener {
            selectedMonth.add(Calendar.MONTH, -1)
            loadAnalytics()
        }
        binding.nextMonthButton.setOnClickListener {
            selectedMonth.add(Calendar.MONTH, 1)
            loadAnalytics()
        }
    }

    private fun observeAnalytics() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AnalyticsUiState.Content -> renderAnalytics(state.summary)
                        is AnalyticsUiState.Error -> Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_SHORT,
                        ).show()
                        AnalyticsUiState.Loading -> Unit
                    }
                }
            }
        }
    }

    private fun loadAnalytics() {
        binding.monthTextView.text = getString(R.string.analytics_month, selectedMonth.displayName())
        viewModel.load(selectedMonth.toServerMonth())
    }

    private fun renderAnalytics(summary: AnalyticsSummary) {
        binding.boughtValueTextView.text = "${summary.boughtProductsCount} продуктов"
        binding.savedMoneyValueTextView.text = "${summary.savedMoneyRub} руб."

        renderPercentChart(summary.percentChart.map { it.toChartEntry() })
        renderCountChart(summary.countChart.map { it.toChartEntry() })
        renderInsights(summary)
    }

    private fun renderInsights(summary: AnalyticsSummary) {
        val insights = binding.analyticsInsights

        setupInsight(
            insights.topLeftInsight,
            title = summary.insights.oftenBought.title,
            description = summary.insights.oftenBought.description,
            icon = summary.insights.oftenBought.iconKey.toIconRes(),
        )
        setupInsight(
            insights.topRightInsight,
            title = summary.insights.favoriteCategory.title,
            description = summary.insights.favoriteCategory.description,
            icon = summary.insights.favoriteCategory.iconKey.toIconRes(),
        )
        setupInsight(
            insights.bottomLeftInsight,
            title = summary.insights.familyFavoriteCategory.title,
            description = summary.insights.familyFavoriteCategory.description,
            icon = summary.insights.familyFavoriteCategory.iconKey.toIconRes(),
        )
        setupInsight(
            insights.bottomRightInsight,
            title = summary.insights.oftenSpoiled.title,
            description = summary.insights.oftenSpoiled.description,
            icon = summary.insights.oftenSpoiled.iconKey.toIconRes(),
        )

        insights.championNameTextView.text = summary.insights.purchaseChampion.displayName.uppercase()
        insights.championDescriptionTextView.text = getString(R.string.analytics_champion_description)
    }

    private fun setupInsight(
        binding: ItemAnalyticsInsightBinding,
        title: String,
        description: String,
        icon: Int,
    ) {
        binding.insightTitleTextView.text = title
        binding.insightDescriptionTextView.text = description
        binding.insightIconImageView.setImageResource(icon)
    }

    private fun setupChartModeButtons() {
        binding.percentModeButton.setOnClickListener {
            selectChartMode(showPercent = true)
        }

        binding.countModeButton.setOnClickListener {
            selectChartMode(showPercent = false)
        }
        selectChartMode(showPercent = true)
    }

    private fun setupCharts() {
        binding.percentChartView.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 0f
            transparentCircleRadius = 0f
            rotationAngle = 198f
            isRotationEnabled = false
            setTouchEnabled(false)
            setExtraOffsets(4f, 4f, 4f, 4f)
        }

        binding.countChartView.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)
            setTouchEnabled(false)
            setFitBars(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            setViewPortOffsets(20f, 24f, 20f, 72f)
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
                axisMinimum = 0f
            }
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.textColor = color(R.color.text_primary)
            xAxis.textSize = 11f
            xAxis.labelRotationAngle = -18f
            xAxis.setAvoidFirstLastClipping(true)
        }
    }

    private fun renderPercentChart(entries: List<AnalyticsChartEntry>) {
        val visibleEntries = entries.filter { it.value > 0 }
        val pieEntries = visibleEntries.map { PieEntry(it.value.toFloat(), it.label) }

        if (pieEntries.isEmpty()) {
            binding.percentChartView.clear()
            return
        }

        val dataSet = PieDataSet(pieEntries, "").apply {
            colors = visibleEntries.map { it.color }
            sliceSpace = 0f
            selectionShift = 0f
            valueTextColor = color(R.color.text_primary)
            valueTextSize = 18f
            valueTypeface = Typeface.DEFAULT_BOLD
            valueFormatter = object : ValueFormatter() {
                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                    val label = pieEntry?.label.orEmpty()
                    return "${value.toInt()}%\n${label.lowercase()}"
                }
            }
        }

        binding.percentChartView.data = PieData(dataSet)
        binding.percentChartView.animateY(500, Easing.EaseInOutQuad)
        binding.percentChartView.invalidate()
    }

    private fun renderCountChart(entries: List<AnalyticsChartEntry>) {
        val barEntries = entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        if (barEntries.isEmpty()) {
            binding.countChartView.clear()
            return
        }

        binding.countChartView.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String =
                entries.getOrNull(value.toInt())?.label.orEmpty()
        }

        val dataSet = BarDataSet(barEntries, "").apply {
            colors = entries.map { it.color }
            setDrawValues(true)
            valueTextColor = color(R.color.text_primary)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = "${value.toInt()}"
            }
        }

        binding.countChartView.data = BarData(dataSet).apply {
            barWidth = 0.32f
        }
        binding.countChartView.axisLeft.axisMinimum = 0f
        binding.countChartView.axisLeft.axisMaximum =
            (entries.maxOf { it.value } * 1.28f).coerceAtLeast(3f)
        binding.countChartView.xAxis.labelCount = entries.size
        binding.countChartView.animateY(450, Easing.EaseInOutQuad)
        binding.countChartView.invalidate()
    }

    private fun selectChartMode(showPercent: Boolean) {
        showPercentChart = showPercent
        binding.percentModeButton.isSelected = showPercent
        binding.countModeButton.isSelected = !showPercent
        binding.percentChartView.isVisible = showPercent
        binding.countChartView.isVisible = !showPercent

        binding.percentModeButton.setTextColor(
            color(if (showPercent) R.color.background_primary else R.color.button_primary)
        )
        binding.countModeButton.setTextColor(
            color(if (showPercent) R.color.button_primary else R.color.background_primary)
        )
    }

    private fun AnalyticsMetric.toChartEntry(): AnalyticsChartEntry =
        AnalyticsChartEntry(
            label = label,
            value = value,
            color = when (key) {
                "spoiled" -> Color.rgb(255, 78, 49)
                "eaten" -> Color.rgb(121, 213, 35)
                "saved" -> Color.rgb(219, 174, 222)
                "thrownAway" -> Color.rgb(155, 170, 239)
                else -> Color.rgb(223, 255, 78)
            },
        )

    private fun String.toIconRes(): Int =
        when (this) {
            "milk" -> R.drawable.ic_milk
            "cheese" -> R.drawable.ic_cheese
            "grains" -> R.drawable.ic_grains
            else -> R.drawable.ic_hot
        }

    private fun Calendar.displayName(): String =
        DateFormatSymbols(Locale("ru")).months[get(Calendar.MONTH)]

    private fun Calendar.toServerMonth(): String =
        String.format(Locale.US, "%04d-%02d", get(Calendar.YEAR), get(Calendar.MONTH) + 1)

    private fun color(resId: Int): Int = requireContext().getColor(resId)

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
