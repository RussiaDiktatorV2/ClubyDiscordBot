/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.russiadiktatorv2.clubybot.core

import com.github.russiadiktatorv2.clubybot.events.GuildMemberJoinEvent
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.loadClubyCache
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.moderationModule
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketModule
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeModule
import com.github.russiadiktatorv2.clubybot.management.commands.CommandManager
import com.github.russiadiktatorv2.clubybot.management.database.MariaDB
import com.github.russiadiktatorv2.clubybot.settings.ClubySettings
import com.vdurmont.emoji.EmojiParser
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.entity.intent.Intent
import java.util.concurrent.TimeUnit

object ClubyDiscordBot {

    init {
        loadClubyCache()

        val discordApi = DiscordApiBuilder().setToken(ClubySettings.BOT_TOKEN).setWaitForUsersOnStartup(false).setWaitForServersOnStartup(false)
            .setIntents(Intent.GUILDS, Intent.GUILD_MEMBERS, Intent.GUILD_MESSAGES, Intent.GUILD_MESSAGE_REACTIONS)
            .addServerMemberJoinListener(GuildMemberJoinEvent())
            .login().join()
        discordApi.setMessageCacheSize(0, 0)
        discordApi.setReconnectDelay { reconnectDelay -> reconnectDelay * 2 }
        discordApi.threadPool.daemonScheduler.scheduleAtFixedRate( { autoCache()} , 0, 12, TimeUnit.HOURS)
        discordApi.threadPool.daemonScheduler.scheduleAtFixedRate( {changeActivity(discordApi)}, 0, 2, TimeUnit.MINUTES)

        discordApi.addMessageCreateListener { event ->
            if (event.server.isPresent && event.isServerMessage) {
                if (event.messageAuthor.asUser().isPresent && event.messageAuthor.asUser().get().isBot.not()) {
                    val customPrefix = prefixMap.getOrDefault(event.server.get().id, "!")
                    if (event.messageContent.startsWith(customPrefix)) {
                        CommandManager().loadClubyCommands(event.messageContent.substring(customPrefix.length).split(' ')[0], event, event.messageContent.split(' '))
                    }
                }
            }
        }
    }

    private fun changeActivity(discordApi: DiscordApi) {
        val statusList = arrayOf("${convertUnicode("\uD83D\uDD75\u200D♂")}️| with ${discordApi.servers.size} guilds",
            "${convertUnicode("\uD83D\uDD75\u200D♀")} | Prefix !(Custom)", "${convertUnicode("\uD83E\uDD16")} | Version 0.10", "📡 | (East-Europe)").random()
        discordApi.updateActivity(ActivityType.WATCHING, statusList)
    }

    private fun autoCache() {
        //Prefix Cache
        prefixMap.forEach { (serverID, prefix) -> MariaDB.connection?.prepareStatement("INSERT OR REPLACE INTO customPrefixes(serverID, prefix) VALUES($serverID, $prefix)")?.execute() }

        //Ticket Cache
        ticketMap.forEach { (serverID, ticketChannel) -> MariaDB.connection?.prepareStatement("INSERT INTO REPLACE INTO ticketSystem(serverID, ticketChannelID, ticketMessage) VALUES($serverID, ${ticketChannel.channelID}, ${ticketChannel.ticketMessage})")?.execute() }

        //Welcome Cache
        welcomeMap.forEach { (serverID, welcomeChannel) -> MariaDB.connection?.prepareStatement("INSERT OR REPLACE INTO welcomeSystems(serverID, welcomeChannelID, welcomeMessage, userNameAllowed, memberCountAllowed) VALUES($serverID, ${welcomeChannel.channelID}, ${welcomeChannel.welcomeMessage}, ${welcomeChannel.userNamesAllowed}, ${welcomeChannel.memberCountAllowed})")?.execute() }

        //Moderation Cache


        //Module Cache
        moderationModule.forEach { serverID -> MariaDB.connection?.prepareStatement("INSERT OR REPLACE INTO moderationModule(serverID) VALUES($serverID)")?.execute() }
        ticketModule.forEach { serverID -> MariaDB.connection?.prepareStatement("INSERT OR REPLACE INTO ticketModule(serverID) VALUES($serverID)")?.execute() }
        welcomeModule.forEach { serverID -> MariaDB.connection?.prepareStatement("INSERT INTO REPLACE INTO welcomeModule(serverID) VALUES($serverID)")?.execute() }
    }

    fun convertUnicode(unicodeID: String) : String {
        return EmojiParser.parseToUnicode(unicodeID)
    }
}

fun main() {
    MariaDB.connect()
    ClubyDiscordBot
}