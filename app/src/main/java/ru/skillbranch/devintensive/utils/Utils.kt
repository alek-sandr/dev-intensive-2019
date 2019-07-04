package ru.skillbranch.devintensive.utils

import java.lang.StringBuilder

object Utils {
    private val symbolsMap = mapOf<Char, String> (
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya"
    )

    fun parseFullName(fullName: String?) : Pair<String?, String?> {
        val parts = if (fullName?.trim() == "") {
            null
        } else {
            fullName?.split(" ")
        }
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val builder = StringBuilder()
        payload.forEach {
            if (it == ' ') { // check divider
                if (divider != " ") {
                    builder.append(divider)
                } else {
                    builder.append(" ")
                }
            } else { //check symbol
                var c: String? = symbolsMap[it]
                if (c != null) { // found match
                    builder.append(c)
                } else {
                    c = symbolsMap[it.toLowerCase()] // maybe it in upper case
                    if (c != null) {
                        builder.append(c.capitalize())
                    } else { // no matches append as is
                        builder.append(it)
                    }
                }
            }
        }
        return builder.toString()
    }

    fun toInitials(firstName: String?, lastName: String?) : String? {
        val first = firstName?.trim()?.getOrNull(0) ?: ""
        val second = lastName?.trim()?.getOrNull(0) ?: ""
        val nick = "$first$second"
        return if (nick.isEmpty()) null else nick.toUpperCase()
    }
}