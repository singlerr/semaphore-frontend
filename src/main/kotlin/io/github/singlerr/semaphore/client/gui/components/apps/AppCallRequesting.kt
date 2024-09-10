package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.client.gui.BMJUA
import io.github.singlerr.semaphore.client.gui.ICON_REJECT_CALL
import io.github.singlerr.semaphore.client.gui.components.UIInteractor
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.UIBlurredGradientBackground
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.client.gui.widgets.defaultConstraint
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest
import java.util.UUID

class AppCallRequesting(
    navigator: GuiNavigator,
    callerInformation: CallerInformation,
    calleeInformation: CalleeInformation,
    private val callRequestController: CallRequestController,
    private val callStateController: CallStateController
) : UIApp(navigator), UIInteractor {

    override val onShow: UIComponent.() -> Unit = { parent.unhide() }

    private val background: UIComponent

    init {
        defaultConstraint()
        background =
            UIBlurredGradientBackground(delta = 0.0005f).constrain {
                x = 0.pixels()
                y = 0.pixels()

                width = 100.percent()
                height = 100.percent()
            } childOf this

        UIHead(calleeInformation.id).constrain {
            x = CenterConstraint()
            y = 30.pixels()

            width = 30.pixels()
            height = ImageAspectConstraint()
        } childOf this

        UIText(calleeInformation.name, shadow = false).constrain {
            x = CenterConstraint()
            y = 70.pixels()

            width = 45.pixels()
            height = 10.pixels()

            fontProvider = BMJUA
        } childOf this

        UIResourceImage(ICON_REJECT_CALL)
            .constrain {
                x = CenterConstraint()
                y = 30.pixels(alignOpposite = true)

                width = 50.pixels()
                height = ImageAspectConstraint()
            }
            .onMouseClick {
                callRequestController.request(
                    CallRequest(callerInformation.id, calleeInformation.id)
                )

                exit()
            } childOf this
    }

    override fun shouldPresent(entity: Error?): Boolean = true
    override fun presentError(error: ErrorEntity?) {
       exit()
        // Play sound here
    }

    override fun shouldPresent(entity: CallResponse?): Boolean = true
    override fun present(entity: CallResponse?) {
        exit()
        if (entity?.responseType() == CallResponse.ResponseType.ACCEPT) {
            navigator.push(
                AppCall(
                    navigator = navigator,
                    information =
                        CallInformation(
                            opponentId = entity.calleeId(),
                            callerId = entity.callerId(),
                            calleeId = entity.calleeId()
                        ),
                    callStateController = callStateController
                )
            )
        }
    }
}

data class CalleeInformation(val id: UUID, val name: String)
