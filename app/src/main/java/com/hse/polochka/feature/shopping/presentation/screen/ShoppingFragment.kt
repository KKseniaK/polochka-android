package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityShoppingBinding
import com.hse.polochka.feature.shopping.presentation.adapter.FamilyShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingHistoryAdapter
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingListUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class ShoppingFragment : Fragment(R.layout.activity_shopping) {

    private var _binding: ActivityShoppingBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityShoppingBinding.bind(view)

        setupMainShoppingList()
        setupFamilyLists()
        setupHistory()
        setupClicks()
    }

    private fun setupMainShoppingList() {
        binding.shoppingListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.shoppingListRecyclerView.adapter =
            ShoppingListAdapter(
                mutableListOf(
                    ShoppingItemUi(1, "Яблоки"),
                    ShoppingItemUi(2, "Макароны"),
                    ShoppingItemUi(3, "Яйца 10 шт.", true)
                )
            )
    }

    private fun setupFamilyLists() {
        val familyLists = getMockFamilyLists()

        binding.familyListsRecyclerView.visibility =
            if (familyLists.isEmpty()) View.GONE else View.VISIBLE

        binding.noFamilyListsTextView.visibility =
            if (familyLists.isEmpty()) View.VISIBLE else View.GONE

        binding.familyListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.familyListsRecyclerView.adapter =
            FamilyShoppingListAdapter(familyLists) { list ->
                FamilyShoppingListDialogFragment
                    .newInstance(list.ownerName)
                    .show(parentFragmentManager, "family_shopping_list")
            }
    }

    private fun setupHistory() {
        binding.historyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.historyRecyclerView.adapter =
            ShoppingHistoryAdapter(getMockHistory()) { history ->
                Toast.makeText(
                    requireContext(),
                    "Открываем чек за ${history.date}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupClicks() {
        binding.mainShoppingCard.setOnClickListener {
            ShoppingListDialogFragment()
                .show(parentFragmentManager, "shopping_list")
        }
    }

    private fun getMockFamilyLists(): List<FamilyShoppingListUi> {
        return listOf(
            FamilyShoppingListUi(
                id = 1,
                ownerName = "сестра",
                items = listOf(
                    ShoppingItemUi(1, "Манго"),
                    ShoppingItemUi(2, "Яйца 10 шт.", true),
                    ShoppingItemUi(3, "Молоко")
                )
            ),
            FamilyShoppingListUi(
                id = 2,
                ownerName = "папа",
                items = listOf(
                    ShoppingItemUi(4, "Хлеб"),
                    ShoppingItemUi(5, "Сыр")
                )
            ),
            FamilyShoppingListUi(
                id = 3,
                ownerName = "сестра2",
                items = listOf(
                    ShoppingItemUi(1, "Манго"),
                    ShoppingItemUi(2, "Яйца 10 шт.", true),
                    ShoppingItemUi(3, "Молоко")
                )
            ),
            FamilyShoppingListUi(
                id = 4,
                ownerName = "сестра3",
                items = listOf(
                    ShoppingItemUi(1, "Манго"),
                    ShoppingItemUi(2, "Яйца 10 шт.", true),
                    ShoppingItemUi(3, "Молоко")
                )
            ),
        )
    }

    private fun getMockHistory(): List<ShoppingHistoryUi> {
        return listOf(
            ShoppingHistoryUi(
                id = 1,
                date = "06.04.26",
                items = listOf("Яблоки", "Помидоры 6 шт.", "Макароны")
            ),
            ShoppingHistoryUi(
                id = 2,
                date = "02.04.26",
                items = listOf("Молоко", "Сыр", "Хлеб")
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}