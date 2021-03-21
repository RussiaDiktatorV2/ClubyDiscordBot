package com.github.russiadiktatorv2.clubybot.commands.welcomescommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendMissingArguments
import com.github.russiadiktatorv2.clubybot.management.interfaces.WelcomeCommand
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.listener.message.reaction.ReactionAddListener
import org.javacord.api.util.event.ListenerManager
import org.javacord.api.util.logging.ExceptionLogger
import java.awt.Color
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class SetWelcomeSystem : WelcomeCommand {

    override fun executeWelcomeCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            event.deleteMessage()
            if (arguments.size == 2) {
                if (welcomeMap.containsKey(event.server.get().id).not()) { val welcomeChannelID = arguments[1].toLong()
                    if (event.server.flatMap { server: Server -> server.getTextChannelById(welcomeChannelID) }.isPresent) {
                        val welcomeSystem = WelcomeSystem(welcomeChannelID, null)
                        val setupEmbed = createEmbed {
                            setAuthor("👋 | Step 2 of the Setup", null, event.api.yourself.avatar)
                            setDescription("You can chose between two emojis.\n:ok: for a custom welcome message :x: to don't set a welcome message")
                            setFooter("👋 | The Welcomer System")
                        }
                        try {
                            event.serverTextChannel.ifPresent { channel -> channel.sendMessage(setupEmbed).thenAccept { it.addReactions(ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                createListener(it, 1, event.messageAuthor.asUser().get().id, welcomeSystem)
                                startTimer(it) }
                            }
                        } catch (exception: InterruptedException) {
                            exception.printStackTrace()
                        } catch (exception: ExecutionException) {
                            exception.printStackTrace()
                        }
                    }

                } else {
                    sendEmbed(event.serverTextChannel.get(), 20, TimeUnit.SECONDS) {
                        setAuthor("👋 | Problem with the Setup")
                        setDescription("Your server have already a welcomechannel").setFooter("👋 | Delete or update a welcomechannel").setTimestampToNow()
                        setColor(Color.decode("0xf2310f"))
                    }
                }
            } else {
                event.serverTextChannel.ifPresent { channel -> channel.sendMissingArguments("setwelcome textchanelid", "Welcome", event.server.get()) }
            }
        } else {
            sendEmbed(event.serverTextChannel.get(), 13, TimeUnit.SECONDS) {
                setAuthor("${ClubyDiscordBot.convertUnicode("\uD83D\uDC4B")} | Problem with the Setup")
                setDescription("You don't have the required permissions `${PermissionType.MANAGE_SERVER}` to execute the following command").setFooter("👋 | Welcomer System")
                setColor(Color.decode("0x32ff7e"))
            }
        }
    }

    private val messagemap = mutableMapOf<Long, ListenerManager<MessageCreateListener>>()

    private val reactionmap = mutableMapOf<Long, ListenerManager<ReactionAddListener>>()

    private fun createListener(message: Message, state: Int, memberID: Long, welcomeChannel: WelcomeSystem) {
        when (state) {

            1 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.isPresent && event.user.isPresent) {
                            if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":ok:"))) {
                                val stepThreeEmbed = createEmbed {
                                    setAuthor("👋 | Step 3 of the Setup(Set a message)")
                                    setDescription("Type your custom welcome message now.\nYou are only allowed to use 170 Characters for the name.\n\n" +
                                            "Syntax highlighting like **__Hello ${event.user.get().name}__** or Cluby is ~~not~~ the best bot are allowed to use")
                                    setFooter("👋 | The Welcomer System").setTimestampToNow()
                                }
                                message.edit(stepThreeEmbed).whenComplete { _, _ ->
                                    message.removeAllReactions()
                                    delete(message.id, 2)
                                    createListener(message, 2, memberID, welcomeChannel)
                                }
                            } else if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":x:"))) {
                                val stepFourEmbed = createEmbed {
                                    setAuthor("👋 | Step 4 of the Setup(Username?)")
                                    setDescription("Do you would like to add the username with the discriminater (${event.api.getUserById(433307484166422538).get().discriminatedName} for example) in the welcome picture?\n\n" + ":ok: for yes or :x: for no")
                                    setFooter("👋 | The Welcomer System").setTimestampToNow()
                                }
                                message.edit(stepFourEmbed).whenComplete { _, _ ->
                                    message.removeReactionsByEmoji(event.user.get(), ClubyDiscordBot.convertUnicode(":x:"), ClubyDiscordBot.convertUnicode(":ok:"))
                                    delete(message.id, 2)
                                    createListener(message, 3, memberID, welcomeChannel)
                                }
                            }
                        }
                    }
                }
                reactionmap[message.id] = listenerManager
            }

            2 -> {
                val listenerManager: ListenerManager<MessageCreateListener> = message.channel.addMessageCreateListener { event ->
                        if (event.messageAuthor.asUser().isPresent) {
                            if (event.messageAuthor.asUser().get().id == memberID) {
                                val welcomeMessage = event.messageContent
                                if (welcomeMessage.length <= 170) {
                                    welcomeChannel.welcomeMessage = welcomeMessage
                                    event.deleteMessage()

                                    val stepFourEmbed = createEmbed {
                                        setAuthor("👋 | Step 4 of the Setup(Username?)")
                                        setDescription("Do you would like to add the username with the discriminater (${event.api.getUserById(433307484166422538).get().discriminatedName} for example) in the welcome picture?\n\n" + ":ok: for yes or :x: for no")
                                        setFooter("👋 | The Welcomer System").setTimestampToNow()
                                    }
                                    message.edit(stepFourEmbed).whenComplete { _, _ ->
                                        message.addReactions(ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                        delete(message.id, 1)
                                        createListener(message, 3, memberID, welcomeChannel)
                                    }
                                }
                            }
                        }
                }
                messagemap[message.id] = listenerManager
            }

            3 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.isPresent) {
                            if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":ok:"))) {
                                welcomeChannel.userNamesAllowed = true
                                val stepFiveEmbed = createEmbed {
                                    setAuthor("👋 | Step 5 of the Setup (Membercount)")
                                    setDescription("Do you would like to add a membercount in the welcome picture which count the new user is on server?\n:ok: for yes or :x: for no")
                                    setFooter("👋 | The Welcomer System").setTimestampToNow()
                                }
                                message.edit(stepFiveEmbed).whenComplete { _, _ ->
                                    message.removeReactionsByEmoji(event.user.get(), ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                    delete(message.id, 2)
                                    createListener(message, 4, memberID, welcomeChannel)
                                }
                            } else if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":x:"))) {
                                welcomeChannel.userNamesAllowed = false
                                val stepFiveEmbed = createEmbed {
                                    setAuthor("👋 | Step 5 of the Setup (Membercount)")
                                    setDescription("Do you would like to add a membercount in the welcome picture which count the new user is on server?\n:ok: for yes or :x: for no")
                                    setFooter("👋 | The Welcomer System").setTimestampToNow()
                                }
                                message.edit(stepFiveEmbed).whenComplete { _, _ ->
                                    message.removeReactionsByEmoji(event.user.get(), ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                    delete(message.id, 2)
                                    createListener(message, 4, memberID, welcomeChannel)
                                }
                            }
                        }
                    }
                }
                reactionmap[message.id] = listenerManager
            }

            4 -> {
                val listenerManager: ListenerManager<ReactionAddListener> = message.addReactionAddListener { event ->
                    if (event.userId == memberID) {
                        if (event.reaction.isPresent && event.server.isPresent) {
                            if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":ok:"))) {
                                welcomeChannel.memberCountAllowed = true
                                val serverID: Long = event.server.get().id
                                welcomeMap[serverID] = welcomeChannel

                                val finishedSetupEmbed = createEmbed {
                                    setAuthor("👋 | Finished Welcomer Setup", null, event.api.yourself.avatar)
                                    setDescription("The setup was finished by **${event.user.get().name}** for the textchannel `${event.server.get().getTextChannelById(welcomeChannel.channelID).get().name}`")
                                    addInlineField("Welcome Message", if (welcomeChannel.welcomeMessage != null) "${welcomeChannel.welcomeMessage}" else "null")
                                    addField("👨 Username in Picture? | 👨‍🎓 Membercount in Picture?",
                                        "The username in the picture ``${if (welcomeChannel.userNamesAllowed) "is allowed" else "isn't allowed"}``\n\n" +
                                                "The membercount in the picture ``${if (welcomeChannel.memberCountAllowed) "is allowed" else "isn't allowed"}``\n\n" +
                                                "React to the ``❗`` emoji if the settings are wrong.", false
                                    )
                                    setColor(Color.decode("0x32ff7e"))
                                }
                                message.edit(finishedSetupEmbed).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                    message.removeReactionsByEmoji(ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                    message.addReaction(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                    stopTimer(message.id)
                                    event.api.threadPool.scheduler.schedule({ message.delete() }, 40, TimeUnit.SECONDS)
                                    delete(message.id, 2)

                                    message.addReactionAddListener { listener ->
                                        if (listener.user.isPresent && listener.reaction.isPresent) {
                                            if (listener.user.get().isBot.not()) {
                                                if (listener.userId == memberID) {
                                                    if (listener.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))) {
                                                        welcomeMap.remove(serverID, welcomeChannel)
                                                        message.edit(createEmbed {
                                                            setAuthor("👋 | Welcomersetup")
                                                            setDescription("You deleted the welcome channel ``${event.server.get().getTextChannelById(welcomeChannel.channelID).get().name}`` from you server").setFooter("👋 | The Welcomer System").setTimestampToNow()
                                                            setColor(Color.decode("0x32ff7e"))
                                                        }).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                                            message.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }.removeAfter(20, TimeUnit.SECONDS).addRemoveHandler { message.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:")) }
                                }
                            } else if (event.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":x:"))) {
                                welcomeChannel.memberCountAllowed = false
                                val serverID: Long = event.server.get().id
                                welcomeMap[serverID] = welcomeChannel

                                val finishedSetupEmbed = createEmbed {
                                    setAuthor("👋 | Finished Welcomer Setup", null, event.api.yourself.avatar)
                                    setDescription("The setup was finished by **${event.user.get().name}** for the textchannel `${event.server.get().getTextChannelById(welcomeChannel.channelID).get().name}`")
                                    addInlineField("Welcome Message", "${if (welcomeChannel.welcomeMessage != null) "${welcomeChannel.welcomeMessage}" else "null"} \u00AD")
                                    addField("👨 Username in Picture? | 👨‍🎓 Membercount in Picture?", "The username in the picture ``${if (welcomeChannel.userNamesAllowed) "is allowed" else "isn't allowed"}``\n\n" +
                                            "The membercount in the picture ``${if (welcomeChannel.memberCountAllowed) "is allowed" else "isn't allowed"}``\n\n" +
                                            "React to the ``❗`` emoji if the settings are wrong.\n", false)
                                    setColor(Color.decode("0x32ff7e"))
                                }
                                message.edit(finishedSetupEmbed).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                    message.removeReactionsByEmoji(ClubyDiscordBot.convertUnicode("\uD83C\uDD97"), ClubyDiscordBot.convertUnicode("\u274C"))
                                    message.addReaction(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                    stopTimer(message.id)
                                    event.api.threadPool.scheduler.schedule({ message.delete() }, 40, TimeUnit.SECONDS)
                                    delete(message.id, 2)

                                    message.addReactionAddListener { listener ->
                                        if (listener.user.isPresent && listener.reaction.isPresent) {
                                            if (listener.user.get().isBot.not()) {
                                                if (listener.userId == memberID) {
                                                    if (listener.reaction.get().emoji.equalsEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))) {
                                                        welcomeMap.remove(serverID, welcomeChannel)
                                                        message.edit(createEmbed {
                                                            setAuthor("👋 | Welcomersetup")
                                                            setDescription("You deleted the welcome channel ``${event.server.get().getTextChannelById(welcomeChannel.channelID).get().name}`` from you server").setFooter("👋 | The Welcomer System").setTimestampToNow()
                                                            setColor(Color.decode("0x32ff7e"))
                                                        }).exceptionally(ExceptionLogger.get()).whenComplete { _, _ ->
                                                            message.removeReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:"))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }.removeAfter(20, TimeUnit.SECONDS).addRemoveHandler { message.removeOwnReactionByEmoji(ClubyDiscordBot.convertUnicode(":exclamation:")) }
                                }
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
        timer.schedule(task, (4 * 60000).toLong())
        timerMap[message.id] = task
    }

    private fun stopTimer(messageid: Long) {

        timerMap[messageid]?.cancel()
        timerMap.remove(messageid)
    }
}