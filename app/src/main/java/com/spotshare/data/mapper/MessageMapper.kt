package com.spotshare.data.mapper

import com.spotshare.data.local.entity.MessageEntity
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Message

fun MessageEntity.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        text = text,
        mediaUrl = mediaUrl,
        mediaType = mediaType?.let { MediaType.valueOf(it) },
        timestamp = timestamp,
        isRead = isRead
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        senderId = senderId,
        senderName = senderName,
        text = text,
        mediaUrl = mediaUrl,
        mediaType = mediaType?.name,
        timestamp = timestamp,
        isRead = isRead
    )
}
