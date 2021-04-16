package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.extensions.createEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.tickets
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.channel.ServerTextChannelBuilder
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Permissions
import org.javacord.api.entity.permission.PermissionsBuilder
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.reaction.ReactionAddEvent
import org.javacord.api.listener.message.reaction.ReactionAddListener
import org.javacord.api.util.event.ListenerManager
import java.awt.Color

@LoadCommand
class CreateTicket : Command("createticket", CommandModule.TICKET), ReactionAddListener {

    override fun onReactionAdd(event: ReactionAddEvent) {
        event.server.ifPresent { server -> event.user.ifPresent { ticketUser ->
            if (!ticketUser.isBot) {

                val ticketChannel: TicketSystem? = ticketMap[event.serverTextChannel.get().id]
                val ticketChannelID = ticketChannel?.reactionMessageId?.let { event.serverTextChannel.get().getMessageById(it).get().id }

                if (ticketChannelID == event.messageId) {
                    event.reaction.ifPresent { reaction ->
                        if (reaction.emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":incoming_envelope:"))) {
                            reaction.removeUser(ticketUser)

                            val ticketName: String = ticketChannel.channelName.replace("%name%", ticketUser.getDisplayName(server)).replace("%name%", ticketUser.getDisplayName(server)).replace("%s%", "'")
                            val ticketBuilder: ServerTextChannelBuilder = server.createTextChannelBuilder().setName(ticketName).setCategory(event.channel.asCategorizable().get().category.orElse(null))

                            val moderationPermissions: Permissions = PermissionsBuilder().setAllowed(PermissionType.MANAGE_MESSAGES, PermissionType.MANAGE_ROLES,
                                PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY, PermissionType.SEND_MESSAGES, PermissionType.ATTACH_FILE).build()
                            val userPermissions: Permissions = PermissionsBuilder().setAllowed(PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY, PermissionType.ATTACH_FILE, PermissionType.EMBED_LINKS).build()

                            ticketChannel.roleIDs?.forEach { roleIds ->
                                ticketBuilder.addPermissionOverwrite(server.getRoleById(roleIds).get(), moderationPermissions)
                                ticketBuilder.addPermissionOverwrite(ticketUser, userPermissions)
                            }

                            ticketBuilder.create().thenAcceptAsync { ticket ->
                                MessageBuilder().append { ticketUser.mentionTag }.appendNewLine()
                                    .setEmbed(createEmbed {
                                        setTitle("🎟 | Ticket ${ticket.name}")
                                        addInlineField("❗ User Information", ticketChannel.ticketMessage)
                                        addInlineField("🔴 Status", "Open")
                                        addField("💨 Announcement", "React with the :lock: reaction to lock the ticket. React with :x: to close the ticket directly", false)
                                        setThumbnail(ticketUser.avatar)
                                        setColor(Color.decode("0x32ff7e"))
                                    })
                                    .send(ticket).thenAccept {
                                        it.addReactions(ClubyDiscordBot.convertUnicode(":lock:"), ClubyDiscordBot.convertUnicode(":x:"))
                                        ticketListener(it, 1)
                                    }
                                tickets.add(ticket.id)
                                println("+ TicketChannel: ${ticket.name}")
                            }
                        }
                    }
                }
            }
        } }
    }

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        if (!user.isBot) {
            message.delete()
            val ticketChannel: TicketSystem? = ticketMap[textChannel.id]

            if (ticketChannel != null) {
                val channelName: String =
                    ticketChannel.channelName.replace("%name%", user.getDisplayName(server)).replace("%name%", user.getDisplayName(server))
                        .replace("%s%", "'")
                val ticketBuilder: ServerTextChannelBuilder = server.createTextChannelBuilder().setName(channelName).setCategory(textChannel.category.orElse(null))

                val moderationPermissions: Permissions = PermissionsBuilder().setAllowed(PermissionType.MANAGE_MESSAGES, PermissionType.MANAGE_ROLES,
                    PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY, PermissionType.SEND_MESSAGES, PermissionType.ATTACH_FILE).build()
                val userPermissions: Permissions = PermissionsBuilder().setAllowed(PermissionType.SEND_MESSAGES, PermissionType.READ_MESSAGES, PermissionType.READ_MESSAGE_HISTORY, PermissionType.ATTACH_FILE, PermissionType.EMBED_LINKS).build()

                ticketChannel.roleIDs?.forEach { roleIDs ->
                    ticketBuilder.addPermissionOverwrite(server.getRoleById(roleIDs).get(), moderationPermissions)
                    ticketBuilder.addPermissionOverwrite(user, userPermissions)
                }
                ticketBuilder.create().thenAcceptAsync { ticket ->
                    MessageBuilder().append { user.mentionTag }.appendNewLine()
                        .setEmbed(createEmbed {
                            setTitle("🎟 | Ticket ${ticket.name}")
                            addInlineField("❗ User Information", "${ticketChannel.ticketMessage} \u00AD\n")
                            addInlineField("🔴 Status", "Open")
                            addField("💨 Announcement", "React with the :lock: reaction to lock the ticket. React with :x: to close the ticket directly", false)
                            setThumbnail(user.avatar)
                            setColor(Color.decode("0x32ff7e"))
                        })
                        .send(ticket).thenAccept {
                            it.addReactions(ClubyDiscordBot.convertUnicode(":lock:"), ClubyDiscordBot.convertUnicode(":x:"))
                            ticketListener(it, 1)
                        }
                    tickets.add(ticket.id)
                    println("+ TicketChannel: ${ticket.name}")
                }
            }
        }
    }

    private fun ticketListener(ticketMessage: Message, reactionState: Int): ListenerManager<ReactionAddListener> {
        return ticketMessage.addReactionAddListener { event ->
            event.user.ifPresent { user ->
                event.reaction.ifPresent { reaction ->
                    if (!user.isBot && event.messageId == ticketMessage.id) {

                        when (reactionState) {

                            1 -> {

                                when (reaction.emoji.asUnicodeEmoji().get()) {

                                    ClubyDiscordBot.convertUnicode(":lock:") -> {
                                        ticketMessage.edit(
                                            createEmbed {
                                                setTitle("🎟 Ticket | ${ticketMessage.serverTextChannel.get().name}")
                                                setDescription("\u00AD\n" +
                                                        "🔴 Status: **Saved** \u00AD\n" +
                                                        "\u00AD\n" +
                                                        "\u00AD" +
                                                        "💨 Announcement: React to the ✅ emoji to unlock the ticket. React to the :x: emoji to close the ticket directly")
                                                setThumbnail(user.avatar)
                                                setColor(Color.decode("0xff9f1a"))
                                            }
                                        )
                                        ticketMessage.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":lock:"))
                                        ticketMessage.addReaction(ClubyDiscordBot.convertUnicode(":white_check_mark:"))
                                        ticketListener(ticketMessage, 2)
                                    }

                                    ClubyDiscordBot.convertUnicode(":x:") -> {
                                        ticketMessage.serverTextChannel.ifPresent { ticketChannel -> ticketChannel.delete() }
                                        tickets.remove(ticketMessage.channel.id)
                                        println("- TicketChannel: ${ticketMessage.serverTextChannel.get().name}")
                                    }
                                }
                            }

                            2 -> {
                                if (reaction.emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":white_check_mark:"))) {
                                    ticketMessage.edit(
                                        createEmbed {
                                            setTitle("🎟 Ticket | ${ticketMessage.serverTextChannel.get().name}")
                                            setDescription(
                                                "\u00AD\n" +
                                                        "🔴 Status: **Open** \u00AD\n" +
                                                        "\u00AD\n" +
                                                        "\u00AD" +
                                                        "💨 Announcement: React with the :lock: reaction to lock the ticket. React with :x: to close the ticket directly"
                                            )
                                            setThumbnail(user.avatar)
                                            setColor(Color.decode("0x32ff7e"))
                                        }
                                    )
                                    ticketMessage.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":white_check_mark:"))
                                    ticketMessage.addReaction(ClubyDiscordBot.convertUnicode(":lock:"))
                                    ticketListener(ticketMessage, 1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf()
    override val description: String
        get() = ""
    override val usage: String
        get() = ""
}