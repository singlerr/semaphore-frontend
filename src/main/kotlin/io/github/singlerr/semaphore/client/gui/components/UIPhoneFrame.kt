package io.github.singlerr.semaphore.client.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.ConfigHolder
import io.github.singlerr.semaphore.client.gui.IMAGE_BACKGROUND
import io.github.singlerr.semaphore.client.gui.components.apps.*
import io.github.singlerr.semaphore.client.gui.widgets.*
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.client.sounds.SoundResource
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest
import java.awt.Color

class UIPhoneFrame(
    override val navigator: GuiNavigator,
    callRequestController: CallRequestController,
    private val callResponseController: CallResponseController,
    entityController: EntityController,
    private val callStateController: CallStateController,
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
                    apps =
                        appList(
                            navigator,
                            callRequestController,
                            entityController,
                            config,
                            callStateController
                        )
                )
                .defaultConstraint() childOf this

        val backButton =
            UIBlock(Color(0, 0, 0, 0))
                .constrain {
                    x = 5.pixels(alignOpposite = true)
                    y = 5.pixels(alignOpposite = true)

                    width = 25.pixels()
                    height = 15.pixels()
                }
                .onMouseClick {
                    if (
                        navigator.last() !is UIPhoneFrame &&
                            navigator.last() !is AppCallReceiver &&
                            navigator.last() !is AppCallRequesting &&
                            navigator.last() !is AppCall
                    )
                        navigator.pop()
                } childOf this

        val homeButton =
            UIBlock(Color(0, 0, 0, 0))
                .constrain {
                    x = 35.pixels(alignOpposite = true)
                    y = 5.pixels(alignOpposite = true)
                    width = 35.pixels()
                    height = 15.pixels()
                }
                .onMouseClick {
                    if (
                        navigator.last() !is AppCallReceiver &&
                            navigator.last() !is AppCallRequesting &&
                            navigator.last() !is AppCall
                    ) {
                        while (navigator.last() !is UIPhoneFrame) {
                            navigator.pop()
                        }
                    }
                } childOf this

        onHide = { appContainer.hide(true) }
        onShow = { appContainer.unhide(true) }
        onMouseClick {
            SoundPlayerAccess.getInstance()
                .playSound(SoundResource.INTERACTION, 1.0f, 1.0f, false, true)
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
                        request!!.callerId,
                        getPlayerProfile(request.callerId)?.name!!
                    ),
                callResponseController = callResponseController,
                callStateController = callStateController,
                fullConstraint = navigator.last() !is AppAddressBook
            )
        )
    }

    override fun present(entity: CallResponse?) {}

    override fun error(entity: Error?) {}

    override fun shouldPresent(request: InverseCallRequest?): Boolean =
        request?.calleeId == UMinecraft.getMinecraft().player.uniqueID

    override fun shouldPresent(entities: List<PresentableEntity>?): Boolean {
        return true
    }

    override fun shouldPresent(entity: CallResponse?): Boolean {
        return true
    }

    override fun shouldPresent(
        entity: io.github.singlerr.semaphore.interactors.caller.presenter.data.Error?
    ): Boolean {
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
            (
                GuiNavigator,
                CallRequestController,
                EntityController,
                ConfigHolder,
                CallStateController
            ) -> List<UIAppIcon> =
            { navigator, reqController, entityController, config, callStateController ->
                listOf(
                    IconAddressBook(navigator, reqController, callStateController),
                    IconSettings(navigator, config),
                    IconSystemSettings(navigator, config),
                    IconUserRegistration(navigator, entityController)
                )
            }
    }
}
