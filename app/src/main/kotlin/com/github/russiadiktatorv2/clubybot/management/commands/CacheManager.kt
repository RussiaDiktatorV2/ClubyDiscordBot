package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.management.database.PostgreSQL.connection
import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.enums.CommandModule
import java.sql.ResultSet

object CacheManager {

    //Lists
    val moderationModule = mutableListOf<Long>()
    val ticketModule = mutableListOf<Long>()
    val welcomeModule = mutableListOf<Long>()
    val devList = mutableListOf<Long>()
    val tickets = mutableListOf<Long>()

    //Maps
    val prefixMap = mutableMapOf<Long, String>()
    val welcomeMap = mutableMapOf<Long, WelcomeSystem>()
    val ticketMap = mutableMapOf<Long, TicketSystem>()

    fun loadClubyCache() {

    }


    //PostgreSQL Database loads the documents in the specific maps

    private fun loadPrefixCache() {
        val resultSet: ResultSet? = connection?.prepareStatement("")?.executeQuery()
    }
}