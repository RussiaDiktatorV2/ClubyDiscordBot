package com.github.russiadiktatorv2.clubybot.commands.devcommands

import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager

import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent

import org.javacord.api.event.message.MessageCreateEvent
import java.util.concurrent.TimeUnit

import java.awt.Color
class ContributorCommand : CommandEvent {


    override fun executeCommand(command: String, event: MessageCreateEvent, arguments: List<String>) {

        event.deleteMessage()
        if (event.getMessageAuthor().asUser().get().getId() == (433307484166422538)) {

            if (arguments.size == 3) {
                when (arguments[1]) {

                    "add" -> {
                        try {

                            val id = arguments[2].toLong()
                            if (event.getServer().get().getId() == (795462575503573013)) {

                                if (event.getServer().get().getMemberById(id).isPresent) {
                                    if (! CacheManager.devList.contains(id)) {

                                        event.getChannel().sendMessage("Erfolgreich.")
                                        CacheManager.devList.add(id)

                                    } else {
                                        event.getChannel().sendMessage("The user is already in the list.").thenAccept {
                                            event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                                        }

                                    }

                                } else {
                                    event.getChannel().sendMessage("Die Kennung wurde nicht gefunden.").thenAccept {
                                        event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                                    }

                                }

                            }
                        } catch (exception: NumberFormatException) {
                            event.getChannel().sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").thenAccept {
                                event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                            }

                        }

                    }
                    "remove" -> {
                        try {

                            val id = arguments[2].toLong()
                            if (event.getServer().get().getId() == (795462575503573013)) {

                                if (event.getServer().get().getMemberById(id).isPresent) {
                                    if (CacheManager.devList.contains(id)) {

                                        event.getChannel().sendMessage("Erfolgreich.")
                                        CacheManager.devList.remove(id)

                                    } else {
                                        event.getChannel().sendMessage("The user is not in the list.").thenAccept {
                                            event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                                        }

                                    }

                                } else {
                                    event.getChannel().sendMessage("Die Kennung wurde nicht gefunden.").thenAccept {
                                        event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                                    }

                                }

                            }

                        } catch (exception: NumberFormatException) {
                            event.getChannel().sendMessage("Bitte schreiben sie eine Kennung, die gültig ist.").thenAccept {
                                event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                            }

                        }

                    }

                }

            } else if (arguments.size == 1) {

                if (CacheManager.devList.size > 0) {

                    val stringBuilder: StringBuilder = StringBuilder("~~----------~~ \n\n")
                    CacheManager.devList.forEach { contributor -> stringBuilder.append("${event.getServer().get().getHighestRole(event.getServer().get().getMemberById(contributor).get()).get().getMentionTag()} ↣ ${event.getApi().getUserById(contributor).get().getName()} \n") }

                    sendEmbed(event.channel,15, TimeUnit.DAYS) {
                        addInlineField("Contributor-List:", stringBuilder.toString())
                        setColor(Color.decode("0x32ff7e"))

                        setAuthor("💻 | The Cluby Contributor-List",null, event.api.yourself.avatar)
                    }

                } else {
                    event.getChannel().sendMessage("The list is empty.").thenAccept {
                        event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
                    }

                }

            }

        } else {
            event.getChannel().sendMessage("You must be the maintainer of Cluby to execute this command.").thenAccept {
                event.getApi().getThreadPool().getScheduler().schedule({ it.delete() },5, TimeUnit.SECONDS)
            }

        }

    }

}