package com.spotshare.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
                        mediaType = doc.getString("mediaType")?.let { 
                            try { MediaType.valueOf(it) } catch(e: Exception) { null } 
                        },
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
            val timestamp = System.currentTimeMillis()
            
            val messageData = hashMapOf(
                "id" to messageId,
                "chatId" to chatId,
                "senderId" to userId,
                "senderName" to userName,
                "text" to text,
                "timestamp" to timestamp,
                "isRead" to false
            )
            
            // 1. Add message to the central messages collection
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(messageData)
                .await()
                
            // 2. Update chat summary for sender
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .collection("chats")
                .document(chatId)
                .update(
                    "lastMessage", text,
                    "lastMessageTime", timestamp
                ).await()

            // 3. Update chat summary for receiver (get otherUserId first)
            val chatDoc = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .collection("chats")
                .document(chatId)
                .get()
                .await()
            
            val otherUserId = chatDoc.getString("otherUserId")
            if (otherUserId != null) {
                firestore.collection(Constants.USERS_COLLECTION)
                    .document(otherUserId)
                    .collection("chats")
                    .document(chatId)
                    .update(
                        "lastMessage", text,
                        "lastMessageTime", timestamp,
                        "unreadCount", FieldValue.increment(1)
                    ).await()
            }
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrCreateChat(otherUserId: String): Result<String> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            
            // Deterministic chat ID for 1-to-1 chats
            val chatId = if (currentUserId < otherUserId) "${currentUserId}_$otherUserId" else "${otherUserId}_$currentUserId"
            
            val chatRef = firestore.collection(Constants.USERS_COLLECTION)
                .document(currentUserId)
                .collection("chats")
                .document(chatId)
            
            val chatSnapshot = chatRef.get().await()
            
            if (!chatSnapshot.exists()) {
                // Fetch other user's info to create summaries
                val otherUserDoc = firestore.collection(Constants.USERS_COLLECTION).document(otherUserId).get().await()
                val otherUserName = otherUserDoc.getString("userName") ?: "User"
                val otherUserProfilePic = otherUserDoc.getString("profilePicUrl")
                
                val currentUserDoc = firestore.collection(Constants.USERS_COLLECTION).document(currentUserId).get().await()
                val currentUserName = currentUserDoc.getString("userName") ?: "User"
                val currentUserProfilePic = currentUserDoc.getString("profilePicUrl")

                val batch = firestore.batch()
                
                // Summary for current user
                batch.set(chatRef, mapOf(
                    "otherUserId" to otherUserId,
                    "otherUserName" to otherUserName,
                    "otherUserProfilePic" to otherUserProfilePic,
                    "lastMessage" to "",
                    "lastMessageTime" to System.currentTimeMillis(),
                    "unreadCount" to 0
                ))
                
                // Summary for other user
                val otherChatRef = firestore.collection(Constants.USERS_COLLECTION)
                    .document(otherUserId)
                    .collection("chats")
                    .document(chatId)
                
                batch.set(otherChatRef, mapOf(
                    "otherUserId" to currentUserId,
                    "otherUserName" to currentUserName,
                    "otherUserProfilePic" to currentUserProfilePic,
                    "lastMessage" to "",
                    "lastMessageTime" to System.currentTimeMillis(),
                    "unreadCount" to 0
                ))
                
                batch.commit().await()
            }
            
            Result.success(chatId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChat(chatId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
