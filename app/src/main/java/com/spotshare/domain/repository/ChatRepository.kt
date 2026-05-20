package com.spotshare.domain.repository

import com.spotshare.domain.model.Chat
import com.spotshare.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, text: String): Result<Unit>
    suspend fun getOrCreateChat(otherUserId: String): Result<String>
    suspend fun deleteChat(chatId: String): Result<Unit>
}
