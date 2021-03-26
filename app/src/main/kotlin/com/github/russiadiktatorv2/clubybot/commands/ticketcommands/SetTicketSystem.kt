package com.github.russiadiktatorv2.clubybot.commands.ticketcommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketMap
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.extensions.createEmbed
import com.github.russiadiktatorv2.clubybot.extensions.sendEmbed
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.listener.message.reaction.ReactionAddListener
import org.javacord.api.util.event.ListenerManager
import org.javacord.api.util.logging.ExceptionLogger
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

@LoadCommand
class SetTicketSystem : Command("setticket", CommandModule.TICKET, "ticket") {
    private val messageMap = mutableMapOf<Long, ListenerManager<MessageCreateListener>>()
    private val reactionMap = mutableMapOf<Long, ListenerManager<ReactionAddListener>>()

    override fun executeCommand(
        server: Server,
        user: User,
        textChannel: ServerTextChannel,
        message: Message,
        args: Array<out String>
    ) {
        message.delete()
        if (args.size == 1) {
            val ticketChannelID: Long = args[0].toLong()

            if (!ticketMap.containsKey(ticketChannelID)) {
                val ticketSystem = TicketSystem("%name%", null, null)

                val ticketSetupEmbed = createEmbed {
                    setAuthor("🎟 | Step 2 of the Setup", null, server.api.yourself.avatar)
                    setDescription("You can chose between two emojis.\n:ok: for a custom channel name or :x: to set the username as channel name")
                    setFooter("👋 | The Ticket System")
                }

                textChannel.sendMessage(ticketSetupEmbed).thenAccept {
                    it.addReactions(ClubyDiscordBot.convertUnicode(":ok:"), ClubyDiscordBot.convertUnicode(":x:"))
                    createTicketListener(it, 1, user.id, ticketChannelID,  ticketSystem)
                    startTimer(it)
                }
            } else {
                sendEmbed(textChannel, 20, TimeUnit.SECONDS) {
                    setAuthor("🎟 | Problem with the Setup")
                    setDescription("The channel ``${server.getTextChannelById(ticketChannelID).get().name}`` is already a ticket channel").setFooter("🎟 | Delete or update a ticketchannel").setTimestampToNow()
                    setColor(Color.decode("0xf2310f"))
                }
            }

        }

    }

