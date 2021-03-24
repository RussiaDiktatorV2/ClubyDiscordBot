package com.github.russiadiktatorv2.clubybot.commands.devcommands

import com.github.russiadiktatorv2.clubybot.extensions.deleteAfter
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.abstracts.Command
import com.github.russiadiktatorv2.clubybot.management.commands.annotations.LoadCommand
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule

import com.github.russiadiktatorv2.clubybot.management.commands.interfaces.ICommand
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

import org.javacord.api.event.message.MessageCreateEvent
import java.util.concurrent.TimeUnit

import java.awt.Color

@LoadCommand
class ContributorCommand : Command("contributer", CommandModule.DEFAULT) {
    val serverID = 795462575503573013

    override fun executeCommand(server: Server, user: User, textChannel: ServerTextChannel, message: Message, args: Array<out String>) {
        message.delete()

        if (user.id == server.api.ownerId) {
            if (args.size == 3) {
                when (args[1]) {
                    "add" -> {
                        try {
                            val id = args[2].toLong()
                            if (server.id == serverID) {
                                if (server.getMemberById(id).isPresent) {
                                    if (! CacheManager.devList.contains(id)) {
                                        textChannel.sendMessage("Erfolgreich.")
                                        CacheManager.devList.add(id)
                                    } else {
                                        textChannel.sendMessage("The user is already in the list.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                                    }
                                } else {
                                    textChannel.sendMessage("Die Kennung wurde nicht gefunden.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                                }
                            }
                        } catch (exception: NumberFormatException) {
                            textChannel.sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                        }
                    }
                    "remove" -> {
                        try {
                            val id = args[2].toLong()
                            if (server.id == 795462575503573013) {
                                if (server.getMemberById(id).isPresent) {
                                    if (CacheManager.devList.contains(id)) {
                                        textChannel.sendMessage("Erfolgreich.")
                                        CacheManager.devList.remove(id)
                                    } else {
                                        textChannel.sendMessage("The user is not in the list.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                                    }
                                } else {
                                    textChannel.sendMessage("Die Kennung wurde nicht gefunden.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                                }
                            }
                        } catch (exception: NumberFormatException) {
                            textChannel.sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").deleteAfter(server.api, 5, TimeUnit.SECONDS)
                        }
                    }
                }
            } else if (args.size == 1) {
                if (CacheManager.devList.size > 0) {
                    val stringBuilder: StringBuilder = StringBuilder("~~----------~~ \n\n")
                    CacheManager.devList.forEach { contributor -> stringBuilder.append("${server.getHighestRole(server.getMemberById(contributor).get()).get().mentionTag} ↣ ${server.api.getUserById(contributor).get().name} \n") }
                    // Kommentier' hier mal in der Pull Request mit dem kleinen "+" links neben der Zeile, damit ich weiß, dass du dir das auch angeschaut hast :)
                    sendEmbed(textChannel,15, TimeUnit.DAYS) {
                        addInlineField("Contributor-List:", stringBuilder.toString())
                        setColor(Color.decode("0x32ff7e"))
                        setAuthor("💻 | The Cluby Contributor-List", null, server.api.yourself.avatar)
                    }
                } else {
                    textChannel.sendMessage("The list is empty.").thenAccept {
                        server.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                    }
                }
            }
        } else {
            textChannel.sendMessage("You must be the maintainer of Cluby to execute this command.").thenAccept {
                server.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
            }
        }
    }

    override val permissions: MutableList<PermissionType>
        get() = mutableListOf(PermissionType.ADMINISTRATOR)
    override val description: String
        get() = TODO("Implement your description here")
    override val usage: String
        get() = TODO("Implement your usage here")
}