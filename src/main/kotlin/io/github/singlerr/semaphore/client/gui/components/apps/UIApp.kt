package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.pixels
import io.github.singlerr.semaphore.client.gui.components.UINavigable
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator

open class UIApp(override val navigator: GuiNavigator) : UIComponent(), UINavigable {

    override val onShow: UIComponent.() -> Unit = {}

    override val onHide: UIComponent.() -> Unit = {
        hide(true)
        navigator.last()?.let {
            if (it is UINavigable) {
                it.onShow.invoke(it)
            }
        }
    }

    init {
        animateAfterUnhide { introAnimation() }
        animateBeforeHide { outroAnimation() }
    }

    private fun introAnimation() {
        val originalWidth = this@UIApp.constraints.width
        val originalHeight = this@UIApp.constraints.height

        setWidth(0.pixels())
        setHeight(0.pixels())
        animate {
            setWidthAnimation(Animations.LINEAR, 0.1f, originalWidth)
            setHeightAnimation(Animations.LINEAR, 0.1f, originalHeight)
        }
    }

    private fun outroAnimation() {
        animate {
            setWidthAnimation(Animations.LINEAR, 0.1f, 0.pixels())
            setHeightAnimation(Animations.LINEAR, 0.1f, 0.pixels())
        }
    }

    open fun open() {
        navigator.push(this)
    }

    open fun exit(): UIComponent? = navigator.pop()
}
