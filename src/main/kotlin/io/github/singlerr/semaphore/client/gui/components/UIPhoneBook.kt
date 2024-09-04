package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest

class UIPhoneBook(override val navigator: GuiNavigator) : UIBlock(), UIInteractor, UINavigable {

    override val onShow: UIComponent.() -> Unit = {}
    override val onHide: UIComponent.() -> Unit = {}

    init {}

    override fun presentError(error: ErrorEntity?) {}

    override fun present(entity: PresentableEntity?) {}

    override fun present(entities: MutableList<PresentableEntity>?) {}

    override fun present(request: InverseCallRequest?) {}

    override fun present(entity: CallResponse?) {}

    override fun error(entity: Error?) {}
}
