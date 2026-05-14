package com.hse.polochka.feature.shopping.presentation.screen

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
import com.hse.polochka.databinding.ActivityShoppingBinding
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.shopping.data.remote.ShoppingApi
import com.hse.polochka.feature.shopping.data.repository.ShoppingConflictException
import com.hse.polochka.feature.shopping.data.repository.ShoppingRepositoryImpl
import com.hse.polochka.feature.shopping.presentation.adapter.FamilyShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingHistoryAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingActionState
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingListUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi
import kotlinx.coroutines.launch

class ShoppingFragment : Fragment(R.layout.activity_shopping) {

    private var _binding: ActivityShoppingBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val ownShoppingItems = mutableListOf<ShoppingItemUi>()
    private val familyLists = mutableListOf<FamilyShoppingListUi>()
    private var historyItems = emptyList<ShoppingHistoryUi>()
    private var currentUserId: String = ""

    private lateinit var repository: ShoppingRepositoryImpl
    private lateinit var previewAdapter: ShoppingListAdapter
    private val shoppingPreferences by lazy {
        requireContext().getSharedPreferences("shopping_preferences", android.content.Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityShoppingBinding.bind(view)
        repository = ShoppingRepositoryImpl(
            shoppingApi = ApiClient.create(ShoppingApi::class.java),
            authApi = ApiClient.create(AuthApi::class.java),
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

        setupMainShoppingList()
        setupClicks()
        renderFamilyLists()
        renderHistory()
        loadShoppingData()
    }

    private fun setupMainShoppingList() {
        binding.shoppingListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        previewAdapter = ShoppingListAdapter(
            items = ownShoppingItems.take(PREVIEW_ITEMS_LIMIT).toMutableList(),
            showDeleteButton = false,
            isPreview = true,
        )
        binding.shoppingListRecyclerView.adapter = previewAdapter
    }

    private fun setupClicks() {
        binding.mainShoppingCard.setOnClickListener {
            ShoppingListDialogFragment.newInstance(
                title = getString(R.string.shopping_list_title),
                items = ownShoppingItems,
                mode = ShoppingListDialogFragment.Mode.OWN,
            ).apply {
                onAddItem = ::addShoppingItem
                onCheckChanged = { item, isChecked ->
                    updateShoppingItem(item, isChecked)
                    true
                }
                onDeleteItem = { item ->
                    deleteShoppingItem(item, REASON_NOT_NEEDED)
                    true
                }
                onDeleteBoughtToStorage = { item ->
                    deleteShoppingItem(item, REASON_BOUGHT_TO_STORAGE)
                }
                onMoveBoughtToStorage = ::moveBoughtItemsToStorage
                askBeforeDelete = shoppingPreferences.getBoolean(KEY_ASK_BEFORE_DELETE, true)
                onRememberDeleteChoiceChanged = { shouldAsk ->
                    shoppingPreferences.edit().putBoolean(KEY_ASK_BEFORE_DELETE, shouldAsk).apply()
                }
            }.show(parentFragmentManager, "shopping_list")
        }
    }

    private fun loadShoppingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                currentUserId = repository.currentUserId()
                val items = repository.getItems()
                historyItems = repository.getHistory()
                applyShoppingItems(items)
            }.onFailure {
                currentUserId = ""
                ownShoppingItems.clear()
                familyLists.clear()
                historyItems = emptyList()
                renderAll()
                Toast.makeText(requireContext(), "Не получилось загрузить список покупок", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyShoppingItems(items: List<ShoppingItemUi>) {
        ownShoppingItems.clear()
        ownShoppingItems.addAll(items.filter { it.createdByUserId == currentUserId })

        familyLists.clear()
        familyLists.addAll(
            items
                .filter { it.createdByUserId != currentUserId }
                .groupBy { it.createdByUserId }
                .values
                .mapIndexed { index, memberItems ->
                    val first = memberItems.first()
                    FamilyShoppingListUi(
                        id = index + 1,
                        ownerUserId = first.createdByUserId,
                        ownerName = first.createdByUserName.ifBlank { "Участник" },
                        items = memberItems,
                    )
                }
        )

        renderAll()
    }

    private fun renderAll() {
        renderOwnPreview()
        renderFamilyLists()
        renderHistory()
    }

    private fun renderOwnPreview() {
        previewAdapter.submitItems(ownShoppingItems.take(PREVIEW_ITEMS_LIMIT))
    }

    private fun renderFamilyLists() {
        binding.familyListsRecyclerView.visibility =
            if (familyLists.isEmpty()) View.GONE else View.VISIBLE
        binding.noFamilyListsTextView.visibility =
            if (familyLists.isEmpty()) View.VISIBLE else View.GONE

        binding.familyListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.familyListsRecyclerView.adapter =
            FamilyShoppingListAdapter(familyLists) { list ->
                FamilyShoppingListDialogFragment
                    .newInstance(list)
                    .apply {
                        onBuyItem = { item, isChecked ->
                            markFamilyItemBought(item, isChecked)
                        }
                    }
                    .show(parentFragmentManager, "family_shopping_list")
            }
    }

    private fun renderHistory() {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter =
            ShoppingHistoryAdapter(historyItems) { history ->
                ShoppingHistoryDialogFragment
                    .newInstance(history)
                    .show(parentFragmentManager, "shopping_history")
            }
    }

    private fun addShoppingItem(title: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { repository.addItem(title) }
                .onSuccess { loadShoppingData() }
                .onFailure {
                    Toast.makeText(requireContext(), "Не получилось добавить продукт", Toast.LENGTH_SHORT).show()
                    loadShoppingData()
                }
        }
    }

    private fun updateShoppingItem(item: ShoppingItemUi, isChecked: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { repository.updateItem(item, isChecked) }
                .onSuccess { loadShoppingData() }
                .onFailure { error ->
                    handleShoppingError(error, item)
                    loadShoppingData()
                }
        }
    }

    private fun deleteShoppingItem(item: ShoppingItemUi, reason: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { repository.deleteItem(item, reason) }
                .onSuccess { loadShoppingData() }
                .onFailure { error ->
                    handleShoppingError(error, item)
                    loadShoppingData()
                }
        }
    }

