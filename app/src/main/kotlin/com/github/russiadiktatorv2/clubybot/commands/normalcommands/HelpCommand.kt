package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.interfaces.ICommand
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

@LoadCommand
class HelpCommand : Command("help", CommandModule.DEFAULT) {

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        message.delete()
        val prefix = prefixMap.getOrDefault(server.id, "!")

        textChannel.sendMessage(
            EmbedBuilder()
                .setAuthor("🔧 | The Cluby Commandlist", "", server.api.yourself.avatar)
                .setDescription(
                    "This is the Cluby bot.\n The bot is currently in the alpha phase, which is why bugs can occur.\n"
                            + " Please report every bug you find.\n" + "If you have trouble, questions, feedback and more just join the support" +
                            " [Server](https://discord.gg/8b6z2GTKT9)\n\n"
                            + "Here is a list of commands the bot have.\n" + "\u00AD\n"
                )
                .addInlineField(
                    "👋 | Welcomer System",
                    "Send a beautiful welcome Message with a image of the Member. Say hello to the new Member with this Feature!\n" +
                            "Usage: `${prefix}setwelcome`"
                )
                .addInlineField(
                    "🎟 | Ticket Manager", "If a user wouldn't like to clarify his problem publicly" +
                            " then he can create a ticket that only the server moderation can see. " + "Usage: `${prefix}setticket`"
                )
                .addField(
                    "❗ | Custom Prefix",
                    "If you don't like the boring `!` prefix,\n you can chose your own server prefix.\n" +
                            "Usage: `${prefix}prefix`"
                )
                .setColor(Color.decode("0xfff200"))
        ).thenAccept {
            server.api.threadPool.scheduler.schedule({ it.delete() }, 30, TimeUnit.SECONDS)
        }
    }

    override val permissions: MutableList<PermissionType>
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}