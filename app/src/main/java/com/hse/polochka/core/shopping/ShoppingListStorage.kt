package com.hse.polochka.core.shopping

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShoppingListStorage(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getItems(): List<StoredShoppingItem> {
        val rawItems = preferences.getString(KEY_ITEMS, null) ?: return defaultItems()
        return runCatching {
            val type = object : TypeToken<List<StoredShoppingItem>>() {}.type
            gson.fromJson<List<StoredShoppingItem>>(rawItems, type)
        }.getOrDefault(defaultItems())
    }

    fun saveItems(items: List<StoredShoppingItem>) {
        preferences.edit()
            .putString(KEY_ITEMS, gson.toJson(items))
            .apply()
    }

    fun addItems(titles: List<String>) {
        val cleanTitles = titles.map { it.trim() }.filter { it.isNotBlank() }
        if (cleanTitles.isEmpty()) return

        val currentItems = getItems()
        val existingTitles = currentItems.map { it.title.lowercase() }.toSet()
        val newItems = cleanTitles
            .filter { it.lowercase() !in existingTitles }
            .mapIndexed { index, title ->
                StoredShoppingItem(
                    id = nextId(currentItems) + index,
                    title = title,
                    isChecked = false,
                )
            }

        if (newItems.isNotEmpty()) {
            saveItems(currentItems + newItems)
        }
    }

    private fun nextId(items: List<StoredShoppingItem>): Int =
        (items.maxOfOrNull { it.id } ?: 0) + 1

    private fun defaultItems(): List<StoredShoppingItem> =
        listOf(
            StoredShoppingItem(1, "Яблоки"),
            StoredShoppingItem(2, "Макароны"),
            StoredShoppingItem(3, "Яйца 10 шт.", isChecked = true),
        )

    private companion object {
        const val PREFERENCES_NAME = "shopping_list"
        const val KEY_ITEMS = "items"
    }
}

data class StoredShoppingItem(
    val id: Int,
    val title: String,
    val isChecked: Boolean = false,
)
