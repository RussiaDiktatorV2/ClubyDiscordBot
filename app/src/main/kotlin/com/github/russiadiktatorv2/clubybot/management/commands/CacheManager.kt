package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.mongoClient
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem

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

    val ticketsMap = mutableMapOf<Long, Long>()

    fun loadClubyCache() {
        loadPrefixCache();loadWelcomeSystemCache();loadTicketCache();loadModuleCache()
    }

    private fun loadPrefixCache() {
        val prefixDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("PrefixSystem").find()
        for (documents in prefixDocuments) {
            val serverID: Long = documents.getLong("ServerID")
            val prefix: String = documents.getString("Prefix")

            prefixMap[serverID] = prefix
        }
    }

    private fun loadWelcomeSystemCache() {
        val welcomeDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeSystem").find()
        for (documents in welcomeDocuments) {
            val serverID = documents.getLong("ServerID")
            val channelID = documents.getLong("ChannelID")
            val welcomeMessage = documents.getString("WelcomeMessage")
            val userNameAllowed = documents.getBoolean("UserNameAllowed")
            val memberCountAllowed = documents.getBoolean("MemberCountAllowed")

            val welcomeSystem = WelcomeSystem(channelID, welcomeMessage, userNameAllowed, memberCountAllowed)
            welcomeMap[serverID] = welcomeSystem
        }
    }

    private fun loadTicketCache() {
        val ticketDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("TicketSystem").find()
        for (documents in ticketDocuments) {
            val channelID = documents.getLong("ChannelID")
            val channelName = documents.getString("TicketName")
            val ticketMessage = documents.getString("TicketMessage")
            val roleIDs: List<Long> = documents["RoleIDs"] as List<Long>
            val ticketSystem = TicketSystem(channelName, ticketMessage, roleIDs)
            ticketMap[channelID] = ticketSystem
        }
    }

    private fun loadModuleCache() {
        val moderationModuleDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("ModerationModule").find()
        val welcomeModuleDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeModule").find()
        val ticketModuleDocuments = mongoClient.getDatabase("ClubyDatabase").getCollection("TicketModule").find()
        for (documents in moderationModuleDocuments) {
            val serverID = documents.getLong("ServerID")
            moderationModule.add(serverID)
        }
        for (documents in welcomeModuleDocuments) {
            val serverID = documents.getLong("ServerID")
            welcomeModule.add(serverID)
        }
        for (documents in ticketModuleDocuments) {
            val serverID = documents.getLong("ServerID")
            ticketModule.add(serverID)
        }
    }
}