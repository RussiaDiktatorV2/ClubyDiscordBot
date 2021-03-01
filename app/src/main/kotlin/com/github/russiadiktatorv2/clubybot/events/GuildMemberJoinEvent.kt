package com.github.russiadiktatorv2.clubybot.events

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.user.User
import org.javacord.api.event.server.member.ServerMemberJoinEvent
import org.javacord.api.listener.server.member.ServerMemberJoinListener
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints.*
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class GuildMemberJoinEvent : ServerMemberJoinListener {

    override fun onServerMemberJoin(event: ServerMemberJoinEvent) {
        if (event.user.isBot.not()) {
            val welcomeChannel: WelcomeSystem? = CacheManager.welcomeMap[event.server.id]
            if (welcomeChannel != null) {
                event.user.avatar.asBufferedImage().thenAccept { avatar ->
                    try {
                        val image: BufferedImage = ImageIO.read(URI.create("https://media.discordapp.net/attachments/739079625413492767/800001453149388840/Background.png").toURL())
                        val g: Graphics = image.graphics
                        try {
                            if (g is Graphics2D) {
                                g.apply {
                                    setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY)
                                    setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
                                    setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY)
                                    setRenderingHint(KEY_DITHERING, VALUE_DITHER_ENABLE)
                                    setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)
                                    setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
                                    setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY)
                                }
                            }
                            val width = image.width
                            val height = image.height

                            g.color = Color(0x2c2f33)
                            g.drawRect(0, 0, width - 1, height - 1)

                            if (welcomeChannel.userNamesAllowed && welcomeChannel.memberCountAllowed) {
                                var font = Font("sans-serif", Font.BOLD, 38)

                                g.color = Color.GRAY
                                g.font = font
                                g.font = font.deriveFont(57f)
                                val fm = g.fontMetrics
                                val textWidth = fm.stringWidth(event.user.discriminatedName)

                                g.apply {
                                    drawString(event.user.discriminatedName, ((width / 2) - (textWidth / 2)), (height / 1.08f).roundToInt())
                                    color = Color.GRAY
                                    font = font.deriveFont(60f)
                                    drawString("MEMBER", width / 36.6f.roundToInt(), height / 1.5f.roundToInt())
                                    color = Color.GRAY
                                    font = font.deriveFont(60f)
                                    drawString("#${event.server.members.filter { user: User -> user.isBot.not()}.count()}", (width / 1.2f).roundToInt(), (height / 2.00f).roundToInt())
                                    clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                    drawImage(avatar, 371, 100, 249, 249,null)
                                }

                            } else if (welcomeChannel.userNamesAllowed) {
                                val font = Font("sans-serif", Font.BOLD, 38)
                                g.color = Color.GRAY
                                g.font = font
                                g.font = font.deriveFont(57f)
                                val fm = g.fontMetrics
                                val textWidth = fm.stringWidth(event.user.discriminatedName)
                                g.drawString(event.user.discriminatedName, ((width / 2) - (textWidth / 2)), (height / 1.08f).roundToInt())
                                g.clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                g.drawImage(avatar, 371, 100, 249, 249,null)

                            } else if (welcomeChannel.memberCountAllowed) {
                                val font = Font("sans-serif", Font.BOLD, 38)
                                g.color = Color.GRAY
                                g.font = font
                                g.font = font.deriveFont(65f)
                                val memberMessage = "MEMBER #${event.server.members.filter { user: User -> user.isBot.not() }.count()}"
                                val fm = g.fontMetrics
                                val textWidth = fm.stringWidth(memberMessage)
                                g.drawString(memberMessage, ((width / 2) - (textWidth / 2)), (height / 1.08f).roundToInt())
                                g.clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                g.drawImage(avatar, 371, 100, 249, 249,null)
                            } else if (welcomeChannel.userNamesAllowed.not() && welcomeChannel.memberCountAllowed.not()) {
                                g.apply {
                                    clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                    drawImage(avatar, 371, 100, 249, 249,null)
                                }
                            }

                        } finally {
                            g.dispose()
                        }
                        val baos = ByteArrayOutputStream()
                        ImageIO.write(image, "png", baos)
                        MessageBuilder().append(welcomeChannel.welcomeMessage).appendNewLine()
                            .setEmbed(createEmbed { setImage(ByteArrayInputStream(baos.toByteArray()), "WelcomeImage.png") })
                            .setTts(false).send(event.server.getTextChannelById(welcomeChannel.channelID).get())
                    } catch (exception: IOException) {
                        throw RuntimeException(exception)
                    }

                }
            }
        }
    }
}