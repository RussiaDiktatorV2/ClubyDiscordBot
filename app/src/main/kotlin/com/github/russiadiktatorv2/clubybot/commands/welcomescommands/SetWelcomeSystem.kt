package com.github.russiadiktatorv2.clubybot.commands.welcomescommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.convertUnicode
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import com.github.russiadiktatorv2.clubybot.management.database.MariaDB
import com.github.russiadiktatorv2.clubybot.management.interfaces.WelcomeCommand
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.listener.message.reaction.ReactionAddListener
import org.javacord.api.util.event.ListenerManager
import java.awt.Color
import java.lang.NumberFormatException
import java.sql.SQLException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class SetWelcomeSystem : WelcomeCommand {

    override fun executeWelcomeCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            val setupEmbed = createEmbed {
                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Welcomer System")
                setDescription("Welcome to this Setup!,\n You won't need longer as 5 Minutes to setup a welcome message with cool features.")
                setFooter("Write the id of a textchannel now.The Setup was started").setTimestampToNow()
                setColor(Color.decode("0x32ff7e"))
            }
            try {
                event.deleteMessage().whenCompleteAsync { t, u ->
                    val message = event.channel.sendMessage(setupEmbed).get()
                    createListener(message, 1, event.messageAuthor.asUser().get().id, null)
                    startTimer(message)
                }
            } catch (exception: InterruptedException) {
                exception.printStackTrace()
            } catch (exception: ExecutionException) {
                exception.printStackTrace()
            }
        }
    }

    private val messagemap = mutableMapOf<Long, ListenerManager<MessageCreateListener>>()

    private val reactionmap = mutableMapOf<Long, ListenerManager<ReactionAddListener>>()

    private fun createListener(message: Message, state: Int, memberID: Long , welcomeChannel: WelcomeSystem?) {
        when (state) {

            1 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                    if (event.messageAuthor.asUser().get().id == memberID) {
                        try {
                            val welcomeChannelID = event.messageContent.toLong()
                            event.deleteMessage()
                            if (event.server.get().getTextChannelById(welcomeChannelID).isPresent) {
                                if (welcomeMap.containsKey(event.server.get().id)) {
                                    delete(message.id, 1)
                                    event.channel.sendMessage(createEmbed {
                                        setAuthor("${convertUnicode("\uD83D\uDC4B")} | The setup was terminated")
                                        setDescription("The welcomechannel ``${event.server.get().getTextChannelById(welcomeChannelID).get().name}`` " +
                                                "is already the welcomechannel of **${event.server.get().name}**")
                                        setColor(Color.decode("0xf2310f")).setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System")
                                    })
                                } else if (event.server.flatMap { server: Server -> server.getTextChannelById(welcomeChannelID) }.isPresent) {
                                    val welcomeSystem = WelcomeSystem(null, null, null, null)
                                    welcomeSystem.channelID = welcomeChannelID

                                    val stepTwoEmbed = createEmbed {
                                        setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 2 of the Setup", null, message.api.yourself.avatar)
                                        setDescription("Now you can chose between to emojis.\n:ok: for a custom welcome message :x: to don't set a welcome message")
                                        setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System")
                                    }
                                    message.edit(stepTwoEmbed).whenComplete { _, _ ->
                                        message.addReactions(convertUnicode("\uD83C\uDD97"), convertUnicode("\u274C"))
                                        delete(message.id, 1)
                                        createListener(message, 2, memberID, welcomeSystem)
                                    }
                                }
                            } else {
                                event.channel.sendMessage(createEmbed {
                                    setAuthor("${convertUnicode("\uD83D\uDC4B")} | An Error occurred!")
                                    setDescription("Please write an id of a textchannel to set the welcomechannel")
                                    setColor(Color.decode("0xf2310f"))
                                })
                            }
                        } catch (exception: NumberFormatException) {
                            event.channel.sendMessage(createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | An Error occurred!")
                                setDescription("Please write a real id of a textchannel to set the welcomechannel")
                                setColor(Color.decode("0xf2310f"))
                            })
                        }
                    }
                }
                messagemap[message.id] = listenerManager
            }

            2 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":ok:"))) {
                            val stepThreeEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 3 of the Setup(Set a message)")
                                setDescription("Type your custom welcome message now.\nYou are only allowed to use 170 Characters for the name.")
                                setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System").setTimestampToNow()
                            }
                            message.edit(stepThreeEmbed).whenCompleteAsync { _, _ ->
                                message.removeAllReactions()
                                delete(message.id, 2)
                                createListener(message, 3, memberID, welcomeChannel)
                            }
                        } else if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":x:"))) {
                            val stepFourEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 4 of the Setup(Username?)")
                                setDescription("Do you would like to add the username with the discriminater (#0042 for example) in the welcome picture?\n\n" + ":ok: for yes or :x: for no")
                                setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System").setTimestampToNow()
                            }
                            message.edit(stepFourEmbed).whenCompleteAsync { _, _ ->
                                message.removeReactionsByEmoji(event.user.get(), convertUnicode(":x:"), convertUnicode(":ok:"))
                                delete(message.id, 2)
                                createListener(message, 4, memberID, welcomeChannel)
                            }
                        }
                    }
                }
                reactionmap[message.id] = listenerManager
            }

            3 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                    if (event.messageAuthor.id == memberID) {
                        val welcomeMessage = event.messageContent
                        if (welcomeMessage.length <= 170) {
                            welcomeChannel?.welcomeMessage = welcomeMessage
                            event.deleteMessage()

                            val stepFourEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 4 of the Setup(Username?)")
                                setDescription("Do you would like to add the username with the discriminater (#0042 for example) in the welcome picture?\n\n" + ":ok: for yes or :x: for no")
                                setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System").setTimestampToNow()
                            }
                            message.edit(stepFourEmbed).whenCompleteAsync { _, _ ->
                                message.addReactions(convertUnicode("\uD83C\uDD97"), convertUnicode("\u274C"))
                                delete(message.id, 1)
                                createListener(message, 4, memberID, welcomeChannel)
                            }
                        }
                    }
                }
                messagemap[message.id] = listenerManager
            }

            4 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":ok:"))) {
                            welcomeChannel?.userNamesAllowed = true
                            val stepFiveEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 5 of the Setup (Membercount)")
                                setDescription("Do you would like to add a membercount in the welcome picture which count the new user is on server?\n:ok: for yes or :x: for no")
                                setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System").setTimestampToNow()
                            }
                            message.edit(stepFiveEmbed).whenCompleteAsync { _, _ ->
                                message.removeReactionsByEmoji(event.user.get(), convertUnicode("\uD83C\uDD97"), convertUnicode("\u274C"))
                                delete(message.id, 2)
                                createListener(message, 5, memberID, welcomeChannel)
                            }
                        } else if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":x:"))) {
                            welcomeChannel?.userNamesAllowed = false
                            val stepFiveEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Step 5 of the Setup (Membercount)")
                                setDescription("Do you would like to add a membercount in the welcome picture which count the new user is on server?\n:ok: for yes or :x: for no")
                                setFooter("${convertUnicode("\uD83D\uDC4B")} | The Welcomer System").setTimestampToNow()
                            }
                            message.edit(stepFiveEmbed).whenCompleteAsync { _, _ ->
                                message.removeReactionsByEmoji(event.user.get(), convertUnicode("\uD83C\uDD97"), convertUnicode("\u274C"))
                                delete(message.id, 2)
                                createListener(message, 5, memberID, welcomeChannel)
                            }
                        }
                    }
                }
                reactionmap[message.id] = listenerManager
            }

            5 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener{ event ->
                    if (event.userId == memberID) {
                        if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":ok:"))) {
                            welcomeChannel?.memberCountAllowed = true
                            welcomeMap[event.server.get().id] = welcomeChannel!!

                            val finishedSetupEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Finished Welcomer Setup", null, event.api.yourself.avatar)
                                setDescription("The setup was finished by **${event.user.get().name}** for the textchannel " +
                                        "`${welcomeChannel.channelID?.let { event.server.get().getTextChannelById(it).get().name }}`")
                                addInlineField("Welcome Message", welcomeChannel.welcomeMessage)
                                addField("👨 Username in Picture? | 👨‍🎓 Membercount in Picture?", "The username in the picture ``${if (welcomeChannel.userNamesAllowed!!) "is allowed" else "isn't allowed"}``\n\n" +
                                        "The membercount in the picture ``${if (welcomeChannel.memberCountAllowed!!) "is allowed" else "isn't allowed"}``", false)
                                setColor(Color.decode("0x32ff7e"))
                            }
                            event.api.threadPool.scheduler.schedule( {message.delete() },30, TimeUnit.SECONDS)
                            message.edit(finishedSetupEmbed).whenCompleteAsync { _, _ ->
                                stopTimer(message.id)
                                message.removeAllReactions()
                                delete(message.id, 2)
                            }
                        } else if (event.reaction.get().emoji.equalsEmoji(convertUnicode(":x:"))) {
                            welcomeChannel?.memberCountAllowed = false
                            welcomeMap[event.server.get().id] = welcomeChannel!!

                            val finishedSetupEmbed = createEmbed {
                                setAuthor("${convertUnicode("\uD83D\uDC4B")} | Finished Welcomer Setup", null, event.api.yourself.avatar)
                                setDescription("The setup was finished by **${event.user.get().name}** for the textchannel " +
                                        "`${welcomeChannel.channelID?.let { event.server.get().getTextChannelById(it).get().name }}`")
                                addInlineField("Welcome Message", "${welcomeChannel.welcomeMessage} \u00AD")
                                addField("👨 Username in Picture? | 👨‍🎓 Membercount in Picture?", "The username in the picture ``${if (welcomeChannel.userNamesAllowed!!) "is allowed" else "isn't allowed"}``\n\n" +
                                        "The membercount in the picture ``${if (welcomeChannel.memberCountAllowed!!) "is allowed" else "isn't allowed"}``", false)
                                setColor(Color.decode("0x32ff7e"))
                            }
                            event.api.threadPool.scheduler.schedule( {message.delete() },30, TimeUnit.SECONDS)
                            message.edit(finishedSetupEmbed).whenComplete { _, _ ->
                                stopTimer(message.id)
                                message.removeAllReactions()
                                delete(message.id, 2)
                            }
                        }
                    }
                }
                reactionmap[message.id] = listenerManager
            }
        }
    }

    private fun delete(messageid: Long, state: Int) {

        when (state) {

            1 -> {
                messagemap[messageid]?.remove()
                messagemap.remove(messageid)
            }

            2 -> {
                reactionmap[messageid]?.remove()
                reactionmap.remove(messageid)
            }

            3 -> {
                if (messagemap.containsKey(messageid)) messagemap[messageid]?.remove()
                messagemap.remove(messageid)
                if (reactionmap.containsKey(messageid)) reactionmap[messageid]?.remove()
                reactionmap.remove(messageid)
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
                message.channel.sendMessage("The Welcomer Setup was terminated, because it took to long.")
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

    fun loadWelcomeSystemCache() {
        val resultSet = MariaDB.onQuery("SELECT * FROM welcomeSystems")

        try {
            if (resultSet != null) {
                while (resultSet.next()) {

                    val guildID = resultSet.getLong("serverID")
                    val welcomeChannelID = resultSet.getLong("welcomeChannelID")
                    val welcomeMessage = resultSet.getString("welcomeMessage")
                    val userNameAllowed = resultSet.getBoolean("userNameAllowed")
                    val memberCountAllowed = resultSet.getBoolean("memberCountAllowed")

                    val welcomeSystem = WelcomeSystem(welcomeChannelID, welcomeMessage, userNameAllowed, memberCountAllowed)
                    welcomeMap[guildID] = welcomeSystem
                }
                resultSet.close()
            }
        } catch (exception: SQLException) {
            exception.errorCode
        }
    }
}