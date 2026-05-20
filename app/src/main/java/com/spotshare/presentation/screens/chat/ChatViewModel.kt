package com.spotshare.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.domain.model.Chat
import com.spotshare.domain.model.Message
import com.spotshare.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _currentChat = MutableStateFlow<Chat?>(null)
    val currentChat = _currentChat.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        chatRepository.getChats()
            .onEach { _chats.value = it }
            .launchIn(viewModelScope)
    }

    fun loadMessages(chatId: String) {
        chatRepository.getMessages(chatId)
            .onEach { _messages.value = it }
            .launchIn(viewModelScope)
            
        // Find chat in existing list to get metadata (name, pic)
        _chats.onEach { list ->
            list.find { it.id == chatId }?.let { _currentChat.value = it }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(chatId: String, text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, text)
        }
    }
}
