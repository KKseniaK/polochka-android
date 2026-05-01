package com.hse.polochka.feature.storage.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R

class ProductDetailsDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_product_details, container, false)

        view.findViewById<ImageView>(R.id.closeButton).setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val margin = (24 * resources.displayMetrics.density).toInt()

            setLayout(
                resources.displayMetrics.widthPixels - margin * 2,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: Int): ProductDetailsDialogFragment {
            return ProductDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PRODUCT_ID, productId)
                }
            }
        }
    }
}