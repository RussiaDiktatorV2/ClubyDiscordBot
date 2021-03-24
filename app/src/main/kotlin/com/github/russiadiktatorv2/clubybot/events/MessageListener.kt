package com.github.russiadiktatorv2.clubybot.events

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.CommandManager
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

class MessageListener : MessageCreateListener {
    override fun onMessageCreate(event: MessageCreateEvent) {
        if (event.server.isPresent && event.isServerMessage) {
            if (event.messageAuthor.asUser().isPresent && event.messageAuthor.asUser().get().isBot.not()) {
                val customPrefix = CacheManager.prefixMap.getOrDefault(event.server.get().id, "!")
                if (event.messageContent.startsWith(customPrefix)) {

                    CommandManager.executeCommand(event.server.get(), event.messageAuthor.asUser().get(), event.serverTextChannel.get(), event.message)
                }
            }
        }
    }
}