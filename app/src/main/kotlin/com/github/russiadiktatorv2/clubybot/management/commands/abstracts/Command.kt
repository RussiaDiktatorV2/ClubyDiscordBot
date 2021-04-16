package com.github.russiadiktatorv2.clubybot.management.commands.abstracts

import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.interfaces.ICommand
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

abstract class Command : ICommand {

    var name: String
        private set

    var module: CommandModule
        private set

    var aliases: List<String>
        private set

    constructor(name: String, module: CommandModule) {
        this.name = name
        this.module = module
        this.aliases = emptyList()
    }

    constructor(name: String, module: CommandModule, vararg aliases: String) {
        this.name = name
        this.module = module
        this.aliases = listOf(*aliases)
    }

    abstract fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>)
}