package com.hse.polochka.feature.storage.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.storage_events.StorageEvent
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.databinding.ActivityStorageBinding
import com.hse.polochka.feature.storage.presentation.adapter.StorageAdapter
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi
import com.hse.polochka.feature.storage.presentation.model.WriteOffResult

class StorageFragment : Fragment(R.layout.activity_storage) {

    private var _binding: ActivityStorageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var storageAdapter: StorageAdapter
    private lateinit var eventStorage: StorageEventStorage
    private val selectedProductIds = mutableSetOf<Int>()
    private var products = emptyList<StorageProductUi>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityStorageBinding.bind(view)
        eventStorage = StorageEventStorage(requireContext())
        products = getMockProducts()

        setupProductsList()
        setupClickListeners()
        updateBulkActionBar()
    }

    private fun setupProductsList() {
        storageAdapter = StorageAdapter(
            items = products.filterNot { it.isWrittenOff },
            selectedIds = selectedProductIds,
            onSelectionChanged = {
                updateBulkActionBar()
            },
            onProductClick = { product ->
                ProductDetailsDialogFragment.newInstance(product.id)
                    .show(parentFragmentManager, "product_details")
            }
        )

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = storageAdapter
        updateEmptyState()
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

        binding.writeOffButton.setOnClickListener {
            showWriteOffProductsDialog()
        }

        binding.selectAllButton.setOnClickListener {
            toggleSelectAllProducts()
        }
    }

    private fun updateBulkActionBar() {
        val selectedCount = selectedProductIds.size
        val activeProductIds = getActiveProducts().map { it.id }.toSet()
        val allActiveSelected = activeProductIds.isNotEmpty() && selectedProductIds.containsAll(activeProductIds)

        binding.bulkActionBar.visibility = if (selectedCount > 0) View.VISIBLE else View.GONE
        binding.selectedCountTextView.text = getString(R.string.storage_selected_count, selectedCount)
        binding.selectAllButton.text = getString(
            if (allActiveSelected) R.string.storage_clear_selection else R.string.storage_select_all
        )
    }

    private fun toggleSelectAllProducts() {
        val activeProductIds = getActiveProducts().map { it.id }
        if (activeProductIds.isEmpty()) return

        val allActiveSelected = selectedProductIds.containsAll(activeProductIds)
        selectedProductIds.clear()
        if (!allActiveSelected) {
            selectedProductIds.addAll(activeProductIds)
        }
        storageAdapter.refreshSelection()
        updateBulkActionBar()
    }

    private fun showWriteOffProductsDialog() {
        if (selectedProductIds.isEmpty()) return

        val selectedProducts = getActiveProducts().filter { it.id in selectedProductIds }
        if (selectedProducts.isEmpty()) return

        WriteOffProductsDialogFragment().apply {
            products = selectedProducts
            onCompleted = ::writeOffSelectedProducts
        }.show(parentFragmentManager, "write_off_products")
    }

    private fun writeOffSelectedProducts(results: List<WriteOffResult>) {
        if (results.isEmpty()) return

        val now = System.currentTimeMillis()
        val idsToWriteOff = results.map { it.productId }.toSet()
        products = products.map { product ->
            if (product.id in idsToWriteOff) {
                product.copy(isWrittenOff = true)
            } else {
                product
            }
        }
        eventStorage.addEvents(
            results.map { result ->
                val product = products.firstOrNull { it.id == result.productId }
                StorageEvent(
                    productId = result.productId,
                    eventType = getString(R.string.storage_event_used),
                    happenedAtMillis = now,
                    reason = result.reason,
                    productName = product?.name.orEmpty(),
                    category = product?.tags?.firstOrNull().orEmpty(),
                    quantity = 1,
                    estimatedPriceRub = 120,
                )
            }
        )

        selectedProductIds.clear()
        submitActiveProducts()
        updateBulkActionBar()
        Toast.makeText(
            requireContext(),
            getString(R.string.storage_written_off, idsToWriteOff.size),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun submitActiveProducts() {
        storageAdapter.submitItems(getActiveProducts())
        updateEmptyState()
    }

    private fun updateEmptyState() {
        val hasActiveProducts = getActiveProducts().isNotEmpty()
        binding.productsRecyclerView.visibility = if (hasActiveProducts) View.VISIBLE else View.GONE
        binding.emptyStateContainer.visibility = if (hasActiveProducts) View.GONE else View.VISIBLE
    }

    private fun getActiveProducts(): List<StorageProductUi> = products.filterNot { it.isWrittenOff }

    private fun getMockProducts(): List<StorageProductUi> {
        val today = System.currentTimeMillis()
        return listOf(
            StorageProductUi(
                id = 1,
                name = "Йогурт Epica",
                amount = "5 шт.",
                tags = listOf("молочка", "быстро портящийся"),
                imageResId = R.drawable.ic_milk,
                addedAtMillis = today - days(4),
                expirationAtMillis = today + days(1),
            ),
            StorageProductUi(
                id = 2,
                name = "Молоко Parmalat",
                amount = "1 шт.",
                tags = listOf("молочка", "быстро портящийся"),
                imageResId = R.drawable.ic_milk,
                addedAtMillis = today - days(2),
                expirationAtMillis = today + days(4),
            ),
            StorageProductUi(
                id = 3,
                name = "Сыр",
                amount = "2 шт.",
                tags = listOf("молочка", "сыр"),
                imageResId = R.drawable.ic_cheese,
                addedAtMillis = today - days(3),
                expirationAtMillis = today + days(10),
            ),
            StorageProductUi(
                id = 4,
                name = "Рис",
                amount = "1 уп.",
                tags = listOf("крупы", "долгое хранение"),
                imageResId = R.drawable.ic_grains,
                addedAtMillis = today - days(30),
                expirationAtMillis = null,
            ),
            StorageProductUi(
                id = 5,
                name = "Йогурт Epica",
                amount = "1 шт.",
                tags = listOf("молочка", "быстро портящийся"),
                imageResId = R.drawable.ic_milk,
                addedAtMillis = today - days(8),
                expirationAtMillis = today - days(1),
            ),
        )
    }

    private fun days(value: Long): Long = value * 86_400_000L

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
