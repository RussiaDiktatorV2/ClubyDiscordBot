package com.github.russiadiktatorv2.clubybot.management.interfaces

import org.javacord.api.event.message.MessageCreateEvent

interface ModerationCommand  {

    fun executeModerationCommands(command: String, event: MessageCreateEvent, arguments: List<String>)
}