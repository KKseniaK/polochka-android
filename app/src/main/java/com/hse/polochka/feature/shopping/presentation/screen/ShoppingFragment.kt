package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.storage_events.StorageEvent
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.databinding.ActivityShoppingBinding
import com.hse.polochka.feature.shopping.presentation.adapter.FamilyShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingHistoryAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingActionState
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingListUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class ShoppingFragment : Fragment(R.layout.activity_shopping) {

    private var _binding: ActivityShoppingBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val ownShoppingItems = mutableListOf(
        ShoppingItemUi(1, "Яблоки"),
        ShoppingItemUi(2, "Макароны"),
        ShoppingItemUi(3, "Яйца 10 шт.", true),
    )
    private val familyLists = getMockFamilyLists().toMutableList()
    private lateinit var previewAdapter: ShoppingListAdapter
    private lateinit var eventStorage: StorageEventStorage
    private val shoppingPreferences by lazy {
        requireContext().getSharedPreferences("shopping_preferences", android.content.Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityShoppingBinding.bind(view)
        eventStorage = StorageEventStorage(requireContext())

        setupMainShoppingList()
        setupFamilyLists()
        setupHistory()
        setupClicks()
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

    private fun refreshOwnShoppingPreview() {
        previewAdapter.submitItems(ownShoppingItems.take(PREVIEW_ITEMS_LIMIT))
    }

    private fun setupFamilyLists() {
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
                            markFamilyItemBought(list.id, item, isChecked)
                        }
                    }
                    .show(parentFragmentManager, "family_shopping_list")
            }
    }

    private fun setupHistory() {
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter =
            ShoppingHistoryAdapter(getMockHistory()) { history ->
                ShoppingHistoryDialogFragment
                    .newInstance(history)
                    .show(parentFragmentManager, "shopping_history")
            }
    }

    private fun setupClicks() {
        binding.mainShoppingCard.setOnClickListener {
            ShoppingListDialogFragment.newInstance(
                title = getString(R.string.shopping_list_title),
                items = ownShoppingItems,
                mode = ShoppingListDialogFragment.Mode.OWN,
            ).apply {
                onItemsChanged = ::updateOwnShoppingItems
                onCheckChanged = ::updateOwnShoppingItemChecked
                onDeleteItem = ::deleteOwnShoppingItem
                onMoveBoughtToStorage = ::moveBoughtItemsToStorage
                askBeforeDelete = shoppingPreferences.getBoolean(KEY_ASK_BEFORE_DELETE, true)
                onRememberDeleteChoiceChanged = { shouldAsk ->
                    shoppingPreferences.edit().putBoolean(KEY_ASK_BEFORE_DELETE, shouldAsk).apply()
                }
            }.show(parentFragmentManager, "shopping_list")
        }
    }

    private fun moveBoughtItemsToStorage(items: List<ShoppingItemUi>) {
        if (items.isEmpty()) return

        eventStorage.addEvents(
            items.map { item ->
                StorageEvent(
                    productId = item.id,
                    eventType = EVENT_BOUGHT,
                    happenedAtMillis = System.currentTimeMillis(),
                    reason = "shopping",
                    productName = item.title,
                    category = categoryForTitle(item.title),
                    quantity = 1,
                    estimatedPriceRub = priceForTitle(item.title),
                )
            }
        )

        Toast.makeText(
            requireContext(),
            getString(R.string.shopping_moved_to_storage, items.size),
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun categoryForTitle(title: String): String {
        val normalized = title.lowercase()
        return when {
            "мол" in normalized || "сыр" in normalized || "йогурт" in normalized -> "Молочка"
            "яй" in normalized || "хлеб" in normalized || "макарон" in normalized -> "База"
            "яблок" in normalized || "помидор" in normalized || "манго" in normalized -> "Овощи/фрукты"
            else -> "Другое"
        }
    }

    private fun priceForTitle(title: String): Int {
        val normalized = title.lowercase()
        return when {
            "сыр" in normalized -> 280
            "мол" in normalized -> 110
            "яй" in normalized -> 160
            "хлеб" in normalized -> 70
            "манго" in normalized -> 240
            else -> 120
        }
    }

    private fun updateOwnShoppingItems(updatedItems: List<ShoppingItemUi>) {
        ownShoppingItems.clear()
        ownShoppingItems.addAll(updatedItems)
        refreshOwnShoppingPreview()
    }

    private fun updateOwnShoppingItemChecked(item: ShoppingItemUi, isChecked: Boolean): Boolean {
        val index = ownShoppingItems.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            ownShoppingItems[index] = ownShoppingItems[index].copy(isChecked = isChecked)
            refreshOwnShoppingPreview()
        }
        return true
    }

    private fun deleteOwnShoppingItem(item: ShoppingItemUi): Boolean {
        val index = ownShoppingItems.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            ownShoppingItems.removeAt(index)
            refreshOwnShoppingPreview()
        }
        return true
    }

    private fun markFamilyItemBought(listId: Int, item: ShoppingItemUi, isChecked: Boolean): Boolean {
        if (!isChecked) return true

        return when (item.familyActionState) {
            FamilyShoppingActionState.AVAILABLE -> {
                updateFamilyItem(listId, item.copy(isChecked = true))
                true
            }
            FamilyShoppingActionState.ALREADY_DELETED_BY_OWNER -> {
                showConflictWithStorageChoice(
                    title = getString(R.string.shopping_conflict_deleted_title),
                    message = getString(R.string.shopping_conflict_deleted_message, item.title),
                    item = item,
                )
                false
            }
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_OWNER -> {
                showConflictWithStorageChoice(
                    title = getString(R.string.shopping_conflict_bought_by_owner_title),
                    message = getString(R.string.shopping_conflict_bought_by_owner_message, item.title),
                    item = item,
                )
                false
            }
            FamilyShoppingActionState.ALREADY_BOUGHT_BY_YOU -> {
                showConflict(
                    title = getString(R.string.shopping_conflict_bought_by_you_title),
                    message = getString(R.string.shopping_conflict_bought_by_you_message),
                )
                false
            }
        }
    }

    private fun showConflict(title: String, message: String) {
        ShoppingConflictDialogFragment
            .newInstance(title, message)
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

    private fun updateFamilyItem(listId: Int, updatedItem: ShoppingItemUi) {
        val listIndex = familyLists.indexOfFirst { it.id == listId }
        if (listIndex == -1) return

        val list = familyLists[listIndex]
        familyLists[listIndex] = list.copy(
            items = list.items.map { item ->
                if (item.id == updatedItem.id) updatedItem else item
            }
        )
        setupFamilyLists()
    }

    private fun getMockFamilyLists(): List<FamilyShoppingListUi> =
        listOf(
            FamilyShoppingListUi(
                id = 1,
                ownerName = "мама",
                items = listOf(
                    ShoppingItemUi(1, "Молоко"),
                    ShoppingItemUi(2, "Яйца 10 шт.", true),
                    ShoppingItemUi(3, "Манго"),
                ),
            ),
            FamilyShoppingListUi(
                id = 2,
                ownerName = "папа",
                items = listOf(
                    ShoppingItemUi(
                        id = 4,
                        title = "Хлеб",
                        familyActionState = FamilyShoppingActionState.ALREADY_DELETED_BY_OWNER,
                    ),
                    ShoppingItemUi(
                        id = 5,
                        title = "Сыр",
                        familyActionState = FamilyShoppingActionState.ALREADY_BOUGHT_BY_OWNER,
                    ),
                ),
            ),
            FamilyShoppingListUi(
                id = 3,
                ownerName = "сестра",
                items = listOf(
                    ShoppingItemUi(6, "Помидоры"),
                    ShoppingItemUi(
                        id = 7,
                        title = "Кофе",
                        familyActionState = FamilyShoppingActionState.ALREADY_BOUGHT_BY_YOU,
                    ),
                ),
            ),
        )

    private fun getMockHistory(): List<ShoppingHistoryUi> =
        listOf(
            ShoppingHistoryUi(
                id = 1,
                date = "06.04.26",
                items = listOf("Яблоки", "Помидоры 6 шт.", "Макароны"),
            ),
            ShoppingHistoryUi(
                id = 2,
                date = "02.04.26",
                items = listOf("Молоко", "Сыр", "Хлеб"),
            ),
        )

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREVIEW_ITEMS_LIMIT = 3
        const val KEY_ASK_BEFORE_DELETE = "ask_before_delete"
        const val EVENT_BOUGHT = "bought"
    }
}
