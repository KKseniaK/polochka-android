package com.hse.polochka.feature.storage.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.databinding.ActivityStorageBinding
import com.hse.polochka.feature.storage.data.remote.StorageApi
import com.hse.polochka.feature.storage.data.repository.StorageRepositoryImpl
import com.hse.polochka.feature.storage.domain.repository.StorageRepository
import com.hse.polochka.feature.storage.presentation.adapter.StorageAdapter
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi
import com.hse.polochka.feature.storage.presentation.model.WriteOffResult
import kotlinx.coroutines.launch

class StorageFragment : Fragment(R.layout.activity_storage) {

    private var _binding: ActivityStorageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var storageAdapter: StorageAdapter
    private lateinit var storageRepository: StorageRepository
    private val selectedProductIds = mutableSetOf<Int>()
    private var products = emptyList<StorageProductUi>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityStorageBinding.bind(view)
        storageRepository = StorageRepositoryImpl(
            storageApi = ApiClient.create(StorageApi::class.java),
            eventStorage = StorageEventStorage(requireContext()),
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

        setupProductsList()
        setupClickListeners()
        updateBulkActionBar()
        loadProducts()
    }

    private fun setupProductsList() {
        storageAdapter = StorageAdapter(
            items = products,
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
            AddProductDialogFragment().apply {
                searchCatalog = storageRepository::searchCatalog
                onProductCreated = ::addProduct
            }.show(parentFragmentManager, "add_product")
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

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { storageRepository.getProducts() }
                .onSuccess { loadedProducts ->
                    products = loadedProducts
                    selectedProductIds.clear()
                    submitActiveProducts()
                    updateBulkActionBar()
                }
                .onFailure {
                    products = emptyList()
                    selectedProductIds.clear()
                    submitActiveProducts()
                    updateBulkActionBar()
                    Toast.makeText(requireContext(), "Не получилось загрузить хранилище", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addProduct(
        name: String,
        amount: String,
        tagIds: List<String>,
        expirationAtMillis: Long?,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                storageRepository.addProduct(
                    name = name,
                    amount = amount,
                    tagIds = tagIds,
                    expirationAtMillis = expirationAtMillis,
                )
            }.onSuccess { createdProduct ->
                products = products + createdProduct
                submitActiveProducts()
                Toast.makeText(requireContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Не получилось добавить продукт", Toast.LENGTH_SHORT).show()
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            val failed = results.count { result ->
                runCatching {
                    storageRepository.writeOffProduct(result.productId, result.reason)
                }.isFailure
            }

            if (failed == 0) {
                val idsToWriteOff = results.map { it.productId }.toSet()
                products = products.map { product ->
                    if (product.id in idsToWriteOff) {
                        product.copy(isWrittenOff = true)
                    } else {
                        product
                    }
                }
                selectedProductIds.clear()
                submitActiveProducts()
                updateBulkActionBar()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.storage_written_off, idsToWriteOff.size),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(), "Не получилось списать часть продуктов", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
        }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
