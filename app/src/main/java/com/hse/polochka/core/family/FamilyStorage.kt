package com.hse.polochka.core.family

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FamilyStorage(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getFamilyName(): String = preferences.getString(KEY_FAMILY_NAME, DEFAULT_FAMILY_NAME) ?: DEFAULT_FAMILY_NAME

    fun getMembers(): List<FamilyMember> {
        val rawMembers = preferences.getString(KEY_MEMBERS, null)
        val members = if (rawMembers == null) {
            emptyList()
        } else {
            runCatching {
                val type = object : TypeToken<List<FamilyMember>>() {}.type
                gson.fromJson<List<FamilyMember>>(rawMembers, type)
            }.getOrDefault(emptyList())
        }

        return members.ifEmpty {
            val defaultMember = createDefaultOwner()
            saveMembers(listOf(defaultMember))
            listOf(defaultMember)
        }
    }

    fun updateCurrentUser(name: String, email: String) {
        val members = getMembers().map { member ->
            if (member.isCurrentUser) {
                member.copy(name = name.ifBlank { DEFAULT_USER_NAME }, email = email.ifBlank { DEFAULT_USER_EMAIL })
            } else {
                member
            }
        }
        saveMembers(members)
    }

    fun addInvitedMember(email: String): FamilyMember {
        val cleanEmail = email.trim()
        val existing = getMembers().firstOrNull { it.email.equals(cleanEmail, ignoreCase = true) }
        if (existing != null) return existing

        val member = FamilyMember(
            id = "invite-${cleanEmail.lowercase()}",
            name = cleanEmail.substringBefore("@").ifBlank { "Новый участник" },
            email = cleanEmail,
            role = ROLE_MEMBER,
            status = STATUS_INVITED,
            isCurrentUser = false,
        )
        saveMembers(getMembers() + member)
        return member
    }

    fun removeMember(memberId: String): Boolean {
        val members = getMembers()
        val member = members.firstOrNull { it.id == memberId } ?: return false
        if (member.isCurrentUser) return false

        saveMembers(members.filterNot { it.id == memberId })
        return true
    }

    fun createInviteLink(email: String): String =
        "https://polochka.local/invite?family=local-family&email=${email.trim()}"

    private fun saveMembers(members: List<FamilyMember>) {
        preferences.edit()
            .putString(KEY_MEMBERS, gson.toJson(members))
            .apply()
    }

    private fun createDefaultOwner(): FamilyMember =
        FamilyMember(
            id = DEFAULT_USER_ID,
            name = DEFAULT_USER_NAME,
            email = DEFAULT_USER_EMAIL,
            role = ROLE_OWNER,
            status = STATUS_ACTIVE,
            isCurrentUser = true,
        )

    companion object {
        private const val PREFERENCES_NAME = "family_storage"
        private const val KEY_MEMBERS = "members"
        private const val KEY_FAMILY_NAME = "family_name"
        private const val DEFAULT_FAMILY_NAME = "Семья"
        private const val DEFAULT_USER_ID = "local-test-user"
        private const val DEFAULT_USER_NAME = "Test User"
        private const val DEFAULT_USER_EMAIL = "test@polochka.local"
        private const val ROLE_OWNER = "owner"
        private const val ROLE_MEMBER = "member"
        private const val STATUS_ACTIVE = "active"
        private const val STATUS_INVITED = "invited"
    }
}
