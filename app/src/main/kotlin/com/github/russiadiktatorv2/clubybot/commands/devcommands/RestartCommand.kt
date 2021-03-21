package com.github.russiadiktatorv2.clubybot.commands.devcommands

import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.event.message.MessageCreateEvent

class RestartCommand : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {

        event.deleteMessage()
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.ADMINISTRATOR)) {

        }
    }
}