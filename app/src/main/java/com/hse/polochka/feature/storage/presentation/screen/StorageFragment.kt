package com.hse.polochka.feature.storage.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityStorageBinding
import com.hse.polochka.feature.storage.presentation.adapter.StorageAdapter
import com.hse.polochka.feature.storage.presentation.model.ProductStorageStatus
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi

class StorageFragment : Fragment(R.layout.activity_storage) {

    private var _binding: ActivityStorageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var storageAdapter: StorageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityStorageBinding.bind(view)

        setupProductsList()
        setupClickListeners()
    }



    private fun setupProductsList() {
        storageAdapter = StorageAdapter(getMockProducts()) { product ->
            ProductDetailsDialogFragment.newInstance(product.id)
                .show(parentFragmentManager, "product_details")
        }

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = storageAdapter
    }

    private fun setupClickListeners() {

        binding.addProductButton.setOnClickListener {
            AddProductDialogFragment()
                .show(parentFragmentManager, "add_product")
        }

        binding.filterButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.storage_filter_soon),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getMockProducts(): List<StorageProductUi> {
        return listOf(
            StorageProductUi(
                id = 1,
                name = "Йогурт Epica",
                amount = "5 шт.",
                daysLeftText = "осталось: 1дн",
                tags = listOf("молочка", "быстро портящийся"),
                imageResId = R.drawable.ic_milk,
                status = ProductStorageStatus.LAST_DAY
            ),
            StorageProductUi(
                id = 2,
                name = "Молоко Parmalat",
                amount = "1 шт.",
                daysLeftText = "осталось: 4дн",
                tags = listOf("молочка"),
                imageResId = R.drawable.ic_milk,
                status = ProductStorageStatus.MIDDLE
            ),
            StorageProductUi(
                id = 3,
                name = "Сыр",
                amount = "2 шт.",
                daysLeftText = "осталось: 10дн",
                tags = listOf("молочка", "сыр"),
                imageResId = R.drawable.ic_cheese,
                status = ProductStorageStatus.FRESH
            ),
            StorageProductUi(
                id = 4,
                name = "Молоко Parmalat",
                amount = "1 шт.",
                daysLeftText = "осталось: 4дн",
                tags = listOf("молочка"),
                imageResId = R.drawable.ic_milk,
                status = ProductStorageStatus.MIDDLE
            ),
            StorageProductUi(
                id = 5,
                name = "Сыр",
                amount = "2 шт.",
                daysLeftText = "осталось: 10дн",
                tags = listOf("молочка", "сыр"),
                imageResId = R.drawable.ic_cheese,
                status = ProductStorageStatus.FRESH
            ),

        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}