package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.client.gui.getInternalBackground
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.client.gui.widgets.getLocalPlayerUUID
import io.github.singlerr.semaphore.client.gui.widgets.loadResource

class UIAppContainer(
    override val navigator: GuiNavigator,
    apps: List<UIAppIcon>,
    unit: Int = 3,
) : UIComponent(), UINavigable, UIInteractor {

    override val onShow: UIComponent.() -> Unit
    override val onHide: UIComponent.() -> Unit

    init {

        val playerBackground = getInternalBackground(getLocalPlayerUUID()!!)
        loadResource(playerBackground)?.let {
            UIResourceImage(playerBackground).constrain {
                x = CenterConstraint()
                y = CenterConstraint()

                width = 100.percent()
                height = 100.percent()
            } childOf this
        }

        val verticalList =
            ScrollComponent().constrain {
                x = 0.pixels()
                y = 0.pixels()

                width = 100.percent()
                height = 100.percent()
            } childOf this

        var horizontalList: UIContainer? = null

        for ((i, icon) in apps.withIndex()) {
            if (i % unit == 0) {
                horizontalList = buildHorizontalContainer(verticalList)
            }

            icon.constrain {
                x = SiblingConstraint(padding = 2f) + 2.pixels()
                width = 25.pixels()
                height = 25.pixels()
            } childOf horizontalList!!
        }
        onShow = { verticalList.unhide() }
        onHide = { verticalList.hide() }
    }

    private fun buildHorizontalContainer(parent: UIComponent): UIContainer =
        UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint(padding = 2f) + 5.pixels()
            width = 100.percent()
            height = 25.pixels()
        } childOf parent
}
