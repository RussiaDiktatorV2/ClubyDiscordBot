package com.github.russiadiktatorv2.clubybot.extensions

import com.github.russiadiktatorv2.clubybot.core.ClubyDiscordBot
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.Message
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

fun CompletableFuture<Message>.deleteAfter(api: DiscordApi, amount: Long, unit: TimeUnit) {
    this.thenAcceptAsync { api.threadPool.scheduler.schedule({ it.delete() }, amount, unit) }
}