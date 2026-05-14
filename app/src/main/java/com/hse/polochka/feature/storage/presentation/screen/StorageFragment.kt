package com.hse.polochka.feature.storage.presentation.screen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import java.util.Locale
import kotlinx.coroutines.launch

class StorageFragment : Fragment(R.layout.activity_storage) {

    private var _binding: ActivityStorageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var storageAdapter: StorageAdapter
    private lateinit var storageRepository: StorageRepository
    private val selectedProductIds = mutableSetOf<Int>()
    private var products = emptyList<StorageProductUi>()
    private var searchQuery = ""
    private var showOnlySoon = false
    private var isFilterOptionsVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityStorageBinding.bind(view)
        storageRepository = StorageRepositoryImpl(
            storageApi = ApiClient.create(StorageApi::class.java),
            eventStorage = StorageEventStorage(requireContext()),
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

        setupProductsList()
        setupSearch()
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
            isFilterOptionsVisible = !isFilterOptionsVisible
            updateFilterOptionsVisibility()
        }

        binding.showAllFilterButton.setOnClickListener {
            applySoonFilter(enabled = false)
        }

        binding.showSoonFilterButton.setOnClickListener {
            applySoonFilter(enabled = true)
        }

        binding.writeOffButton.setOnClickListener {
            showWriteOffProductsDialog()
        }

        binding.selectAllButton.setOnClickListener {
            toggleSelectAllProducts()
        }
        updateFilterOptionsVisibility()
        updateFilterButtonState()
    }

    private fun applySoonFilter(enabled: Boolean) {
        showOnlySoon = enabled
        isFilterOptionsVisible = false
        updateFilterOptionsVisibility()
        updateFilterButtonState()
        submitFilteredProducts()
        Toast.makeText(
            requireContext(),
            getString(if (showOnlySoon) R.string.storage_filter_soon else R.string.storage_filter_all),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                submitFilteredProducts()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.searchEditText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { storageRepository.getProducts() }
                .onSuccess { loadedProducts ->
                    products = loadedProducts
                    selectedProductIds.clear()
                    submitFilteredProducts()
                    updateBulkActionBar()
                }
                .onFailure {
                    products = emptyList()
                    selectedProductIds.clear()
                    submitFilteredProducts()
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
                submitFilteredProducts()
                Toast.makeText(requireContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Не получилось добавить продукт", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBulkActionBar() {
        val selectedCount = selectedProductIds.size
        val activeProductIds = getVisibleProducts().map { it.id }.toSet()
        val allActiveSelected = activeProductIds.isNotEmpty() && selectedProductIds.containsAll(activeProductIds)

        binding.bulkActionBar.visibility = if (selectedCount > 0) View.VISIBLE else View.GONE
        binding.selectedCountTextView.text = getString(R.string.storage_selected_count, selectedCount)
        binding.selectAllButton.text = getString(
            if (allActiveSelected) R.string.storage_clear_selection else R.string.storage_select_all
        )
    }

    private fun toggleSelectAllProducts() {
        val activeProductIds = getVisibleProducts().map { it.id }
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

        val selectedProducts = getVisibleProducts().filter { it.id in selectedProductIds }
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
                submitFilteredProducts()
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

    private fun submitFilteredProducts() {
        val visibleProducts = getVisibleProducts()
        selectedProductIds.retainAll(visibleProducts.map { it.id }.toSet())
        storageAdapter.submitItems(visibleProducts)
        updateEmptyState()
        updateBulkActionBar()
    }

    private fun updateEmptyState() {
        val hasVisibleProducts = getVisibleProducts().isNotEmpty()
        val hasFilters = searchQuery.isNotBlank() || showOnlySoon
        binding.productsRecyclerView.visibility = if (hasVisibleProducts) View.VISIBLE else View.GONE
        binding.emptyStateContainer.visibility = if (hasVisibleProducts) View.GONE else View.VISIBLE
        binding.emptyTitleTextView.text = getString(
            if (hasFilters) R.string.storage_empty_filtered_title else R.string.storage_empty_title
        )
        binding.emptySubtitleTextView.text = getString(
            if (hasFilters) R.string.storage_empty_filtered_subtitle else R.string.storage_empty_subtitle
        )
    }

    private fun getActiveProducts(): List<StorageProductUi> = products.filterNot { it.isWrittenOff }

    private fun getVisibleProducts(): List<StorageProductUi> =
        getActiveProducts().filter { product ->
            val matchesSearch = searchQuery.normalizeForSearch().let { query ->
                query.isBlank() ||
                    product.name.normalizeForSearch().contains(query) ||
                    product.tags.any { it.normalizeForSearch().contains(query) }
            }
            val matchesSoonFilter = !showOnlySoon || product.expirationAtMillis != null && product.daysLeft <= 4
            matchesSearch && matchesSoonFilter
        }

    private fun updateFilterButtonState() {
        binding.showAllFilterButton.setBackgroundResource(
            if (showOnlySoon) R.drawable.shape_button_secondary else R.drawable.shape_button_primary
        )
        binding.showAllFilterButton.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (showOnlySoon) R.color.text_primary else R.color.background_primary,
            )
        )
        binding.showSoonFilterButton.setBackgroundResource(
            if (showOnlySoon) R.drawable.shape_button_primary else R.drawable.shape_button_secondary
        )
        binding.showSoonFilterButton.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (showOnlySoon) R.color.background_primary else R.color.text_primary,
            )
        )
        if (showOnlySoon) {
            binding.filterButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.button_primary))
        } else {
            binding.filterButton.clearColorFilter()
        }
    }

    private fun updateFilterOptionsVisibility() {
        binding.filterOptionsContainer.isVisible = isFilterOptionsVisible
    }

    private fun String.normalizeForSearch(): String =
        trim()
            .lowercase(Locale.getDefault())
            .replace('ё', 'е')

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
