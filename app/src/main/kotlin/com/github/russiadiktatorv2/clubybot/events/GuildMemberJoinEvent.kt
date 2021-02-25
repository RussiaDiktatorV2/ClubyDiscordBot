package com.github.russiadiktatorv2.clubybot.events

import com.github.russiadiktatorv2.clubybot.management.commands.CacheManager
import com.github.russiadiktatorv2.clubybot.management.commands.data.WelcomeSystem
import com.github.russiadiktatorv2.clubybot.management.commands.handling.createEmbed
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.user.User
import org.javacord.api.event.server.member.ServerMemberJoinEvent
import org.javacord.api.listener.server.member.ServerMemberJoinListener
import org.javacord.api.util.logging.ExceptionLogger
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
import java.lang.RuntimeException
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.roundToInt

class GuildMemberJoinEvent : ServerMemberJoinListener {

    override fun onServerMemberJoin(event: ServerMemberJoinEvent) {
        if (event.user.isBot.not()) {
            var welcomeChannel: WelcomeSystem?
            if (CacheManager.welcomeMap[event.server.id].also { welcomeChannel = it } != null) {
                event.user.avatar.asBufferedImage().thenAccept { avatar ->
                    try {
                        val image: BufferedImage = ImageIO.read(URI.create("https://media.discordapp.net/attachments/739079625413492767/800001453149388840/Background.png").toURL())
                        val g: Graphics = image.graphics
                        try {
                            if (g is Graphics2D) {
                                g.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY)
                                g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
                                g.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY)
                                g.setRenderingHint(KEY_DITHERING, VALUE_DITHER_ENABLE)
                                g.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)
                                g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
                                g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY)
                            }
                            val width = image.width
                            val height = image.height

                            g.color = Color(0x2c2f33)
                            g.drawRect(0, 0, width - 1, height - 1)

                            if (welcomeChannel?.userNamesAllowed == true && welcomeChannel?.memberCountAllowed == true) {
                                val font = Font("sans-serif", Font.BOLD, 38)

                                g.color = Color.GRAY
                                g.font = font
                                g.font = font.deriveFont(57f)
                                val fm = g.fontMetrics
                                val textWidth = fm.stringWidth(event.user.discriminatedName)

                                g.drawString(event.user.discriminatedName, ((width / 2) - (textWidth / 2)), (height / 1.08f).roundToInt())
                                g.color = Color.GRAY
                                g.font = font.deriveFont(60f)
                                g.drawString("MEMBER", width / 36.6f.roundToInt(), height / 1.5f.roundToInt())
                                g.color = Color.GRAY
                                g.font = font.deriveFont(60f)
                                g.drawString("#${event.server.members.filter { user: User -> user.isBot.not()}.count()}", (width / 1.2f).roundToInt(), (height / 1.99f).roundToInt())
                                g.clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                g.drawImage(avatar, 371, 100, 249, 249,null)

                            } else if (welcomeChannel?.userNamesAllowed == true) {
                                val font = Font("sans-serif", Font.BOLD, 38)
                                g.color = Color.GRAY
                                g.font = font
                                g.font = font.deriveFont(57f)
                                val fm = g.fontMetrics
                                val textWidth = fm.stringWidth(event.user.discriminatedName)
                                g.drawString(event.user.discriminatedName, ((width / 2) - (textWidth / 2)), (height / 1.08f).roundToInt())
                                g.clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                g.drawImage(avatar, 371, 100, 249, 249,null)

                            } else if (welcomeChannel?.memberCountAllowed == true) {
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
                            } else if (welcomeChannel?.userNamesAllowed!!.not() && welcomeChannel!!.memberCountAllowed!!.not()) {
                                g.clip = Ellipse2D.Float(371F,  100F, 249F, 249F)
                                g.drawImage(avatar, 371, 100, 249, 249,null)
                            }

                        } finally {
                            g.dispose()
                        }
                        val baos = ByteArrayOutputStream()
                        ImageIO.write(image, "png", baos)
                        welcomeChannel!!.channelID?.let {
                            event.server.getTextChannelById(it).get().sendMessage(createEmbed {
                                setDescription(welcomeChannel!!.welcomeMessage)
                                setImage(ByteArrayInputStream(baos.toByteArray()), "WelcomeImage.png")
                            })
                        }
                    } catch (exception: IOException) {
                        throw RuntimeException(exception)
                    }

                }
            }
        }
    }
}