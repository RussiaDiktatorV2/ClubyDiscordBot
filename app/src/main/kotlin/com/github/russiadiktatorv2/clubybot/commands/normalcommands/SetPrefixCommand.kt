package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendPrefixIsSame
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendPrefixWasChanged
import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

class SetPrefixCommand : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        event.deleteMessage()
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            if (arguments.size == 2) { var newPrefix = arguments[1]
                newPrefix = newPrefix.replace("\n", "")
                prefixSetup(newPrefix, event.server.get().id, event.serverTextChannel.get())
            } else {
                sendEmbed(event.serverTextChannel.get(), 20, TimeUnit.SECONDS) {
                    setAuthor("» Error to set a prefix")
                    setDescription("Use `${prefixMap.getOrDefault(event.server.get().id, "!")}setprefix newprefix` to change the prefix on your server").setFooter("❗ | The prefix system").setTimestampToNow()
                    setColor(Color.decode("0xf2310f"))
                }
            }
        } else {
            sendEmbed(event.serverTextChannel.get(), 10, TimeUnit.SECONDS) {
                setAuthor("» Error to set a prefix")
                setDescription("You don't have the ``${PermissionType.ADMINISTRATOR}`` permissions to change the prefix").setFooter("❗ | The prefix system").setTimestampToNow()
                setColor(Color.decode("0xf2310f"))
            }
        }
    }

    private fun prefixSetup(prefix: String, serverID: Long, textChannel: ServerTextChannel) {
        val currentPrefix = prefixMap.getOrDefault(serverID, "!")
        if ((prefix == "!").not()) {
            if ((prefix == currentPrefix).not()) {
                if (prefix.length <= 7) {
                    prefixMap[serverID] = prefix
                    textChannel.sendPrefixWasChanged(textChannel.api, currentPrefix, prefix)
                }
            } else {
                textChannel.sendPrefixIsSame(textChannel.api)
            }
        } else {
            if (prefixMap.containsKey(serverID)) {
                prefixMap.remove(serverID)
                textChannel.sendPrefixWasChanged(textChannel.api, currentPrefix, "!")
            }
        }
    }
}