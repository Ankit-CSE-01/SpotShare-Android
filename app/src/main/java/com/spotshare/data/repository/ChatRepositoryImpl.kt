package com.spotshare.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.spotshare.data.local.dao.MessageDao
import com.spotshare.domain.model.Chat
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Message
import com.spotshare.domain.repository.ChatRepository
import com.spotshare.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun getChats(): Flow<List<Chat>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        
        val subscription = firestore.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .collection("chats")
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val chats = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val otherUserId = doc.getString("otherUserId") ?: ""
                    val otherUserName = doc.getString("otherUserName") ?: "User"
                    val otherUserProfilePic = doc.getString("otherUserProfilePic")
                    val lastMessage = doc.getString("lastMessage") ?: ""
                    val lastMessageTime = doc.getLong("lastMessageTime") ?: 0L
                    val unreadCount = doc.getLong("unreadCount")?.toInt() ?: 0
                    
                    Chat(id, otherUserId, otherUserName, otherUserProfilePic, lastMessage, lastMessageTime, unreadCount)
                } ?: emptyList()
                
                trySend(chats)
            }
            
        awaitClose { subscription.remove() }
    }

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    Message(
                        id = doc.id,
                        chatId = chatId,
                        senderId = doc.getString("senderId") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        text = doc.getString("text") ?: "",
                        mediaUrl = doc.getString("mediaUrl"),
                        mediaType = doc.getString("mediaType")?.let { MediaType.valueOf(it) },
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } ?: emptyList()
                
                trySend(messages)
            }
            
        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(chatId: String, text: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            val userName = auth.currentUser?.displayName ?: "User"
            val messageId = UUID.randomUUID().toString()
            
            val messageData = hashMapOf(
                "id" to messageId,
                "chatId" to chatId,
                "senderId" to userId,
                "senderName" to userName,
                "text" to text,
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false
            )
            
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(messageData)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChat(chatId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
