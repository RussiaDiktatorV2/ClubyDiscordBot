package com.github.russiadiktatorv2.clubybot.commands.welcomescommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.interfaces.WelcomeCommand
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.event.message.MessageCreateEvent

class RemoveWelcomeSystem : WelcomeCommand {

    override fun executeWelcomeCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            val serverID: Long = event.server.get().id
            welcomeMap.remove(serverID)
            event.channel.sendMessage("Hello")
        }
    }
}