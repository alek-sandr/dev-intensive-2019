package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

/**
 * Created by Makweb on 24.06.2019.
 */
abstract class BaseMessage(
    val id: String,
    val from: User,
    val chat: Chat,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = false
) {

    abstract fun formatMessage(): String

    companion object AbstractFactory {
        var lastId = -1

        fun makeMessage(
            from: User,
            chat: Chat,
            date: Date = Date(),
            type: String = "text",
            payload: Any?,
            isIncoming: Boolean = false,
            isReaded: Boolean = true
        ): BaseMessage {
            lastId++
            return when (type) {
                "image" -> ImageMessage(
                    lastId.toString(),
                    from,
                    chat,
                    isIncoming,
                    date,
                    isReaded,
                    payload as String
                )
                else -> TextMessage(
                    lastId.toString(),
                    from,
                    chat,
                    isIncoming,
                    date,
                    isReaded,
                    payload as String
                )
            }
        }
    }
}