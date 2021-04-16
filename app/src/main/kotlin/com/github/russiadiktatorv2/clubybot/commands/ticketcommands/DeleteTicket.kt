package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.tickets
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeUnit

@LoadCommand
class DeleteTicket : Command("delete", CommandModule.TICKET, "close") {

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        if (!user.isBot) {
            if (tickets.contains(textChannel.id)) {

                val file = File("Ticket_${textChannel.id}.txt")
                val fileWriter = FileWriter(file)

                val messages = textChannel.messageCache.capacity
                textChannel.getMessages(messages).get().deleteAll()

                textChannel.getMessagesAfterAsStream(0).forEach { it.delete() ; fileWriter.write("\n User [${it.userAuthor.get().name}] : ${it.content}") }
                fileWriter.flush()
                fileWriter.close()

                MessageBuilder().append("Here is the ticket log about ${textChannel.name}")
                    .addAttachment(file).send(textChannel)

                server.api.threadPool.scheduler.schedule( {file.delete()} , 2, TimeUnit.SECONDS)

                tickets.remove(textChannel.id)
                server.api.threadPool.scheduler.schedule( {textChannel.delete()} , 10, TimeUnit.SECONDS)
                println("- TicketChannel: ${textChannel.name}")
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