package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import com.github.russiadiktatorv2.clubybot.extensions.sendEmbed
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.reflections8.Reflections
import java.awt.Color
import java.util.concurrent.TimeUnit

object CommandManager {

    val commands = mutableMapOf<String, Command>()

    fun loadCommands() {
        val reflections = Reflections("com.github.russiadiktatorv2.clubybot.commands")

        for (clazz in reflections.getTypesAnnotatedWith(LoadCommand::class.java, true)) {
            val obj = clazz.getDeclaredConstructor().newInstance()

            if (obj is Command) registerCommand(obj)
        }
    }

    fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message) {
        val msg: String = message.content
        val args: Array<String> = if (msg.contains(" ")) msg.substring(msg.split(" ")[0].length + 1).split(" ")
            .toTypedArray() else emptyArray()
        val commandName: String =
            msg.substring(CacheManager.prefixMap.getOrDefault(server.id, "!").length).split(" ")[0].toLowerCase()

        if (!commands.containsKey(commandName)) return

        val command: Command? = commands[commandName]
        val module: CommandModule? = command?.module

        var hasPermission = false

        for (permission in server.getPermissions(user).allowedPermission) {
            if (server.hasPermission(user, permission)) {
                hasPermission = true
            } else {
                print("A")
            }
        }

        if (hasPermission) {
            when (module) {
                CommandModule.MODERATION -> {
                    if (CacheManager.moderationModule.contains(server.id).not())
                        command.executeCommand(server, user, textChannel, message, args)
                    else
                        sendEmbed(textChannel, 15, TimeUnit.SECONDS) {
                            setTitle("❌ Failed to execute the following command")
                            setDescription("Your server does not have the ``moderation`` module enabled")
                            setColor(Color.decode("0xf2310f"))
                        }
                }

                CommandModule.TICKET -> {
                    if (CacheManager.ticketModule.contains(server.id).not())
                        command.executeCommand(server, user, textChannel, message, args)
                    else
                        sendEmbed(textChannel, 15, TimeUnit.SECONDS) {
                            setTitle("❌ Failed to execute the following command")
                            setDescription("Your server does not have the ``ticket`` module enabled")
                            setColor(Color.decode("0xf2310f"))
                        }
                }

                CommandModule.WELCOME -> {
                    if (CacheManager.welcomeModule.contains(server.id).not())
                        command.executeCommand(server, user, textChannel, message, args)
                    else
                        sendEmbed(textChannel, 15, TimeUnit.SECONDS) {
                            setTitle("❌ Failed to execute the following command")
                            setDescription("Your server does not have the ``welcome`` module enabled")
                            setColor(Color.decode("0xf2310f"))
                        }
                }

                else -> command?.executeCommand(server, user, textChannel, message, args)
            }
        }
    }

    private fun registerCommand(command: Command) {
        commands[command.name] = command

        for (alias in command.aliases) {
            commands[alias] = command
        }
    }
}