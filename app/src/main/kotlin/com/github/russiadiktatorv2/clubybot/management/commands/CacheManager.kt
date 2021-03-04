package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.management.commands.data.TicketSystem
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.database.MariaDB.onQuery
import java.sql.SQLException

object CacheManager {

    //List
    val moderationModule = mutableListOf<Long>()
    val ticketModule = mutableListOf<Long>()
    val welcomeModule = mutableListOf<Long>()

    //Maps
    val prefixMap = mutableMapOf<Long, String>()
    val welcomeMap = mutableMapOf<Long, WelcomeSystem>()
    val ticketMap = mutableMapOf<Long, TicketSystem>()


    fun loadCLubyCache() {

        val prefixResultSet = onQuery("SELECT * FROM customPrefixes")
        val welcomeResultSet = onQuery("SELECT * FROM welcomeSystems")

        val moderationModulResultSet = onQuery("SELECT * FROM moderationModule")
        val ticketModulResultSet = onQuery("SELECT * FROM ticketModule")
        val welcomeModulResultSet = onQuery("SELECT * FROM welcomeModule")

        try {
            if (prefixResultSet != null) {

            }
            if (welcomeResultSet != null) {

            }

            if (moderationModulResultSet != null) {

            }
        } catch (exception: SQLException) {
            exception.printStackTrace()
        }
    }
}
