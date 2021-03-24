package com.github.russiadiktatorv2.clubybot.commands.normalcommands.module

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.convertUnicode
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendModuleIsAlreadyEnabled
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendModuleWasActivateMessage
import com.github.russiadiktatorv2.clubybot.management.commands.interfaces.ICommand
import org.javacord.api.DiscordApi
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

class ActivateModule : Command("activateModule", CommandModule.DEFAULT) {
    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        message.delete()

        textChannel.sendMessage(
            EmbedBuilder().setAuthor("Select your Module", null, server.api.yourself.avatar)
                .setDescription("Now you can pick up a module you want to disable. Click on a reactions\n\u00AD")
                .addInlineField("${convertUnicode(":one:")} | Moderation Module", "With the moderation module you can give your server a better security.\n" +
                        "Activate the moderation tool if you would like to use the commands. If you want to see the commands of the moderation," + " u can execute" +
                        " `[prefix]help moderation`")
                .addField("${convertUnicode(":two:")} | Ticket Module", "With the ticket module you can create/delete or manage tickets like a pro with this bot." +
                        " Enable the ticket module if you need the commands. If you want to see the list of commands they are in the ticket module just execute ``[prefix]help ticket``", false)
                .addInlineField("${convertUnicode(":three:")} | Welcome System", "The bot can send beautiful pictures to say hello to the new Member. Enable the module if you need them. Execute `[prefix]help welcome` to see the list of the welcome commands")
                .setFooter("To get a list of commands that are in the modules use [prefix]help").setTimestampToNow().setColor(Color.decode("0xffa502"))
        ).thenAccept {
            it.addReactions(convertUnicode(":one:"), convertUnicode(":two:"), convertUnicode(":three:"))
            createModuleListener(server.api, it.id, user.id, it.server.get().id)
        }
    }

    private fun createModuleListener(discordApi: DiscordApi, messageId: Long, memberId: Long, serverId: Long) {
        discordApi.addReactionAddListener { listener ->
            if (listener.message.get().id == messageId) {
                if (listener.userId == memberId) {
                    when (listener.reaction.get().emoji.asUnicodeEmoji().get()) {

                        convertUnicode(":one:") -> {
                            if (CacheManager.moderationModule.contains(serverId)) {
                                CacheManager.moderationModule.remove(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasActivateMessage(discordApi, "${convertUnicode("\uD83D\uDC6E\u200D")} | Moderation")
                                    discordApi.threadPool.scheduler.schedule({ moduleMessage.delete() }, 20, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent{ moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyEnabled(discordApi, "${convertUnicode("\uD83D\uDC6E\u200D")} | Moderation")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }

                        convertUnicode(":two:") -> {
                            if (CacheManager.ticketModule.contains(serverId)) {
                                CacheManager.ticketModule.remove(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasActivateMessage(discordApi, "${convertUnicode("\uD83C\uDF9F")} | Ticket")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyEnabled(discordApi, "${convertUnicode("\uD83C\uDF9F")} | Ticket")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }

                        convertUnicode(":three:") -> {
                            if (CacheManager.welcomeModule.contains(serverId)) {
                                CacheManager.welcomeModule.remove(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasActivateMessage(discordApi, "${convertUnicode("\uD83D\uDC4B")} | Welcome")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent{ moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyEnabled(discordApi, "${convertUnicode("\uD83D\uDC4B")} | Welcome")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.ADMINISTRATOR)
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}