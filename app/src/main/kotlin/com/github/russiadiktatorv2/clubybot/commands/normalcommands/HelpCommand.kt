package com.github.russiadiktatorv2.clubybot.commands.normalcommands

import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

class HelpCommand : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        event.message.delete()

        event.serverTextChannel.ifPresent { textChannel ->
            textChannel.sendMessage(
                EmbedBuilder()
                    .setAuthor("🔧 | The Cluby Commandlist", "", event.api.yourself.avatar)
                    .setDescription(
                        "This is the Cluby bot.\n The bot is currently in the alpha phase, which is why bugs can occur.\n"
                                + " Please report every bug you find.\n" + "If you have trouble, questions, feedback and more just join the support" +
                                " [Server](https://discord.gg/8b6z2GTKT9)\n\n"
                                + "Here is a list of commands the bot have.\n" + "\u00AD\n"
                    )
                    .addInlineField(
                        "👋 | Welcomer System",
                        "Send a beautiful welcome Message with a image of the Member. Say hello to the new Member with this Feature!\n" +
                                "Usage: `[prefix]setupwelcome`"
                    )
                    .addInlineField(
                        "🎟 | Ticket Manager", "If a user wouldn't like to clarify his problem publicly" +
                                " then he can create a ticket that only the server moderation can see. " + "Usage: `[prefix]setupticket`")
                    .addField("❗ | Custom Prefix", "If you don't like the boring `!` prefix,\n you can chose your own server prefix.\n" +
                            "Usage: `[oldprefix]newprefix`")
                    .setColor(Color.decode("0xfff200"))
            ).thenAccept {
                event.api.threadPool.scheduler.schedule( {it.delete()}, 30, TimeUnit.SECONDS)
            }
        }
    }
}