    private fun createTicketListener(message: Message, state: Int, memberID: Long, channelID: Long, ticketChannel: TicketSystem) {
        when (state) {

            1 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.isPresent && event.user.isPresent) {
                            if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":ok:"))) {
                                val stepThreeEmbed = createEmbed {
                                    setAuthor("🎟 | Step 3 of the Setup(Set a channelname)")
                                    setDescription("Type your custom channel name now.\nYou are only allowed to use 32 Characters for the name.\n\n" +
                                            "Syntax highlighting like **__Hello ${event.user.get().name}__** or Cluby is ~~not~~ the best bot are allowed to use")
                                    setFooter("🎟 | The Ticket System").setTimestampToNow()
                                }
                                message.edit(stepThreeEmbed).whenComplete { _, _ ->
                                    message.removeAllReactions()
                                    delete(message.id, 2)
                                    createTicketListener(message, 2, memberID, channelID, ticketChannel)
                                }
                            } else if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":x:"))) {
                                val stepFourEmbed = createEmbed {
                                    setAuthor("🎟 | Step 4 of the Setup(Set a message)")
                                    setDescription("Type your custom ticketmessage name now.\nYou are only allowed to use 90 Characters for the name.\n\n" +
                                            "Syntax highlighting like **__Hello ${event.user.get().name}__** or Cluby is ~~not~~ the best bot are allowed to use")
                                    setFooter("🎟 | The Ticket System").setTimestampToNow()
                                }
                                message.edit(stepFourEmbed).whenComplete { _, _ ->
                                    message.removeAllReactions()
                                    delete(message.id, 2)
                                    createTicketListener(message, 3, memberID, channelID, ticketChannel)
                                }
                            }
                        }
                    }
                }
                reactionMap[message.id] = listenerManager
            }

            2 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                    if (event.messageAuthor.asUser().isPresent) {
                        if (event.messageAuthor.asUser().get().id == memberID) {
                            if (event.messageContent.length <= 32) {
                                ticketChannel.channelName = event.messageContent
                                event.deleteMessage()
                                val stepFourEmbed = createEmbed {
                                    setAuthor("🎟 | Step 4 of the Setup(Set a message)")
                                    setDescription("Type your custom ticketmessage name now.\nYou are only allowed to use 90 Characters for the name.\n\n" +
                                            "Syntax highlighting like **__Hello ${event.messageAuthor.asUser().get().name}__** or Cluby is ~~not~~ the best bot are allowed to use")
                                    setFooter("🎟 | The Ticket System").setTimestampToNow()
                                }
                                message.edit(stepFourEmbed).whenComplete { _, _ ->
                                    delete(message.id, 1)
                                    createTicketListener(message, 3, memberID, channelID, ticketChannel)
                                }
                            }
                        }
                    }
                }
                messageMap[message.id] = listenerManager
            }

            3 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                    if (event.messageAuthor.asUser().isPresent) {
                        if (event.messageAuthor.asUser().get().id == memberID) {
                            if (event.messageContent.length <= 150) {
                                ticketChannel.ticketMessage = event.messageContent
                                event.deleteMessage()
                                val stepFourEmbed = createEmbed {
                                    setAuthor("🎟 | Last step of the setup(Type roles for the permissions)")
                                    setDescription("Now you must type mentions of roles.")
                                    setFooter("🎟 | The Ticket System").setTimestampToNow()
                                }
                                message.edit(stepFourEmbed).whenComplete { _, _ ->
                                    delete(message.id, 1)
                                    createTicketListener(message, 4, memberID, channelID, ticketChannel)
                                }
                            }
                        }
                    }
                }
                messageMap[message.id] = listenerManager
            }

            4 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                    if (event.server.isPresent && event.messageAuthor.asUser().isPresent) {
                        if (event.message.mentionedRoles.isNotEmpty()) {
                            val mentions = event.message.mentionedRoles.map { roles -> roles.id }
                            val stringBuilder = StringBuilder()
                            event.message.mentionedRoles.map { roles -> roles.mentionTag }.forEach { message -> stringBuilder.append("\n$message") }
                            event.deleteMessage()
                            ticketChannel.roleIDs = mentions
                            ticketMap[channelID] = ticketChannel
                            message.edit(createEmbed {
                                setAuthor("🎟 | Ticket System")
                                setDescription("The setup for the channel `${event.server.get().getTextChannelById(channelID).get().name}` was finished by **${event.server.get().getMemberById(memberID).get().name}**")
                                addInlineField("⚙️ | Ticket Settings", "Channel Name » ${ticketChannel.channelName}\n\n" +
                                            "Ticket Message » ${ticketChannel.ticketMessage}\n\n" +
                                            "Roles ¬ $stringBuilder") }).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                message.removeReactionsByEmoji(ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                message.addReaction(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                stopTimer(message.id)
                                event.api.threadPool.scheduler.schedule({ message.delete() }, 40, TimeUnit.SECONDS)
                                delete(message.id, 1)

                                message.addReactionAddListener { listener ->
                                    if (listener.server.isPresent && listener.reaction.isPresent) {
                                        listener.user.ifPresent { user ->
                                            if (user.id == memberID) {
                                                if (listener.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))) {
                                                    ticketMap.remove(channelID, ticketChannel)
                                                    message.edit(createEmbed {
                                                        setAuthor("🎟 | Ticket Setup")
                                                        setDescription("You deleted the ticket channel ``${event.server.get().getTextChannelById(channelID).get().name}`` from you server"
                                                        ).setFooter("🎟 | The Ticket System").setTimestampToNow()
                                                        setColor(Color.decode("0x32ff7e"))
                                                    }).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                                        message.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }.removeAfter(20, TimeUnit.SECONDS)
                                    .addRemoveHandler { message.removeOwnReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:")) } }
                        }
                    }
                }
                messageMap[message.id] = listenerManager
            }
        }
    }

    private fun delete(messageId: Long, state: Int) {

        when (state) {

            1 -> {
                messageMap[messageId]?.remove()
                messageMap.remove(messageId)
            }

            2 -> {
                reactionMap[messageId]?.remove()
                reactionMap.remove(messageId)
            }

            3 -> {
                if (messageMap.containsKey(messageId)) messageMap[messageId]?.remove()
                messageMap.remove(messageId)
                if (reactionMap.containsKey(messageId)) reactionMap[messageId]?.remove()
                reactionMap.remove(messageId)
            }
        }
    }

    private val timerMap: MutableMap<Long, TimerTask> = mutableMapOf()
    private val timer: Timer = Timer(true)

    private fun startTimer(message: Message) {

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                delete(message.id, 3)
                message.delete()
                message.channel.sendMessage("The Ticket Setup was terminated, because it took to long.")
                    .thenAccept { message1 ->
                        message1.api.threadPool.scheduler.schedule({ message1.delete() }, 30, TimeUnit.SECONDS)
                    }
                timerMap.remove(message.id)
            }
        }
        timer.schedule(task, (5 * 60000).toLong())
        timerMap[message.id] = task
    }

    private fun stopTimer(messageid: Long) {

        timerMap[messageid]?.cancel()
        timerMap.remove(messageid)
    }



    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)
    override val description: String
        get() = TODO("Not yet implemented")
    override val usage: String
        get() = TODO("Not yet implemented")
}