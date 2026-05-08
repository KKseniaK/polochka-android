package com.hse.polochka.core.storage_events

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageEventStorage(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addEvents(events: List<StorageEvent>) {
        val updatedEvents = getEvents() + events
        preferences.edit()
            .putString(KEY_EVENTS, gson.toJson(updatedEvents))
            .apply()
    }

    fun getEvents(): List<StorageEvent> {
        val rawEvents = preferences.getString(KEY_EVENTS, null) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<StorageEvent>>() {}.type
            gson.fromJson<List<StorageEvent>>(rawEvents, type)
        }.getOrDefault(emptyList())
    }

    companion object {
        private const val PREFERENCES_NAME = "storage_events"
        private const val KEY_EVENTS = "events"
    }
}
