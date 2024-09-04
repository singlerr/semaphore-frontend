package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import io.github.singlerr.semaphore.client.gui.BMJUA
import io.github.singlerr.semaphore.client.gui.widgets.Box
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import java.awt.Color
import net.minecraft.util.ResourceLocation

open class UIAppIcon(
    resourceLocation: ResourceLocation,
    iconName: State<String>,
    cutRange: Box? = null,
) : UIBlock() {
    init {
        onMouseEnter {
            animate { setYAnimation(Animations.OUT_CUBIC, 0.3f, 2.pixels(alignOpposite = true)) }
        }
        onMouseLeave { animate { setYAnimation(Animations.OUT_QUAD, 0.3f, 2.pixels()) } }
        val iconImage =
            UIResourceImage(resourceLocation, cutRange).constrain {
                x = CenterConstraint()
                y = CenterConstraint()

                width = 30.pixels()
                height = ImageAspectConstraint()
            } childOf this

        UIText(iconName, shadow = BasicState(false)).constrain {
            x = CenterConstraint()
            y = (-3).pixels(alignOpposite = true) boundTo iconImage

            width = 20.pixels()
            height = 5.pixels()
            fontProvider = BMJUA

            color = Color.BLACK.toConstraint()
        } childOf this
    }
}
