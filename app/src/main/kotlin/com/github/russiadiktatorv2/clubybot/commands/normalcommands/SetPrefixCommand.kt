package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendPrefixIsSame
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendPrefixWasChanged
import com.github.russiadiktatorv2.clubybot.management.commands.interfaces.ICommand
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

@LoadCommand
class SetPrefixCommand : Command("prefix", CommandModule.DEFAULT) {
    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        message.delete()
        if (args.size == 2) {
            var newPrefix = args[1]
            newPrefix = newPrefix.replace("\n", "")
            prefixSetup(newPrefix, server.id, textChannel)

        } else {
            sendEmbed(textChannel, 20, TimeUnit.SECONDS) {
                setAuthor("» Error to set a prefix")
                setDescription("Use `${prefixMap.getOrDefault(server.id, "!")}prefix [newprefix]` to change the prefix on your server").setFooter("❗ | The prefix system").setTimestampToNow()
                setColor(Color.decode("0xf2310f"))
            }
        }
    }

    private fun prefixSetup(prefix: String, serverID: Long, textChannel: ServerTextChannel) {
        val currentPrefix = prefixMap.getOrDefault(serverID, "!")
        if (prefix != "!") {
            if (prefix != currentPrefix) {
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

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.ADMINISTRATOR, PermissionType.MANAGE_SERVER)
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}