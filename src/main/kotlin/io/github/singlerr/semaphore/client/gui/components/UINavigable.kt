package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.UIComponent
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator

interface UINavigable {
    val navigator: GuiNavigator

    val onShow: UIComponent.() -> Unit
    val onHide: UIComponent.() -> Unit
}
