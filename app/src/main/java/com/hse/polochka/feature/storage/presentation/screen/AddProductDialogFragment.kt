package com.hse.polochka.feature.storage.presentation.screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddProductDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.dialog_add_product, container, false)

        val close = view.findViewById<ImageView>(R.id.closeButton)
        val plus = view.findViewById<TextView>(R.id.plusButton)
        val minus = view.findViewById<TextView>(R.id.minusButton)
        val amountText = view.findViewById<TextView>(R.id.amountText)
        val dateEditText = view.findViewById<EditText>(R.id.dateEditText)

        var amount = 1
        updateAmountUi(amount, amountText, minus)

        plus.setOnClickListener {
            amount += 1
            updateAmountUi(amount, amountText, minus)
        }

        minus.setOnClickListener {
            if (amount > 1) {
                amount -= 1
                updateAmountUi(amount, amountText, minus)
            }
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        dateEditText.isFocusable = false
        dateEditText.isClickable = true

        dateEditText.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    dateEditText.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // нельзя выбрать прошлую дату
            dialog.datePicker.minDate = System.currentTimeMillis()

            dialog.show()
        }

        close.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun updateAmountUi(
        amount: Int,
        amountText: TextView,
        minusButton: TextView
    ) {
        amountText.text = amount.toString()
        minusButton.alpha = if (amount == 1) 0.35f else 1f
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}