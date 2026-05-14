package com.hse.polochka.feature.onboarding.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hse.polochka.MainActivity
import com.hse.polochka.R

abstract class BaseOnboardingFragment(
    layoutId: Int
) : Fragment(layoutId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).hideBottomMenu()
    }

    protected fun setupProgress(
        step: Int,
        stepViews: List<View>
    ) {
        stepViews.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index < step) R.drawable.bg_step_active
                else R.drawable.bg_step_inactive
            )
        }
    }
}
