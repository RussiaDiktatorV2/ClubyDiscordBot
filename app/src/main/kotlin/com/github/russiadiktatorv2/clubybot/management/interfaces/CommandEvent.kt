package com.github.russiadiktatorv2.clubybot.management.interfaces

import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

interface CommandEvent {

    fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>)

}