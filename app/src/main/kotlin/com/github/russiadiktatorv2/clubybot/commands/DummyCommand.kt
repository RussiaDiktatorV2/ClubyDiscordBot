package com.github.russiadiktatorv2.clubybot.commands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.CommandManager
import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.event.message.MessageCreateEvent

class DummyCommand : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        val commandSize = CommandManager().normalCommands.size.plus(CommandManager().moderationCommand.size).plus(CommandManager().ticketCommands.size).plus(CommandManager().welcomeCommands.size)
        MessageBuilder().append("Hey there are some informations about me").appendNewLine().appendNewLine()
            .append("🎨 | Prefix for this server: ${CacheManager.prefixMap.getOrDefault(event.server.get().id, "!")}").appendNewLine().appendNewLine()
            .append("👔 | My current command count: $commandSize").appendNewLine().appendNewLine()
            .append("👨‍💻 | I was written in Kotlin with the Javacord Library").appendNewLine().appendNewLine()
            .append("Some important informations about me").appendNewLine().appendNewLine()
            .append("🕵️‍♀️ | My current gateway ping to discord is ${event.api.latestGatewayLatency.toMillis()}ms").appendNewLine().appendNewLine()
            .append("🕵️‍♂️ | My current rest ping is ${event.api.measureRestLatency().join().toMillis()}ms").appendNewLine().appendNewLine()
            .append("🧔 | My current Ram Usage: ${(Runtime.getRuntime().totalMemory().minus(Runtime.getRuntime().freeMemory())).div(1024 * 1024)}%")
            .send(event.channel).thenAccept { it.addReactions("👮‍♂️", "👼", "👨‍🦰", "👲", "🕵️‍♀") }
    }
}