package com.github.russiadiktatorv2.clubybot.management.database

import com.github.russiadiktatorv2.clubybot.management.database.MariaDB.onUpdate

object TableManager {

    fun onCreate() {
        onUpdate("CREATE TABLE IF NOT EXISTS moderationModule(serverID BIGINT(19) NOT NULL PRIMARY KEY)")
        onUpdate("CREATE TABLE IF NOT EXISTS welcomeModule(serverID BIGINT(19) NOT NULL PRIMARY KEY)")
        onUpdate("CREATE TABLE IF NOT EXISTS ticketModule(serverID BIGINT(19) NOT NULL PRIMARY KEY)")

        onUpdate("CREATE TABLE IF NOT EXISTS customPrefixes(serverID BIGINT(19) NOT NULL PRIMARY KEY, prefix VARCHAR(7))")
        onUpdate("CREATE TABLE IF NOT EXISTS welcomeSystems(serverID BIGINT(19) NOT NULL PRIMARY KEY, welcomeChannelID BIGINT(19) NOT NULL, welcomeMessage VARCHAR(170), userNameAllowed BOOLEAN, memberCountAllowed BOOLEAN)")
        onUpdate("CREATE TABLE IF NOT EXISTS ticketSystem(serverID BIGINT(19) NOT NULL PRIMARY KEY, ticketChannelID BIGINT(19) NOT NULL, ticketMessage VARCHAR(90))")
    }
}