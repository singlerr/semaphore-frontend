package io.github.singlerr.semaphore.client.gui.widgets

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import java.awt.Color

class UISoundSlider(
    private val min: Int = 0,
    private val max: Int = 100,
    private val defaultValue: Int = 0,
    private val onChange: (Int) -> Unit = {},
    private val barColor: Color = Color.BLUE,
    private val barBackgroundColor: Color = Color.GRAY,
    private val barHandleColor: Color = Color.BLUE,
    private val onBarPressed: () -> Unit,
    private val onBarReleased: () -> Unit
) : UIComponent() {

    private val handle: State<Double> = BasicState(0.0)

    private var isDragging = false
    private var barX: Float = 0f

    private val barWay: UIBlock
    private val barHandle: UIBlock
    private var barGauge: UIBlock

    init {

        barWay =
            UIBlock().constrain {
                x = 5.pixels()
                y = CenterConstraint()

                width = (70.percent() boundTo this@UISoundSlider)
                height = 10.pixels()

                color = barBackgroundColor.toConstraint()
            } childOf this

        barGauge =
            UIBlock().constrain {
                x = 5.pixels()
                y = CenterConstraint()

                width = (map(defaultValue) * 100).percent() boundTo barWay
                height = 10.pixels()

                color = barColor.toConstraint()
            } childOf this

        barHandle =
            UIBlock().constrain {
                x = (map(defaultValue) * 100).percent() boundTo barWay
                y = CenterConstraint()

                width = 10.pixels()
                height = 15.pixels()

                color = barHandleColor.toConstraint()
            } childOf this

        val ghostBarHandle =
            UIBlock().constrain {
                x = (map(defaultValue) * 100).percent() boundTo barWay
                y = CenterConstraint()

                width = 10.pixels()
                height = 15.pixels()

                color = Color(0, 0, 0, 0).toConstraint()
            } childOf this

        ghostBarHandle
            .onMouseClick {
                isDragging = true

                barHandle.animate {
                    setColorAnimation(
                        Animations.LINEAR,
                        0.3f,
                        barHandleColor.multiply(0.7f).toConstraint()
                    )
                }

                onBarPressed()
            }
            .onMouseRelease {
                if (isDragging) {
                    setX(barX.pixels())
                    isDragging = false

                    barHandle.animate {
                        setColorAnimation(Animations.LINEAR, 0.3f, barHandleColor.toConstraint())
                    }
                    onBarReleased()
                }
            }
            .onMouseDrag { mouseX, mouseY, btn ->
                if (!isDragging) return@onMouseDrag

                val max = barWay.getRight() - this@UISoundSlider.getLeft() - barHandle.getWidth()
                val min = barWay.getLeft() - this@UISoundSlider.getLeft()

                var newX = ghostBarHandle.getLeft() + mouseX - this@UISoundSlider.getLeft()
                newX = clamp(newX, min, max)
                handle.set(newX.toDouble() / (max - min).toDouble())
                barX = newX
                barHandle.setX(newX.pixels())
                barGauge.setWidth((barHandle.getLeft() - barWay.getLeft()).pixels())
            }

        handle.onSetValue { v -> onChange(mapToRange(v, min, max)) }
        handle.set(map(defaultValue))
    }

    //    override fun afterInitialization() {
    //        super.afterInitialization()
    //        val max = barWay.getRight() - this@UISoundSlider.getLeft() - barHandle.getWidth()
    //        val min = barWay.getLeft() - this@UISoundSlider.getLeft()
    //
    //        val initialX = mapToRange(map(defaultValue), min, max)
    //        barHandle.setX(initialX.pixels())
    //        barGauge.setWidth((barHandle.getLeft() - barWay.getLeft()).pixels())
    //    }

    private fun mapToRange(p: Double, min: Float, max: Float): Float =
        (min + p * (max - min)).toFloat()

    private fun mapToRange(p: Double, min: Int, max: Int): Int = (min + p * (max - min)).toInt()
    private fun map(p: Int): Double = (p - min) / (max - min).toDouble()
}
