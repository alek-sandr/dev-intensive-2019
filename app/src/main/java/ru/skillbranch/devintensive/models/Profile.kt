package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils

data class Profile(
    val firstName: String,
    val lastName: String,
    val about: String,
    val repository: String,
    val rating: Int = 0,
    val respect: Int = 0
) {
    val nickName: String
        get() {
            return if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                Utils.transliteration("$firstName $lastName", "_")
            } else if (firstName.isNotEmpty()) {
                Utils.transliteration(firstName, "_")
            } else if (lastName.isNotEmpty()) {
                Utils.transliteration(lastName, "_")
            } else {
                ""
            }
        }
    val rank: String = "Junior Android Developer"

    fun toMap(): Map<String, Any> = mapOf(
        "nickName" to nickName,
        "rank" to rank,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository,
        "rating" to rating,
        "respect" to respect)
}