package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem

object CacheManager {

    //List
    val moderationModule = mutableListOf<Long>()
    val ticketModule = mutableListOf<Long>()
    val welcomeModule = mutableListOf<Long>()
    val devList = mutableListOf<Long>()

    //Maps
    var prefixMap = mutableMapOf<Long, String>()
    val welcomeMap = mutableMapOf<Long, WelcomeSystem>()
}
