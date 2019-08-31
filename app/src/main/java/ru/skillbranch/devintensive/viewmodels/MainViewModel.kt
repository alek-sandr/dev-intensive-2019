package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val chatRepository = ChatRepository
    private val query = mutableLiveData("")
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats
            .filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filter = {
            val queryString = query.value ?: ""
            val archived: List<Chat> = chatRepository.loadChats().value!!
                .filter { it.isArchived }
            val allChats = if (archived.isEmpty()) {
                chats.value!!
            } else {
                val copy = chats.value!!.toMutableList()
                copy.add(0, getArchiveItem(archived))
                copy
            }
            result.value = if (queryString.isEmpty()) {
                allChats
            } else {
                allChats.filter { it.chatType != ChatType.ARCHIVE && it.title.contains(queryString, true) }
            }
        }
        result.addSource(chats) { filter() }
        result.addSource(query) { filter() }
        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    private fun getArchiveItem(archived: List<Chat>): ChatItem {
        val totalUnread: Int = archived
            .map { it.unreadableMessageCount() }
            .reduce { acc, i -> acc + i }
        val chatWithLastMessage: Chat? = archived
            .filter { it.messages.isNotEmpty() }
            .maxBy { it.messages.last().date }

        val chatItem: ChatItem? = chatWithLastMessage?.toChatItem()
        return ChatItem(
            "0",
            null,
            "",
            "Chats Archive",
            chatItem?.shortDescription,
            totalUnread,
            chatItem?.lastMessageDate,
            false,
            ChatType.ARCHIVE,
            chatItem?.author
        )
    }

    fun handleSearchQuery(text: String) {
        query.value = text.trim()
    }
}