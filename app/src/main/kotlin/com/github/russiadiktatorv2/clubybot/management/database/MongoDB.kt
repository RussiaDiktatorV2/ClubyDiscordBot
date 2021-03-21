package com.github.russiadiktatorv2.clubybot.management.database

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.mongoClient
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.moderationModule
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.ticketModule
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeMap
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.welcomeModule
import org.bson.Document

object MongoDB {

    fun addDocuments() {
        for (prefixDocuments in mongoClient.getDatabase("ClubyDatabase").getCollection("PrefixSystem").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("PrefixSystem").deleteMany(prefixDocuments)
        }
        for (welcomeDocuments in mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeSystem").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("PrefixSystem").deleteMany(welcomeDocuments)
        }
        for (ticketDocuments in mongoClient.getDatabase("ClubyDatabase").getCollection("TicketSystem").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("TicketSystem").deleteMany(ticketDocuments)
        }
        for (moderationModule in mongoClient.getDatabase("ClubyDatabase").getCollection("ModerationModule").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("ModerationModule").deleteMany(moderationModule)
        }
        for (welcomeModule in mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeModule").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeModule").deleteMany(welcomeModule)
        }
        for (ticketModule in mongoClient.getDatabase("ClubyDatabase").getCollection("TicketModule").find()) {
            mongoClient.getDatabase("ClubyDatabase").getCollection("TicketModule").deleteMany(ticketModule)
        }

        prefixMap.forEach { (serverID, prefix) ->
            mongoClient.getDatabase("ClubyDatabase").getCollection("PrefixSystem")
                .insertOne(Document().append("ServerID", serverID).append("Prefix", prefix))
        }

        welcomeMap.forEach { (serverID, welcomeSystem) ->
            mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeSystem").insertOne(
                Document().append("ServerID", serverID).append("ChannelID", welcomeSystem.channelID)
                    .append("WelcomeMessage", welcomeSystem.welcomeMessage)
                    .append("UserNameAllowed", welcomeSystem.userNamesAllowed)
                    .append("MemberCountAllowed", welcomeSystem.memberCountAllowed)
            )
        }
        ticketMap.forEach { (channelID, ticketSystem) ->
            mongoClient.getDatabase("ClubyDatabase").getCollection("TicketSystem").insertOne(
                Document().append("ChannelID", channelID).append("TicketName", ticketSystem.channelName)
                    .append("TicketMessage", ticketSystem.ticketMessage).append("RoleIDs", ticketSystem.roleIDs)
            )
        }
        moderationModule.forEach { serverID -> mongoClient.getDatabase("ClubyDatabase").getCollection("ModerationModule").insertOne(Document().append("ServerID", serverID)) }
        welcomeModule.forEach { serverID -> mongoClient.getDatabase("ClubyDatabase").getCollection("WelcomeModule").insertOne(Document().append("ServerID", serverID)) }
        ticketModule.forEach { serverID -> mongoClient.getDatabase("ClubyDatabase").getCollection("TicketModule").insertOne(Document().append("ServerID", serverID)) }
    }
}
