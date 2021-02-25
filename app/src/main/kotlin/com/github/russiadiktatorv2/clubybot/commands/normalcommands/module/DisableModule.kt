package com.github.russiadiktatorv2.clubybot.commands.normalcommands.module

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.convertUnicode
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendModuleIsAlreadyDisabled
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendModuleWasDisabledMessage
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

class DisableModule : CommandEvent {

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.ADMINISTRATOR)) {
            event.deleteMessage()
            event.serverTextChannel.ifPresent { textChannel ->
                textChannel.sendMessage(
                    EmbedBuilder().setAuthor("Select your Module", null, event.api.yourself.avatar)
                        .setDescription("Now you can pick up a Module u want to disable. Chose on of the reactions\n\u00AD")
                        .addInlineField("${convertUnicode(":one:")} | Moderation Module", "With the moderation module you can give your server a better security.\nDisable the moderation tool if you don't need the commands. If you want to see the commands of the moderation," +
                                " u can execute `[prefix]help moderation`")
                        .addField("${convertUnicode(":two:")} | Ticket Module", "With the ticket module you can create/delete or manage tickets like a pro with this bot. Disable the ticket module if you don't need them. If you want to see the list of commands" +
                                " they are in the ticket module just execute ``[prefix]help ticket``", false)
                        .addInlineField("${convertUnicode(":three:")} | Welcome System", "The bot can send beautiful pictures to say hello to the new Member. \nDisable the module if you need them. Enable the module if you need them. Execute `[prefix]help welcome` to see the list" +
                                " of the welcome commands")
                        .setFooter("To get a list of commands that are in the modules use [prefix]help").setTimestampToNow().setColor(Color.decode("0xffa502"))
                ).thenAccept {
                    it.addReactions(convertUnicode(":one:"), convertUnicode(":two:"), convertUnicode(":three:"))
                    createModuleListener(event.api, it.id, event.messageAuthor.asUser().get().id, it.server.get().id)
                }
            }
        }
    }
    private fun createModuleListener(discordApi: DiscordApi, messageId: Long, memberId: Long, serverId: Long) {
        discordApi.addReactionAddListener { listener ->
            if (listener.message.get().id == messageId) {
                if (listener.userId == memberId) {

                    when (listener.reaction.get().emoji.asUnicodeEmoji().get()) {

                        convertUnicode(":one:") -> {
                            if (CacheManager.moderationModule.contains(serverId).not()) {
                                CacheManager.moderationModule.add(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasDisabledMessage(discordApi, "${convertUnicode("\uD83D\uDC6E\u200D")} | Moderation")
                                    discordApi.threadPool.scheduler.schedule({ moduleMessage.delete() }, 20, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent{ moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyDisabled(discordApi, "${convertUnicode("\uD83D\uDC6E\u200D")} | Moderation")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }

                        convertUnicode(":two:") -> {
                            if (CacheManager.ticketModule.contains(serverId).not()) {
                                CacheManager.ticketModule.add(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasDisabledMessage(discordApi, "${convertUnicode("\uD83C\uDF9F")} | Ticket")
                                    discordApi.threadPool.scheduler.schedule({ moduleMessage.delete() }, 20, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyDisabled(discordApi, "${convertUnicode("\uD83C\uDF9F")} | Ticket")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }

                        convertUnicode(":three:") -> {
                            if (CacheManager.welcomeModule.contains(serverId).not()) {
                                CacheManager.welcomeModule.add(serverId)
                                listener.message.ifPresent { moduleMessage ->
                                    moduleMessage.sendModuleWasDisabledMessage(discordApi, "${convertUnicode("\uD83D\uDC4B")} | Welcome")
                                    discordApi.threadPool.scheduler.schedule({ moduleMessage.delete() }, 20, TimeUnit.SECONDS)
                                }
                            } else {
                                listener.message.ifPresent{ moduleMessage ->
                                    moduleMessage.sendModuleIsAlreadyDisabled(discordApi, "${convertUnicode("\uD83D\uDC4B")} | Welcome")
                                    discordApi.threadPool.scheduler.schedule( {moduleMessage.delete()}, 10, TimeUnit.SECONDS)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}