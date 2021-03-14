package com.github.russiadiktatorv2.clubybot.commands.devcommands

import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager

import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent

import org.javacord.api.event.message.MessageCreateEvent
import java.util.concurrent.TimeUnit

import java.awt.Color
class ContributorCommand : CommandEvent {

    val ownerID = 433307484166422538
    val serverID = 795462575503573013

    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {
        event.deleteMessage()
        if (event.messageAuthor.asUser().get().id == ownerID) {
            if (arguments.size == 3) {
                when (arguments[1]) {
                    "add" -> {
                        try {
                            val id = arguments[2].toLong()
                            if (event.server.get().id == serverID) {
                                if (event.server.get().getMemberById(id).isPresent) {
                                    if (! CacheManager.devList.contains(id)) {
                                        event.channel.sendMessage("Erfolgreich.")
                                        CacheManager.devList.add(id)
                                    } else {
                                        event.channel.sendMessage("The user is already in the list.").thenAccept {
                                            event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                                        }
                                    }
                                } else {
                                    event.channel.sendMessage("Die Kennung wurde nicht gefunden.").thenAccept {
                                        event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                                    }
                                }
                            }
                        } catch (exception: NumberFormatException) {
                            event.channel.sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").thenAccept {
                                event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                            }
                        }
                    }
                    "remove" -> {
                        try {
                            val id = arguments[2].toLong()
                            if (event.server.get().id == 795462575503573013) {
                                if (event.server.get().getMemberById(id).isPresent) {
                                    if (CacheManager.devList.contains(id)) {
                                        event.channel.sendMessage("Erfolgreich.")
                                        CacheManager.devList.remove(id)
                                    } else {
                                        event.channel.sendMessage("The user is not in the list.").thenAccept {
                                            event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                                        }
                                    }
                                } else {
                                    event.channel.sendMessage("Die Kennung wurde nicht gefunden.").thenAccept {
                                        event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                                    }
                                }
                            }
                        } catch (exception: NumberFormatException) {
                            event.channel.sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").thenAccept {
                                event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                            }
                        }
                    }
                }
            } else if (arguments.size == 1) {
                if (CacheManager.devList.size > 0) {
                    val stringBuilder: StringBuilder = StringBuilder("~~----------~~ \n\n")
                    CacheManager.devList.forEach { contributor -> stringBuilder.append("${event.server.get().getHighestRole(event.server.get().getMemberById(contributor).get()).get().mentionTag} ↣ ${event.api.getUserById(contributor).get().name} \n") }
                    sendEmbed(event.channel,15, TimeUnit.DAYS) {
                        addInlineField("Contributor-List:", stringBuilder.toString())
                        setColor(Color.decode("0x32ff7e"))
                        setAuthor("💻 | The Cluby Contributor-List",null, event.api.yourself.avatar)
                    }
                } else {
                    event.channel.sendMessage("The list is empty.").thenAccept {
                        event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
                    }
                }
            }
        } else {
            event.channel.sendMessage("You must be the maintainer of Cluby to execute this command.").thenAccept {
                event.api.threadPool.scheduler.schedule({ it.delete() },5, TimeUnit.SECONDS)
            }
        }
    }
}