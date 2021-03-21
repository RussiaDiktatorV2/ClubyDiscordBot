package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import com.github.russiadiktatorv2.clubybot.management.interfaces.TicketCommand
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.PermissionsBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class CreateTicket : TicketCommand {

    override fun executeTicketCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.isPresent && event.messageAuthor.asUser().isPresent && event.serverTextChannel.isPresent) {
            if (!event.messageAuthor.asUser().get().isBot) {
                if (!CacheManager.ticketsMap.containsKey(event.messageAuthor.asUser().get().id)) {
                    val ticketChannel: TicketSystem? = CacheManager.ticketMap[event.message.serverTextChannel.get().id]
                    val channelName: String? = ticketChannel?.channelName?.replace("%name%", event.messageAuthor.asUser().get().getDisplayName(event.server.get()))?.replace("%name%", event.messageAuthor.asUser().get().getDisplayName(event.server.get()))?.replace("%s%", "'")
                    val ticketBuilder = event.server.get().createTextChannelBuilder().setName(channelName).setAuditLogReason("User wants something")
                    if (ticketChannel != null) {
                        for (IDs in ticketChannel.roleIDs!!) {
                            ticketBuilder.addPermissionOverwrite(event.server.get().getRoleById(IDs).get(), PermissionsBuilder().setAllowed(PermissionType.MANAGE_MESSAGES, PermissionType.MANAGE_ROLES, PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY, PermissionType.SEND_MESSAGES, PermissionType.ATTACH_FILE).build())
                        }
                        ticketBuilder.create().thenAcceptAsync { ticket ->
                            ticket.sendMessage(createEmbed {
                                setTitle("🎟 Your ticket has been created")
                                setDescription(
                                    "${ticketChannel.ticketMessage}\n\n" +
                                            "Status: **Open**\n" +
                                            "Created at: **${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}**")
                                setThumbnail(event.messageAuthor.asUser().get().avatar)
                                setColor(Color.decode("0x32ff7e"))
                            }).thenAccept {
                                ticketListener(it, 1)
                                it.addReactions(ClubyDiscordBot.convertUnicode("Warning:820606568370012170"), ClubyDiscordBot.convertUnicode("\uD83D\uDD12"))
                            }
                            CacheManager.ticketsMap[event.messageAuthor.asUser().get().id] = ticket.id
                        }
                    }
                }
            }
        }
    }

    private fun ticketListener(message: Message, reactionState: Int) {
        message.addReactionAddListener { event ->
            if (event.server.isPresent && event.user.isPresent) {
                if (event.user.isPresent && event.message.isPresent && event.reaction.isPresent && event.reaction.get().emoji.asUnicodeEmoji().isPresent) {
                    if (!(event.user.get().isBot) && event.message.get().id == message.id) {
                        when (reactionState) {

                            1 -> {
                                when (event.reaction.get().emoji.asUnicodeEmoji().get()) {

                                    ClubyDiscordBot.convertUnicode("Warning:820606568370012170") -> {
                                        message.edit(createEmbed {
                                            setTitle("🎟 Ticket ${message.serverTextChannel.get().name}")
                                            setDescription("Status: **Saved**\n" +
                                                    "Created at: **${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}**\n\n" +
                                                    "React to the ✅ emoji to unlog the ticket. React to the ❗ emoji to close the ticket")
                                            setColor(Color.decode("0x32ff7e"))
                                        })
                                    }

                                    ClubyDiscordBot.convertUnicode("\uD83D\uDD12") -> {
                                        message.edit(createEmbed {
                                            setAuthor("Of Course")
                                        }).thenRun {
                                            message.api.threadPool.scheduler.schedule({ message.serverTextChannel.ifPresent { channel -> channel.delete() } }, 10, TimeUnit.SECONDS)
                                            ticketListener(message, 2)
                                        }
                                    }
                                }
                            }

                            2 -> {
                                if (event.reaction.get().emoji.asUnicodeEmoji().get() == ClubyDiscordBot.convertUnicode(":x:")) {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
