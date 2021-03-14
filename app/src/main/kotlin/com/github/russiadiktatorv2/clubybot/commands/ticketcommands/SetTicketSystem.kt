package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketMap
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.interfaces.TicketCommand
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.listener.message.reaction.ReactionAddListener
import org.javacord.api.util.event.ListenerManager
import java.awt.Color
import java.util.concurrent.TimeUnit

class SetTicketSystem : TicketCommand {

    override fun executeTicketCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            event.deleteMessage()
            if (arguments.size == 2) { val ticketChannelID: Long = arguments[1].toLong()
                if (event.server.flatMap { server -> server.getTextChannelById(ticketChannelID) }.isPresent) {
                    if (!ticketMap.containsKey(ticketChannelID)) {
                        val ticketSystem = TicketSystem(ticketChannelID, null, null, null)

                        val ticketSetupEmbed = createEmbed {
                            setAuthor("🎟 | Step 2 of the Setup", null, event.api.yourself.avatar)
                            setDescription("You can chose between two emojis.\n:ok: for a custom welcome message :x: to don't set a welcome message")
                            setFooter("👋 | The Welcomer System")
                        }
                        ticketMap[ticketChannelID] = ticketSystem
                        event.serverTextChannel.ifPresent { channel -> channel.sendMessage("`${event.server.get().getTextChannelById(ticketChannelID).get().name}`") }

                    } else {
                        sendEmbed(event.serverTextChannel.get(), 20, TimeUnit.SECONDS) {
                            setAuthor("🎟 | Problem with the Setup")
                            setDescription("The channel ``${event.server.get().getTextChannelById(ticketChannelID).get().name}}`` is already a ticket channel").setFooter("🎟 | Delete or update a ticketchannel").setTimestampToNow()
                            setColor(Color.decode("0xf2310f"))
                        }
                    }
                }
            }
        }
    }

    private val messagemap = mutableMapOf<Long, ListenerManager<MessageCreateListener>>()

    private val reactionmap = mutableMapOf<Long, ListenerManager<ReactionAddListener>>()

    private fun createTicketListener(message: Message, state: Int, memberID: Long, ticketChannel: TicketSystem) {

    }
}