package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
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
class IdCommand : Command("id", CommandModule.DEFAULT) {
    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        message.delete()
        if (message.mentionedChannels.isNotEmpty()) {
            val textChannelIDsBuilder = StringBuilder("Following textchannels has a valid id ↓").append("\n\n")
            message.mentionedChannels.map { roles -> roles.mentionTag.plus(" -> ${roles.id}") }.forEach { message -> textChannelIDsBuilder.append("$message\n") }

            sendEmbed(textChannel, 1, TimeUnit.MINUTES) {
                setDescription(textChannelIDsBuilder.toString())
                setColor(Color.decode("0x32ff7e"))
            }
        } else if (message.mentionedRoles.isNotEmpty()) {
            val roleIDsBuilder = StringBuilder("Following roles has a valid id ↓").append("\n\n")
            message.mentionedRoles.map { roles -> roles.mentionTag.plus(" -> ${roles.id}") }.forEach { message -> roleIDsBuilder.append("$message\n") }

            sendEmbed(textChannel, 1, TimeUnit.MINUTES) {
                setDescription(roleIDsBuilder.toString())
                setColor(Color.decode("0x32ff7e"))
            }
        }
    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.ADMINISTRATOR)
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}