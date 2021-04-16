package com.github.russiadiktatorv2.clubybot.commands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.CommandManager
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

@LoadCommand
class DummyCommand : Command("dummy", CommandModule.DEFAULT) {

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        val commandSize = CommandManager.commands.size
        MessageBuilder().append("Hey there are some informations about me").appendNewLine().appendNewLine()
            .append("🎨 | Prefix for this server: ${CacheManager.prefixMap.getOrDefault(server.id, "!")}").appendNewLine().appendNewLine()
            .append("👔 | My current command count: $commandSize").appendNewLine().appendNewLine()
            .append("👨‍💻 | I was written in Kotlin with the Javacord Library").appendNewLine().appendNewLine()
            .append("Some important informations about me").appendNewLine().appendNewLine()
            .append("🕵️‍♀️ | My current gateway ping to discord is ${server.api.latestGatewayLatency.toMillis()}ms").appendNewLine().appendNewLine()
            .append("🕵️‍♂️ | My current rest ping is ${server.api.measureRestLatency().join().toMillis()}ms").appendNewLine().appendNewLine()
            .append("🧔 | My current Ram Usage: ${(Runtime.getRuntime().totalMemory().minus(Runtime.getRuntime().freeMemory())).div(1024 * 1024)}%")
            .send(textChannel).thenAccept { it.addReactions("👮‍♂️", "👼", "👨‍🦰", "👲", "🕵️‍♀") }

        
    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.ADMINISTRATOR)
    override val description: String
        get() = ""
    override val usage: String
        get() = TODO("Not yet implemented")
}