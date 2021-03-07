package com.github.russiadiktatorv2.clubybot.commands.welcomescommands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendMissingArguments
import com.github.russiadiktatorv2.clubybot.management.interfaces.WelcomeCommand
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

class RemoveWelcomeSystem : WelcomeCommand {

    override fun executeWelcomeCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        event.deleteMessage()
        if (event.server.get().hasAnyPermission(event.messageAuthor.asUser().get(), PermissionType.MANAGE_SERVER, PermissionType.ADMINISTRATOR)) {
            if (event.messageAuthor.asUser().isPresent && arguments.size == 1) {
                val serverID = event.server.get().id
                if (welcomeMap.containsKey(serverID)) {
                    welcomeMap.remove(serverID)

                    sendEmbed(event.serverTextChannel.get(), 30, TimeUnit.SECONDS) {
                        setAuthor("👋 | Welcomer System")
                        setDescription("You deleted the welcomechannel of **${event.server.get().name}**!").setFooter("👋 | The Welcomer System").setTimestampToNow()
                        setColor(Color.decode("0x32ff7e"))
                    }
                } else {
                    sendEmbed(event.serverTextChannel.get(), 20, TimeUnit.SECONDS) {
                        setAuthor("👋 | Welcomer System")
                        setDescription("Your server don't have a welcomechannel").setFooter("👋 | Maybe you try to set a welcomechannel?").setTimestampToNow()
                        setColor(Color.decode("0xf2310f"))
                    }
                }
            } else {
                event.serverTextChannel.ifPresent { channel -> channel.sendMissingArguments("removewelcome", "Welcome", event.server.get()) }
            }
        } else {
            sendEmbed(event.serverTextChannel.get(), 13, TimeUnit.SECONDS) {
                setAuthor("${ClubyDiscordBot.convertUnicode("\uD83D\uDC4B")} | Problem with the Setup")
                setDescription("You don't have the required permissions `${PermissionType.MANAGE_SERVER}` to execute the following command").setFooter("👋 | Welcomer System")
                setColor(Color.decode("0x32ff7e"))
            }
        }
    }
}