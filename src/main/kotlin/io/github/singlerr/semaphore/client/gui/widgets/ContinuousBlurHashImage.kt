package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.elementa.components.image.BlurHashImage
import gg.essential.elementa.utils.decodeBlurHash
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.utils.ReleasedDynamicTexture
import java.awt.Color
import java.security.SecureRandom
import kotlin.math.abs
import org.lwjgl.opengl.GL11

class ContinuousBlurHashImage : BlurHashImage("") {

    private var dimensions = BASE_WIDTH to BASE_HEIGHT

    private lateinit var texture: ReleasedDynamicTexture

    private var hashIndex: Int = 0
    private var hashComponent: Int = 0
    private var hash: String = ""

    private var partialTicks = 0

    init {
        val random = SecureRandom()
        (0 until HASH_SIZE).forEach { i ->
            val index = random.nextInt(83)
            hash += charMap[index]
        }
    }

    private fun generateTexture(hash: String): ReleasedDynamicTexture {
        return decodeBlurHash(hash, dimensions.first.toInt(), dimensions.second.toInt())?.let {
            UGraphics.getTexture(it)
        }
            ?: run {
                // We encountered an issue decoding the blur hash, it's probably invalid.
                UGraphics.getEmptyTexture()
            }
    }

    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color
    ) {
        if (width > 0 && height > 0) {
            val sizeDifference = abs(dimensions.first * dimensions.second - width * height)

            if (sizeDifference > SIZE_THRESHOLD) {
                dimensions = width to height
            }
        }

        partialTicks++
        if (partialTicks >= PARTIAL_TICK_CUTOFF) {
            partialTicks = 0
            nextHash()
            texture = generateTexture(hash)
        }
        if (::texture.isInitialized && texture.dynamicGlId != -1) {
            drawTexture(matrixStack, texture, color, x, y, width, height)
        }
    }

    fun drawTexture(
        matrixStack: UMatrixStack,
        texture: ReleasedDynamicTexture,
        color: Color,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        textureMinFilter: Int = GL11.GL_NEAREST,
        textureMagFilter: Int = GL11.GL_NEAREST
    ) {
        matrixStack.push()

        UGraphics.enableBlend()
        UGraphics.enableAlpha()
        matrixStack.scale(1f, 1f, 50f)
        val glId = texture.dynamicGlId
        UGraphics.bindTexture(0, glId)
        val red = color.red.toFloat() / 255f
        val green = color.green.toFloat() / 255f
        val blue = color.blue.toFloat() / 255f
        val alpha = color.alpha.toFloat() / 255f
        val worldRenderer = UGraphics.getFromTessellator()
        UGraphics.configureTexture(glId) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureMinFilter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureMagFilter)
        }

        worldRenderer.beginWithDefaultShader(
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR
        )

        worldRenderer
            .pos(matrixStack, x, y + height, 0.0)
            .tex(0.0, 1.0)
            .color(red, green, blue, alpha)
            .endVertex()
        worldRenderer
            .pos(matrixStack, x + width, y + height, 0.0)
            .tex(1.0, 1.0)
            .color(red, green, blue, alpha)
            .endVertex()
        worldRenderer
            .pos(matrixStack, x + width, y, 0.0)
            .tex(1.0, 0.0)
            .color(red, green, blue, alpha)
            .endVertex()
        worldRenderer
            .pos(matrixStack, x, y, 0.0)
            .tex(0.0, 0.0)
            .color(red, green, blue, alpha)
            .endVertex()
        worldRenderer.drawDirect()

        matrixStack.pop()
    }
    private fun setChar(str: String, index: Int, c: Char): String =
        str.substring(0 until index) + c + str.substring(index + 1 until str.length)

    private fun nextHash() {
        if (hashComponent >= MAX_COMPONENT) {
            hashIndex++
            if (hashIndex >= HASH_SIZE) hashIndex = 0

            hashComponent = 0
        }
        hash = setChar(hash, hashIndex, charMap[hashComponent++]!!)
    }
    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw(matrixStack)
        }
        drawImageCompat(matrixStack, x, y, width, height, color)
        super.draw(matrixStack)
    }

    companion object {
        private val PARTIAL_TICK_CUTOFF = 30
        private val HASH_SIZE = 20

        private val MAX_COMPONENT = 83

        private val charMap =
            listOf(
                    '0',
                    '1',
                    '2',
                    '3',
                    '4',
                    '5',
                    '6',
                    '7',
                    '8',
                    '9',
                    'A',
                    'B',
                    'C',
                    'D',
                    'E',
                    'F',
                    'G',
                    'H',
                    'I',
                    'J',
                    'K',
                    'L',
                    'M',
                    'N',
                    'O',
                    'P',
                    'Q',
                    'R',
                    'S',
                    'T',
                    'U',
                    'V',
                    'W',
                    'X',
                    'Y',
                    'Z',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'f',
                    'g',
                    'h',
                    'i',
                    'j',
                    'k',
                    'l',
                    'm',
                    'n',
                    'o',
                    'p',
                    'q',
                    'r',
                    's',
                    't',
                    'u',
                    'v',
                    'w',
                    'x',
                    'y',
                    'z',
                    '#',
                    '$',
                    '%',
                    '*',
                    '+',
                    ',',
                    '-',
                    '.',
                    ':',
                    ';',
                    '=',
                    '?',
                    '@',
                    '[',
                    ']',
                    '^',
                    '_',
                    '{',
                    '|',
                    '}',
                    '~'
                )
                .mapIndexed { i, c -> i to c }
                .toMap()
    }
}
