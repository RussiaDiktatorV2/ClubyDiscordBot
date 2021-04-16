package com.github.russiadiktatorv2.clubybot.events.commandevent

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.tickets
import org.javacord.api.event.channel.server.ServerChannelDeleteEvent
import org.javacord.api.listener.channel.server.ServerChannelDeleteListener

class TicketChannelDeleteListener : ServerChannelDeleteListener {

    override fun onServerChannelDelete(event: ServerChannelDeleteEvent) {

        event.channel.asServerTextChannel().ifPresent { ticketChannel ->
            if (tickets.contains(ticketChannel.id)) {

                tickets.remove(ticketChannel.id)
                println("- TicketChannel: ${ticketChannel.name}")
            }
        }
    }
}