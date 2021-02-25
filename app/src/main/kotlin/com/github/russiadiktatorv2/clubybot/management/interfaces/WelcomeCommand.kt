package com.github.russiadiktatorv2.clubybot.management.interfaces

import org.javacord.api.event.message.MessageCreateEvent

interface WelcomeCommand {

    fun executeWelcomeCommands(command: String, event: MessageCreateEvent, arguments: List<String>)

}