    private fun moveBoughtItemsToStorage(items: List<ShoppingItemUi>) {
        if (items.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { repository.moveToStorage(items) }
                .onSuccess {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.shopping_moved_to_storage, items.size),
                        Toast.LENGTH_SHORT,
                    ).show()
                    loadShoppingData()
                }
                .onFailure {
                    Toast.makeText(requireContext(), "Не получилось перенести в хранилище", Toast.LENGTH_SHORT).show()
                    loadShoppingData()
                }
        }
    }

    private fun markFamilyItemBought(item: ShoppingItemUi, isChecked: Boolean): Boolean {
        if (!isChecked) return true

        return when (item.familyActionState) {
            FamilyShoppingActionState.AVAILABLE -> {
                updateShoppingItem(item, true)
                true
            }
            FamilyShoppingActionState.ALREADY_DELETED_BY_OWNER -> {
                showConflictWithStorageChoice(
                    title = "Уже удалили",
                    message = deletedConflictMessage(item),
                    item = item,
                )
                false
            }
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_OWNER -> {
                showConflictWithStorageChoice(
                    title = "Уже купили",
                    message = boughtConflictMessage(item),
                    item = item,
                )
                false
            }
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_YOU -> {
                showConflict(
                    title = "Вы уже отмечали",
                    message = "Эта покупка уже была отмечена с вашей стороны.",
                )
                false
            }
        }
    }

    private fun handleShoppingError(error: Throwable, fallbackItem: ShoppingItemUi) {
        if (error is ShoppingConflictException) {
            val item = error.item ?: fallbackItem
            when (error.code) {
                "deleted_by_owner" -> showConflictWithStorageChoice(
                    title = "Уже удалили",
                    message = deletedConflictMessage(item),
                    item = item,
                )
                "bought_by_owner" -> showConflictWithStorageChoice(
                    title = "Уже купили",
                    message = boughtConflictMessage(item),
                    item = item,
                )
                "bought_by_you" -> showConflict(
                    title = "Вы уже отмечали",
                    message = "Эта покупка уже была отмечена с вашей стороны.",
                )
                else -> handleStateConflict(item)
            }
        } else {
            Toast.makeText(requireContext(), "Не получилось обновить список покупок", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleStateConflict(item: ShoppingItemUi) {
        when (item.familyActionState) {
            FamilyShoppingActionState.ALREADY_DELETED_BY_OWNER -> showConflictWithStorageChoice(
                title = "Уже удалили",
                message = deletedConflictMessage(item),
                item = item,
            )
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_OWNER -> showConflictWithStorageChoice(
                title = "Уже купили",
                message = boughtConflictMessage(item),
                item = item,
            )
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_YOU -> showConflict(
                title = "Вы уже отмечали",
                message = "Эта покупка уже была отмечена с вашей стороны.",
            )
            FamilyShoppingActionState.AVAILABLE -> showConflict(
                title = "Список обновился",
                message = "Данные списка изменились. Попробуйте действие ещё раз.",
            )
        }
    }

    private fun deletedConflictMessage(item: ShoppingItemUi): String =
        "${item.createdByUserName.ifBlank { "Участник семьи" }} уже удалил ${item.title} у себя в списке. Вы уже купили?"

    private fun boughtConflictMessage(item: ShoppingItemUi): String =
        "${item.createdByUserName.ifBlank { "Участник семьи" }} уже купил ${item.title}. Вы тоже уже купили?"

    private fun showConflict(title: String, message: String) {
        ShoppingConflictDialogFragment
            .newInstance(
                title = title,
                message = message,
                primaryText = "Понятно",
            )
            .show(parentFragmentManager, "shopping_conflict")
    }

    private fun showConflictWithStorageChoice(title: String, message: String, item: ShoppingItemUi) {
        ShoppingConflictDialogFragment
            .newInstance(
                title = title,
                message = message,
                primaryText = getString(R.string.shopping_conflict_send_to_storage),
                secondaryText = getString(R.string.shopping_conflict_do_not_buy_extra),
            )
            .apply {
                onPrimaryClick = {
                    moveBoughtItemsToStorage(listOf(item))
                }
            }
            .show(parentFragmentManager, "shopping_conflict_choice")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREVIEW_ITEMS_LIMIT = 3
        const val KEY_ASK_BEFORE_DELETE = "ask_before_delete"
        const val REASON_NOT_NEEDED = "not_needed"
        const val REASON_BOUGHT_TO_STORAGE = "bought_to_storage"
    }
}
