package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16) : String {
    return if (this.trimEnd().length > length) {
        this.take(length).trimEnd() + "..."
    } else {
        this.trimEnd()
    }
}

fun String.stripHtml() : String {
    return this.replace("<.*?>".toRegex(), "")
        .replace("&.*?;".toRegex(), "")
        .replace("[<>]".toRegex(), "")
        .replace("\\s+".toRegex()," ")
}