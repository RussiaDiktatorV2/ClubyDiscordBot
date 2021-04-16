package com.github.russiadiktatorv2.clubybot.management.commands.interfaces

import org.javacord.api.entity.permission.PermissionType

interface ICommand {
    val permissions: MutableList<PermissionType>
    val description: String
    val usage: String
}