package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit
import kotlin.time.measureTime

class IdCommand : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        event.deleteMessage()
        if (event.message.mentionedChannels.isNotEmpty()) {
            val textChannelIDsBuilder = StringBuilder("Following textchannels has a valid id ↓").append("\n\n")
            event.message.mentionedChannels.map { serverTextChannel -> serverTextChannel.mentionTag.plus(" -> ${serverTextChannel.id}") }.forEach { message -> textChannelIDsBuilder.append("$message\n") }

            sendEmbed(event.channel, 1, TimeUnit.MINUTES) {
                setDescription(textChannelIDsBuilder.toString())
                setColor(Color.decode("0x32ff7e"))
            }
        } else if (event.message.mentionedRoles.isNotEmpty()) {
            val roleIDsBuilder = StringBuilder("Following roles has a valid id ↓").append("\n\n")
            event.message.mentionedRoles.map { roles -> roles.mentionTag.plus(" -> ${roles.id}") }.forEach { message -> roleIDsBuilder.append("$message\n") }

            sendEmbed(event.channel, 1, TimeUnit.MINUTES) {
                setDescription(roleIDsBuilder.toString())
                setColor(Color.decode("0x32ff7e"))
            }
        }
    }
}