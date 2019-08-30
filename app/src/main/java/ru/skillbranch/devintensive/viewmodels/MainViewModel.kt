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
    private val archive = mutableLiveData<ChatItem>(null)
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
            val allChats = if (archive.value == null) {
                chats.value!!
            } else {
                val copy = chats.value!!.toMutableList()
                copy.add(0, archive.value!!)
                copy
            }
            result.value = if (queryString.isEmpty()) {
                allChats
            } else {
                allChats.filter { it.title.contains(queryString, true) }
            }
        }

        result.addSource(archive) {
            if (archive.value != null) {
                val allChats = chats.value!!.toMutableList()
                allChats.add(0, archive.value!!)
                result.value = allChats
            } else {
                result.value = chats.value
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
        val archived: List<Chat> = chatRepository.loadChats().value!!
            .filter { it.isArchived }
        updateArchiveItem(archived)
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))

        val archived = chatRepository.loadChats().value!!
            .filter { it.isArchived }

        if (archived.isEmpty()) { // remove archives item
            archive.value = null
        } else { // update existing
            updateArchiveItem(archived)
        }
    }

    private fun updateArchiveItem(archived: List<Chat>) {
        val totalUnread: Int = archived
            .map { it.unreadableMessageCount() }
            .reduce { acc, i -> acc + i }
        val chatWithLastMessage: Chat? = archived
            .filter { it.messages.isNotEmpty() }
            .maxBy { it.messages.last().date }

        val chatItem: ChatItem? = chatWithLastMessage?.toChatItem()
        archive.value = ChatItem(
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