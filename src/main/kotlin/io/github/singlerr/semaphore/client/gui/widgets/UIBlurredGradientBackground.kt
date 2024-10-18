package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.elementa.UIComponent
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.shader.BlendState
import gg.essential.universal.shader.UShader
import io.github.singlerr.semaphore.client.gui.SHADER_BLURRED_GRADIENT
import io.github.singlerr.semaphore.client.gui.SHADER_PASS_THROUGH
import io.github.singlerr.semaphore.client.gui.shaders.GlShader
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.IllegalStateException
import java.security.SecureRandom
import kotlin.math.*

class UIBlurredGradientBackground(private val delta: Float) : UIComponent() {

    private val quadDeltas: MutableList<RepeatingDelta>
    private val quadColors: MutableList<MutableColor>
    private val colorAppliers: MutableList<(Triple<Int, Int, Int>, MutableColor) -> Unit>
    private val random = SecureRandom()

    init {
        val templates: MutableList<(Triple<Int, Int, Int>, MutableColor) -> Unit> =
            mutableListOf(
                { rgb, color -> color.updateFromRGB(rgb.first, rgb.second, rgb.third) },
                { rgb, color -> color.updateFromRGB(rgb.first, rgb.third, rgb.second) },
                { rgb, color -> color.updateFromRGB(rgb.first, rgb.third, rgb.second) },
                { rgb, color -> color.updateFromRGB(rgb.first, rgb.second, rgb.third) }
            )
        templates.shuffle()
        colorAppliers = ArrayList(4)
        quadColors = ArrayList(4)
        quadDeltas = ArrayList(4)
        // 3pi / 2 * 1 / period + transition *
        val candidates = (1..4).map { Math.random() }.toMutableList()
        (0 until 4).forEach { _ ->
            colorAppliers.add(templates.removeLast())
            val n = candidates.removeLast()
            val transition = (n * 10).toInt()
            quadColors.add(MutableColor.fromRGB(0, random.nextInt(255), random.nextInt(255)))
            val period = n.toFloat() * 10
            quadDeltas.add(
                RepeatingDelta(
                    initialValue = 0f,
                    transition = transition,
                    delta = delta,
                    cutOff = 2f / period,
                    period = period
                )
            )
        }
    }

    private fun updateDelta() {
        quadDeltas.forEach(RepeatingDelta::updateDelta)
    }

    private fun compute(x: Float): Float = (0.5f * sin(x) + 0.5f)
    private fun computeHue(x: Float): Float = 360f * compute(x)
    private fun computeSaturate(x: Float): Float = compute(x)

    private fun computeR(x: Float): Float = 255f * compute(x)
    private fun computeG(x: Float): Float = 255f * compute(x)
    private fun computeB(x: Float): Float = 255f * compute(x)
    private fun colorStep(x: Int): Int = min(230, max(x, 100))

    private fun updateColor() {
        updateDelta()
        quadColors.forEachIndexed { index, mutableColor ->
            val delta = quadDeltas[index]
            val x = delta.computeX()
            colorAppliers[index](Triple(0, colorStep((computeR(x)).toInt()), 255), mutableColor)
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDraw(matrixStack)
        updateColor()
        val x = constraints.getX()
        val y = constraints.getY()
        val width = constraints.getWidth()
        val height = constraints.getHeight()
        drawBackground(
            matrixStack,
            box = Box(x.toInt(), y.toInt(), width.toInt(), height.toInt()),
            c0 = quadColors[0].toColor(),
            c1 = quadColors[1].toColor(),
            c2 = quadColors[2].toColor(),
            c3 = quadColors[3].toColor()
        )
        super.draw(matrixStack)
    }

    companion object {
        private lateinit var shader: UShader
        private fun initShaders() {
            if (::shader.isInitialized) return
            shader =
                GlShader(
                    loadResource(SHADER_PASS_THROUGH)?.bufferedReader()?.readText()
                        ?: throw IllegalStateException(),
                    loadResource(SHADER_BLURRED_GRADIENT)?.bufferedReader()?.readText()
                        ?: throw IllegalStateException(),
                    BlendState.NORMAL
                ) {}
            if (!shader.usable) {
                println("Failed to load BlurredGradient Shader")
                return
            }
        }

        private fun drawBackground(
            matrixStack: UMatrixStack,
            box: Box,
            c0: Color,
            c1: Color,
            c2: Color,
            c3: Color
        ) {
            if (!::shader.isInitialized || !shader.usable) {
                initShaders()
                return
            }
            shader.bind()
            UGraphics.enableBlend()
            UGraphics.disableAlpha()
            UGraphics.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
            )
            UGraphics.shadeModel(GL11.GL_SMOOTH)

            val buffer = UGraphics.getFromTessellator()
            buffer.beginWithActiveShader(
                UGraphics.DrawMode.QUADS,
                UGraphics.CommonVertexFormats.POSITION_COLOR
            )

            buffer
                .pos(matrixStack, box.x.toDouble(), box.y.toDouble(), 0.0)
                .color(c0.red, c0.green, c0.blue, c0.alpha)
                .endVertex()
            buffer
                .pos(matrixStack, box.x.toDouble(), box.y.toDouble() + box.height, 0.0)
                .color(c1.red, c1.green, c1.blue, c1.alpha)
                .endVertex()
            buffer
                .pos(matrixStack, box.x.toDouble() + box.width, box.y.toDouble() + box.height, 0.0)
                .color(c2.red, c2.green, c2.blue, c2.alpha)
                .endVertex()
            buffer
                .pos(matrixStack, box.x.toDouble() + box.width, box.y.toDouble(), 0.0)
                .color(c3.red, c3.green, c3.blue, c3.alpha)
                .endVertex()

            buffer.drawDirect()
            UGraphics.shadeModel(GL11.GL_FLAT)
            UGraphics.disableBlend()
            UGraphics.enableAlpha()
            shader.unbind()
        }
    }
}

internal class RepeatingDelta(
    val initialValue: Float,
    private var x: Float = initialValue,
    val transition: Int,
    private val cutOff: Float,
    private val delta: Float,
    private val period: Float
) {

    fun computeX(): Float = period * Math.PI.toFloat() * (x - transition)
    fun updateDelta() {
        x += delta
        if (x >= cutOff) {
            x = initialValue
        }
    }
}

internal class MutableColor(var red: Int, var green: Int, var blue: Int) {

    fun toColor(): Color = Color(red, green, blue, 255)

    fun updateFromHSV(h: Float, s: Float, v: Float) {
        val c = Color.getHSBColor(h, s, v)
        this.red = c.red
        this.green = c.green
        this.blue = c.blue
    }

    fun updateFromRGB(r: Int, g: Int, b: Int) {
        this.red = r
        this.green = g
        this.blue = b
    }

    companion object {
        fun fromHSV(h: Float, s: Float, v: Float): MutableColor {
            val c = Color.getHSBColor(h, s, v)
            return MutableColor(c.red, c.green, c.blue)
        }

        fun fromRGB(r: Int, g: Int, b: Int): MutableColor = MutableColor(r, g, b)
    }
}
