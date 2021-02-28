package com.github.russiadiktatorv2.clubybot.management.commands.data

data class WelcomeSystem(var channelID: Long, var welcomeMessage: String?, var userNamesAllowed: Boolean = false, var memberCountAllowed: Boolean = false)