package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.PermissionsBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@LoadCommand
class CreateTicket : Command("createTicket", CommandModule.TICKET) {
    override fun executeCommand(
        server: Server,
        user: User,
        textChannel: ServerTextChannel,
        message: Message,
        args: Array<out String>
    ) {
        if (!user.isBot) {
            if (!CacheManager.ticketsMap.containsKey(user.id)) {
                val ticketChannel: TicketSystem? = CacheManager.ticketMap[textChannel.id]
                val channelName: String? = ticketChannel?.channelName?.replace("%name%", user.getDisplayName(server))
                    ?.replace("%name%", user.getDisplayName(server))?.replace("%s%", "'")
                val ticketBuilder =
                    server.createTextChannelBuilder().setName(channelName).setAuditLogReason("User wants something")
                if (ticketChannel != null) {
                    for (IDs in ticketChannel.roleIDs!!) {
                        ticketBuilder.addPermissionOverwrite(
                            server.getRoleById(IDs).get(),
                            PermissionsBuilder().setAllowed(
                                PermissionType.MANAGE_MESSAGES,
                                PermissionType.MANAGE_ROLES,
                                PermissionType.READ_MESSAGES,
                                PermissionType.READ_MESSAGE_HISTORY,
                                PermissionType.SEND_MESSAGES,
                                PermissionType.ATTACH_FILE
                            ).build()
                        )
                    }
                    ticketBuilder.create().thenAcceptAsync { ticket ->
                        ticket.sendMessage(createEmbed {
                            setTitle("🎟 Your ticket has been created")
                            setDescription(
                                "${ticketChannel.ticketMessage}\n\n" +
                                        "Status: **Open**\n" +
                                        "Created at: **${
                                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }**"
                            )
                            setThumbnail(user.avatar)
                            setColor(Color.decode("0x32ff7e"))
                        }).thenAccept {
                            ticketListener(it, 1)
                            it.addReactions(
                                ClubyDiscordBot.convertUnicode("Warning:820606568370012170"),
                                ClubyDiscordBot.convertUnicode("\uD83D\uDD12")
                            )
                        }
                        CacheManager.ticketsMap[user.id] = ticket.id
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
                                            setDescription(
                                                "Status: **Saved**\n" +
                                                        "Created at: **${
                                                            LocalDateTime.now()
                                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                        }**\n\n" +
                                                        "React to the ✅ emoji to unlock the ticket. React to the ❗ emoji to close the ticket"
                                            )
                                            setColor(Color.decode("0x32ff7e"))
                                        })
                                    }

                                    ClubyDiscordBot.convertUnicode("\uD83D\uDD12") -> {
                                        message.edit(createEmbed {
                                            setAuthor("Of Course")
                                        }).thenRun {
                                            message.api.threadPool.scheduler.schedule(
                                                { message.serverTextChannel.ifPresent { channel -> channel.delete() } },
                                                10,
                                                TimeUnit.SECONDS
                                            )
                                            ticketListener(message, 2)
                                        }
                                    }
                                }
                            }

                            2 -> {
                                if (event.reaction.get().emoji.asUnicodeEmoji().get() == ClubyDiscordBot.convertUnicode(
                                        ":x:"
                                    )
                                ) {

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
