package com.hse.polochka.feature.storage.presentation.screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hse.polochka.R
import com.hse.polochka.feature.storage.data.dto.CatalogSuggestionDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddProductDialogFragment : DialogFragment() {

    var onProductCreated: ((name: String, amount: String, tagIds: List<String>, expirationAtMillis: Long?) -> Unit)? = null
    var searchCatalog: (suspend (String) -> List<CatalogSuggestionDto>)? = null

    private var searchJob: Job? = null
    private var suggestions = emptyList<CatalogSuggestionDto>()
    private var selectedTagIds = emptyList<String>()
    private var selectedExpirationAtMillis: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.dialog_add_product, container, false)

        val close = view.findViewById<ImageView>(R.id.closeButton)
        val nameEditText = view.findViewById<AutoCompleteTextView>(R.id.nameEditText)
        val categoryEditText = view.findViewById<EditText>(R.id.categoryEditText)
        val plus = view.findViewById<TextView>(R.id.plusButton)
        val minus = view.findViewById<TextView>(R.id.minusButton)
        val amountText = view.findViewById<TextView>(R.id.amountText)
        val dateEditText = view.findViewById<EditText>(R.id.dateEditText)
        val saveButton = view.findViewById<TextView>(R.id.saveButton)

        nameEditText.requestFocus()
        var amount = 1
        updateAmountUi(amount, amountText, minus)
        setupCatalogSearch(nameEditText, categoryEditText)

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
                    selectedExpirationAtMillis = calendar.timeInMillis
                    dateEditText.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.minDate = System.currentTimeMillis()
            dialog.show()
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(requireContext(), "Введите название продукта", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val manualTag = categoryEditText.text.toString().trim()
            val tagIds = selectedTagIds.ifEmpty {
                listOf(manualTag).filter { it.isNotBlank() }
            }
            onProductCreated?.invoke(
                name,
                "$amount шт.",
                tagIds,
                selectedExpirationAtMillis,
            )
            dismiss()
        }

        close.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun setupCatalogSearch(
        nameEditText: AutoCompleteTextView,
        categoryEditText: EditText,
    ) {
        nameEditText.threshold = 2
        nameEditText.setOnItemClickListener { _, _, position, _ ->
            val suggestion = suggestions.getOrNull(position) ?: return@setOnItemClickListener
            selectedTagIds = suggestion.tagIds
            nameEditText.setText(suggestion.name, false)
            categoryEditText.setText(suggestion.tagIds.firstOrNull().orEmpty())
        }
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty().trim()
                selectedTagIds = emptyList()
                searchJob?.cancel()
                if (query.length < 2) return

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(250)
                    val result = runCatching { searchCatalog?.invoke(query).orEmpty() }.getOrDefault(emptyList())
                    suggestions = result
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        result.map { it.name }
                    )
                    nameEditText.setAdapter(adapter)
                    if (result.isNotEmpty()) {
                        nameEditText.showDropDown()
                    }
                }
            }
        })
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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        view?.findViewById<AutoCompleteTextView>(R.id.nameEditText)?.let { nameEditText ->
            nameEditText.post {
                val inputManager = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        super.onDestroyView()
    }
}
