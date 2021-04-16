package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

@LoadCommand
class NewsCommand : Command("news", CommandModule.DEFAULT) {

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {

    }

    override val permissions: MutableList<PermissionType>
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}