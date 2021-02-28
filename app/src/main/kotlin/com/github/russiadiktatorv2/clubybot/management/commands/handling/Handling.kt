package com.github.russiadiktatorv2.clubybot.management.commands.handling

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot.convertUnicode
import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager.prefixMap
import org.javacord.api.DiscordApi
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
import java.awt.Color
import java.util.concurrent.TimeUnit

//General handling

fun createEmbed(init: EmbedBuilder.() -> Unit) : EmbedBuilder {
    val embedBuilder = EmbedBuilder()
    embedBuilder.init()
    return embedBuilder
}

fun sendEmbed(textChannel: TextChannel, time: Long, timeUnit: TimeUnit ,init: EmbedBuilder.() -> Unit) : EmbedBuilder {
    val embedBuilder = EmbedBuilder()
    embedBuilder.init()
    textChannel.sendMessage(embedBuilder).thenAccept {
        it.api.threadPool.scheduler.schedule( {it.delete()} , time, timeUnit)
    }
    return embedBuilder
}

fun TextChannel.sendMissingArguments(arguments: String, commandName: String, guild: Server) {
    sendMessage(createEmbed {
        setAuthor("${convertUnicode(":exclamation:")} | » Missing Arguments")
        setDescription("Use `${prefixMap.getOrDefault(guild.id, "!")}$arguments` to execute the `$commandName` command").setFooter("Write the right arguments to execute the following command").setTimestampToNow()
        setColor(Color.decode("0xf2310f"))
    }).thenAcceptAsync {
        it.api.threadPool.scheduler.schedule( {it.delete()}, 20, TimeUnit.SECONDS)
    }
}


//Module command handling

fun Message.sendModuleWasActivateMessage(discordApi: DiscordApi, moduleName: String) {
    edit(EmbedBuilder()
        .setAuthor("» Module selection was closed", null, discordApi.yourself.avatar)
        .setDescription("You enabled the module `$moduleName`")
        .setFooter("${convertUnicode("\uD83D\uDCD5")} | The module system").setTimestampToNow().setColor(Color.decode("0x32ff7e")))
    removeAllReactions()
}

fun Message.sendModuleWasDisabledMessage(discordApi: DiscordApi, moduleName: String) {
    edit(EmbedBuilder()
        .setAuthor("» Module selection was closed", null, discordApi.yourself.avatar)
        .setDescription("You disabled the module `$moduleName`")
        .setFooter("${convertUnicode("\uD83D\uDCD5")} | The module system").setTimestampToNow().setColor(Color.decode("0x32ff7e")))
    removeAllReactions()
}

fun Message.sendModuleIsAlreadyEnabled(discordApi: DiscordApi, moduleName: String) {
    edit(EmbedBuilder().setAuthor("» An Error occurred!", null, discordApi.yourself.avatar)
        .setDescription("The module `$moduleName` is already enabled")
        .setFooter("${convertUnicode("\uD83D\uDCD5")} | The module system").setTimestampToNow().setColor(Color.decode("0xf2310f")))
    removeAllReactions()
}

fun Message.sendModuleIsAlreadyDisabled(discordApi: DiscordApi, moduleName: String) {
    edit(EmbedBuilder().setAuthor("» An Error occurred!", null, discordApi.yourself.avatar)
        .setDescription("The module `$moduleName` is already disabled")
        .setFooter("${convertUnicode("\uD83D\uDCD5")} | The module system").setTimestampToNow().setColor(Color.decode("0xf2310f")))
    removeAllReactions()
}

//---------------------------------------------------------------------------------------------------------------------------

//Prefix command handling

fun TextChannel.sendPrefixWasChanged(discordApi: DiscordApi, currentPrefix: String, newPrefix: String) {
    sendMessage(createEmbed {
        setAuthor("» The Prefix was changed", null, discordApi.yourself.avatar)
        setDescription("You changed the prefix `$currentPrefix` to `$newPrefix`")
        setTimestampToNow().setFooter("${convertUnicode(":exclamation:")} | ${prefixMap.size} guilds use a custom prefix").setColor(Color.decode("0x32ff7e")) }).thenAccept {
        discordApi.threadPool.scheduler.schedule({ it.delete() }, 20, TimeUnit.SECONDS)
    }
}

fun TextChannel.sendPrefixIsSame(discordApi: DiscordApi) {
    sendMessage(createEmbed {
        setAuthor("» Problem to change the prefix", null, discordApi.yourself.avatar)
        setDescription("You can't set the same prefix on you server").setTimestampToNow().setColor(Color.decode("0xf2310f")) }).thenAccept {
        discordApi.threadPool.scheduler.schedule({ it.delete() }, 10, TimeUnit.SECONDS)
    }
}