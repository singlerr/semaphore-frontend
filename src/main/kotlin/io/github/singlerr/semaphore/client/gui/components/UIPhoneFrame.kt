package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.client.ConfigHolder
import io.github.singlerr.semaphore.client.gui.IMAGE_BACKGROUND
import io.github.singlerr.semaphore.client.gui.components.apps.*
import io.github.singlerr.semaphore.client.gui.widgets.*
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest

class UIPhoneFrame(
    override val navigator: GuiNavigator,
    callRequestController: CallRequestController,
    private val callResponseController: CallResponseController,
    entityController: EntityController,
    config: ConfigHolder
) :
    UIResourceImage(IMAGE_BACKGROUND, cutRange = Box(x = 199, y = 0, width = 366, height = 767)),
    UINavigable,
    UIInteractor {

    override val onHide: UIComponent.() -> Unit

    override val onShow: UIComponent.() -> Unit

    init {
        constrain {
            x = 2.pixels()
            y = 2.pixels()

            width = ImageAspectConstraint()
            height = 100.percent() - 10.pixels()
        }

        val appContainer =
            UIAppContainer(
                    navigator = navigator,
                    apps = appList(navigator, callRequestController, entityController, config)
                )
                .defaultConstraint(this@UIPhoneFrame) childOf this

        onHide = { appContainer.hide(true) }
        onShow = {
            appContainer.unhide(true)
            grabWindowFocus()
        }
    }

    override fun presentError(error: ErrorEntity?) {}

    override fun present(entity: PresentableEntity?) {}

    override fun present(entities: MutableList<PresentableEntity>?) {}

    override fun present(request: InverseCallRequest?) {
        navigator.push(
            AppCallReceiver(
                navigator = navigator,
                info =
                    CallerInformation(
                        request!!.callerId(),
                        getPlayerProfile(request.callerId())?.name!!
                    ),
                callResponseController = callResponseController
            )
        )
    }

    override fun present(entity: CallResponse?) {}

    override fun error(entity: Error?) {}

    override fun shouldPresent(request: InverseCallRequest?): Boolean = true

    override fun shouldPresent(entities: List<PresentableEntity>?): Boolean {
        return true
    }

    override fun shouldPresent(entity: CallResponse?): Boolean {
        return true
    }

    override fun shouldPresent(entity: kotlin.Error?): Boolean {
        return true
    }

    override fun shouldPresent(entity: PresentableEntity?): Boolean {
        return true
    }

    override fun shouldPresent(error: ErrorEntity?): Boolean {
        return true
    }

    companion object {
        private val appList:
            (GuiNavigator, CallRequestController, EntityController, config: ConfigHolder) -> List<
                    UIAppIcon
                > =
            { navigator, reqController, entityController, config ->
                listOf(
                    IconAddressBook(navigator, reqController),
                    IconSettings(navigator, config),
                    IconUserRegistration(navigator, entityController)
                )
            }
    }
}
