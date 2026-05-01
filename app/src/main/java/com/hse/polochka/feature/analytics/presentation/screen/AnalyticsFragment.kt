package com.hse.polochka.feature.analytics.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityAnalyticsBinding
import com.hse.polochka.databinding.ItemAnalyticsInsightBinding

class AnalyticsFragment : Fragment(R.layout.activity_analytics) {

    private var _binding: ActivityAnalyticsBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityAnalyticsBinding.bind(view)

        setMockData()
        setupInsights()
        setupChartModeButtons()
    }

    private fun setMockData() {
        binding.monthTextView.text =
            getString(R.string.analytics_month, "апрель")

        binding.boughtValueTextView.text =
            getString(R.string.analytics_bought_value_default)

        binding.savedMoneyValueTextView.text =
            getString(R.string.analytics_saved_money_default)
    }

    private fun setupInsights() {
        val insights = binding.analyticsInsights

        setupInsight(
            insights.topLeftInsight,
            title = "Молоко",
            description = "чаще всего\nпокупали",
            icon = R.drawable.ic_milk
        )

        setupInsight(
            insights.topRightInsight,
            title = "Острое",
            description = "ваша любимая\nкатегория",
            icon = R.drawable.ic_hot
        )

        setupInsight(
            insights.bottomLeftInsight,
            title = "Сыры",
            description = "любимая\nкатегория семьи",
            icon = R.drawable.ic_cheese
        )

        setupInsight(
            insights.bottomRightInsight,
            title = "Молоко",
            description = "чаще всего\nпортилось",
            icon = R.drawable.ic_milk
        )

        // центр
        insights.championNameTextView.text = "Папа"
        insights.championDescriptionTextView.text =
            getString(R.string.analytics_champion_description)
    }

    private fun setupInsight(
        binding: ItemAnalyticsInsightBinding,
        title: String,
        description: String,
        icon: Int
    ) {
        binding.insightTitleTextView.text = title
        binding.insightDescriptionTextView.text = description
        binding.insightIconImageView.setImageResource(icon)
    }

    private fun setupChartModeButtons() {
        binding.percentModeButton.isSelected = true
        binding.countModeButton.isSelected = false

        binding.percentModeButton.setOnClickListener {
            selectChartMode(showPercent = true)
        }

        binding.countModeButton.setOnClickListener {
            selectChartMode(showPercent = false)
        }
    }

    private fun selectChartMode(showPercent: Boolean) {
        binding.percentModeButton.isSelected = showPercent
        binding.countModeButton.isSelected = !showPercent

        binding.percentModeButton.setTextColor(
            requireContext().getColor(
                if (showPercent) R.color.background_primary else R.color.button_primary
            )
        )

        binding.countModeButton.setTextColor(
            requireContext().getColor(
                if (showPercent) R.color.button_primary else R.color.background_primary
            )
        )

        // TODO: потом здесь будем переключать подписи графика
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}