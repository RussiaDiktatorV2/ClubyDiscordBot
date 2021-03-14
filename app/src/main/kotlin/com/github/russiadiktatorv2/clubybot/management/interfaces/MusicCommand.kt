package com.github.russiadiktatorv2.clubybot.management.interfaces

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import org.javacord.api.event.message.MessageCreateEvent

interface MusicCommand {

    fun executeMusicCommand(command: String, event: MessageCreateEvent, arguments: List<String>, musicPlayer: AudioPlayer)

}