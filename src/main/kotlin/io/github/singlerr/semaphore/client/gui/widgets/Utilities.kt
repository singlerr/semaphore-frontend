package io.github.singlerr.semaphore.client.gui.widgets

import com.mojang.authlib.GameProfile
import gg.essential.elementa.UIComponent
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.gui.GuiPhoneScreen
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.UUID
import javax.imageio.ImageIO
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.util.ResourceLocation

private val cache: MutableMap<ResourceLocation, BufferedImage> = mutableMapOf()

fun Color.multiply(multiplier: Float): Color =
    Color(
        (red * multiplier).toInt(),
        (green * multiplier).toInt(),
        (blue * multiplier).toInt(),
        (alpha * multiplier).toInt()
    )

fun UIComponent.defaultConstraint(): UIComponent {
    constrain {
        x = 4.pixels()
        y = 20.pixels()

        width = 100.percent() - 7.pixels()
        height = 100.percent() - 40.pixels()
    }
    return this
}

fun UIComponent.defaultConstraint(parent: UIComponent): UIComponent {
    constrain {
        x = 4.pixels() boundTo parent
        y = 20.pixels() boundTo parent

        width = 100.percent() - 7.pixels()
        height = 100.percent() - 40.pixels()
    }
    return this
}

fun loadResource(resourceLocation: ResourceLocation, cut: Box? = null): BufferedImage =
    cache.getOrPut(resourceLocation) {
        val image = ImageIO.read(loadResource(resourceLocation))
        return cut?.let { box -> image.getSubimage(box.x, box.y, box.width, box.height) } ?: image
    }

fun loadResource(resourceLocation: ResourceLocation): InputStream? =
    try {
        UMinecraft.getMinecraft().resourceManager.getResource(resourceLocation).inputStream
    } catch (_: FileNotFoundException) {
        GuiPhoneScreen::class.java.getResourceAsStream(resourceLocation.toResourcePath())
    }

fun getPlayerSkin(id: UUID): ResourceLocation {
    val con = UMinecraft.getMinecraft().connection
    return con?.getPlayerInfo(id)?.run {
        return locationSkin
    }
        ?: DefaultPlayerSkin.getDefaultSkin(id)
}

fun getLocalPlayerUUID(): UUID? = /*UMinecraft.getMinecraft().player?.uniqueID*/
    UUID.fromString("2ca29846-d6ff-4018-bc30-5d10ea0124cd")

fun getPlayerProfile(id: UUID): GameProfile? =
    UMinecraft.getMinecraft().connection?.getPlayerInfo(id)?.gameProfile

fun ResourceLocation.toResourcePath(): String {
    return "/assets/${namespace}/${path}"
}

fun gcd(a: Int, b: Int): Int {
    var at = a
    var bt = b
    while (bt != 0) {
        at = bt.also { bt = at % bt }
    }

    return at
}

fun lcm(a: Int, b: Int): Int {
    return (a * b / gcd(a, b))
}

fun clamp(num: Float, min: Float, max: Float): Float {
    if (num > max) return max
    if (num < min) return min
    return num
}

fun lcmm(v: IntArray): Int {
    if (v.size == 2) {
        return lcm(v[0], v[1])
    } else {
        val v1 = v[0]
        val newV = v.slice(1 until v.size).toIntArray()
        return lcmm(intArrayOf(v1, lcmm(newV)))
    }
}

data class Box(val x: Int, val y: Int, val width: Int, val height: Int)

class GuiNavigator(
    private val history: ArrayDeque<UIComponent> = ArrayDeque(),
    private val onPageChange: (old: UIComponent?, new: UIComponent?) -> Unit = { _, _ -> }
) {

    fun push(page: UIComponent) {
        val old = history.lastOrNull()
        onPageChange(old, page)
        history.addLast(page)
    }

    fun last(): UIComponent? = history.lastOrNull()

    fun pop(): UIComponent? = history.removeLastOrNull().also { onPageChange(it, null) }

    val pages
        get() = history.toList()
}
