package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.database.MariaDB
import com.github.russiadiktatorv2.clubybot.management.database.MariaDB.onQuery
import java.sql.SQLException

object CacheManager {

    //List
    val moderationModule = mutableListOf<Long>()
    val ticketModule = mutableListOf<Long>()
    val welcomeModule = mutableListOf<Long>()
    val devList = mutableListOf<Long>()

    //Maps
    val prefixMap = mutableMapOf<Long, String>()
    val welcomeMap = mutableMapOf<Long, WelcomeSystem>()
    val ticketMap = mutableMapOf<Long, TicketSystem>()

    fun loadClubyCache() {
        loadPrefixCache();loadWelcomeSystemCache();loadModerationModuleCache();loadTicketModuleCache();loadWelcomeModuleCache()
    }

    private fun loadPrefixCache() {
        val prefixResultSet = onQuery("SELECT * FROM customPrefixes")

        try {
            if (prefixResultSet != null) {
                while (prefixResultSet.next()) {
                    val guildID = prefixResultSet.getLong("serverID")
                    val prefix = prefixResultSet.getString("prefix")

                    prefixMap[guildID] = prefix
                }
                prefixResultSet.close()
            }
        } catch (exception: SQLException) {
            exception.errorCode
        }
    }

    private fun loadWelcomeSystemCache() {
        val resultSet = onQuery("SELECT * FROM welcomeSystems")

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

    private fun loadModerationModuleCache() {
        val moderationModulResultSet = onQuery("SELECT * FROM moderationModule")

        if (moderationModulResultSet != null) {
            while (moderationModulResultSet.next()) {
                moderationModule.add(moderationModulResultSet.getLong("serverID"))
            }
            moderationModulResultSet.close()
        }
    }

    private fun loadTicketModuleCache() {
        val ticketModulResultSet = onQuery("SELECT * FROM ticketModule")

        if (ticketModulResultSet != null) {
            while (ticketModulResultSet.next()) {
                moderationModule.add(ticketModulResultSet.getLong("serverID"))
            }
            ticketModulResultSet.close()
        }
    }

    private fun loadWelcomeModuleCache() {
        val welcomeModulResultSet = onQuery("SELECT * FROM welcomeModule")

        if (welcomeModulResultSet != null) {
            while (welcomeModulResultSet.next()) {
                moderationModule.add(welcomeModulResultSet.getLong("serverID"))
            }
            welcomeModulResultSet.close()
        }
    }
}
