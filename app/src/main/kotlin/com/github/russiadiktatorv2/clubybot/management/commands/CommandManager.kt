package com.github.russiadiktatorv2.clubybot.management.commands

import com.github.russiadiktatorv2.clubybot.commands.DummyCommand
import com.github.russiadiktatorv2.clubybot.commands.devcommands.ContributorCommand
import com.github.russiadiktatorv2.clubybot.commands.normalcommands.HelpCommand
import com.github.russiadiktatorv2.clubybot.commands.normalcommands.IdCommand
import com.github.russiadiktatorv2.clubybot.commands.devcommands.RestartCommand
import com.github.russiadiktatorv2.clubybot.commands.normalcommands.module.ActivateModule
import com.github.russiadiktatorv2.clubybot.commands.normalcommands.module.DisableModule
import com.github.russiadiktatorv2.clubybot.commands.normalcommands.SetPrefixCommand
import com.github.russiadiktatorv2.clubybot.commands.ticketcommands.CreateTicket
import com.github.russiadiktatorv2.clubybot.commands.ticketcommands.SetTicketSystem
import com.github.russiadiktatorv2.clubybot.commands.welcomescommands.RemoveWelcomeSystem
import com.github.russiadiktatorv2.clubybot.commands.welcomescommands.SetWelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import com.github.russiadiktatorv2.clubybot.management.commands.handling.sendEmbed
import com.github.russiadiktatorv2.clubybot.management.interfaces.CommandEvent
import com.github.russiadiktatorv2.clubybot.management.interfaces.ModerationCommand
import com.github.russiadiktatorv2.clubybot.management.interfaces.TicketCommand
import com.github.russiadiktatorv2.clubybot.management.interfaces.WelcomeCommand
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.TimeUnit

class CommandManager {

    val normalCommands = mutableMapOf<String, CommandEvent>()
    val moderationCommand = mutableMapOf<String, ModerationCommand>()
    val welcomeCommands = mutableMapOf<String, WelcomeCommand>()
    val ticketCommands = mutableMapOf<String, TicketCommand>()

    fun loadClubyCommands(command: String, event: MessageCreateEvent, arguments: List<String>) {
        var commandEvent: CommandEvent?
        var moderationCommand: ModerationCommand?
        var ticketCommand: TicketCommand?
        var welcomeCommand: WelcomeCommand?

        if (event.serverTextChannel.isPresent) {
            if (this.normalCommands[command].also { commandEvent = it } != null) {
                commandEvent?.executeCommand(command, event, arguments)
            }

            else if (this.moderationCommand[command].also { moderationCommand = it } != null) {
                if (CacheManager.moderationModule.contains(event.server.get().id).not()) {
                    moderationCommand?.executeModerationCommands(command, event, arguments)
                }
            }

            else if (this.ticketCommands[command].also { ticketCommand = it } != null) {
                if (CacheManager.ticketModule.contains(event.server.get().id).not()) {
                    ticketCommand?.executeTicketCommands(command, event, arguments)
                } else {
                    sendEmbed(event.serverTextChannel.get(), 15, TimeUnit.SECONDS) {
                        setTitle("❌ Error to execute the following command")
                        setDescription("The server must have activated the module ``ticket`` to execute the ticket commands")
                        setColor(Color.decode("0xf2310f"))
                    }
                }
            }

            else if (this.welcomeCommands[command].also { welcomeCommand = it } != null) {
                if (CacheManager.welcomeModule.contains(event.server.get().id).not()) {
                    welcomeCommand?.executeWelcomeCommands(command, event,arguments)
                }
            }
        }
    }

    init {
        //Normal Commands
        normalCommands["help"] = HelpCommand()

        normalCommands["addmodule"] = ActivateModule()
        normalCommands["removemodule"] = DisableModule()

        normalCommands["prefix"] = SetPrefixCommand()
        normalCommands["getid"] = IdCommand()

        normalCommands["dummy"] = DummyCommand()
        normalCommands["contributor"] = ContributorCommand()
        normalCommands["restart"] = RestartCommand()

        //Moderation Commands

        //Ticket Commands
        ticketCommands["setticket"] = SetTicketSystem()

        ticketCommands["createticket"] = CreateTicket()

        //Welcome Commands
        welcomeCommands["setwelcome"] = SetWelcomeSystem()
        welcomeCommands["removewelcome"] = RemoveWelcomeSystem()
    }
}