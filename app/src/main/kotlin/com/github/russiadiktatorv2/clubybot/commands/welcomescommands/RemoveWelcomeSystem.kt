package com.github.russiadiktatorv2.clubybot.commands.welcomescommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendMissingArguments
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import java.awt.Color
import java.util.concurrent.TimeUnit

@LoadCommand
class RemoveWelcomeSystem : Command("removeWelcome", CommandModule.WELCOME) {
    override fun executeCommand(
        server: Server,
        user: User,
        textChannel: ServerTextChannel,
        message: Message,
        args: Array<out String>
    ) {
        message.delete()

        if (args.size == 1) {
            val serverID = server.id
            if (welcomeMap.containsKey(serverID)) {
                welcomeMap.remove(serverID)

                sendEmbed(textChannel, 30, TimeUnit.SECONDS) {
                    setAuthor("👋 | Welcomer System")
                    setDescription("You deleted the welcomechannel of **${server.name}**!").setFooter("👋 | The Welcomer System").setTimestampToNow()
                    setColor(Color.decode("0x32ff7e"))
                }
            } else {
                sendEmbed(textChannel, 20, TimeUnit.SECONDS) {
                    setAuthor("👋 | Welcomer System")
                    setDescription("Your server don't have a welcomechannel").setFooter("👋 | Maybe you try to set a welcomechannel?").setTimestampToNow()
                    setColor(Color.decode("0xf2310f"))
                }
            }
        } else {
            textChannel.sendMissingArguments("removewelcome", "Welcome", server)
        }

    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}