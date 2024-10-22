package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import io.github.singlerr.semaphore.client.gui.FONT
import io.github.singlerr.semaphore.client.gui.ICON_REJECT_CALL
import io.github.singlerr.semaphore.client.gui.components.UIInteractor
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.UIBlurredGradientBackground
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.client.gui.widgets.defaultConstraint
import io.github.singlerr.semaphore.client.gui.widgets.innerConstraint
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.client.sounds.SoundResource
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error
import java.util.UUID

class AppCallRequesting(
    navigator: GuiNavigator,
    callerInformation: CallerInformation,
    calleeInformation: CalleeInformation,
    private val callRequestController: CallRequestController,
    private val callStateController: CallStateController,
    fullConstraint: Boolean = true
) : UIApp(navigator), UIInteractor {

    override val onShow: UIComponent.() -> Unit = {
        parent.unhide()
        SoundPlayerAccess.getInstance()
            .playSound(SoundResource.REQUESTING_CALL, 1.0f, 1.0f, true, true)
    }
    override val onHide: UIComponent.() -> Unit = {
        super.onHide(this)
        SoundPlayerAccess.getInstance().stopSound(SoundResource.REQUESTING_CALL)
        SoundPlayerAccess.getInstance()
            .playSound(SoundResource.CALL_REJECT, 1.0f, 1.0f, false, true)
    }
    private val background: UIComponent

    init {
        if (fullConstraint) {
            defaultConstraint()
        } else {
            innerConstraint()
        }
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

            fontProvider = FONT
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

    override fun present(error: Error?) {
        error?.let {
            if (it.reason.equals("error.call.timeout")) {
                SoundPlayerAccess.getInstance()
                    .playSound(SoundResource.TARGET_UNAVAILABLE, 1.0f, 1.0f, false, false)
            } else if (it.reason.equals("error.target.already.in.call")) {
                SoundPlayerAccess.getInstance().stopSound(SoundResource.REQUESTING_CALL)
                SoundPlayerAccess.getInstance()
                    .playSound(SoundResource.RECEIVING_CALL, 1.0f, 1.0f, false, false)
            }
            navigator.pop()
        }
    }

    override fun shouldPresent(entity: CallResponse?): Boolean = true
    override fun present(entity: CallResponse?) {
        navigator.pop()
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
                    callStateController = callStateController,
                    fullConstraint = false
                )
            )
        } else {
            SoundPlayerAccess.getInstance()
                .playSound(SoundResource.TARGET_UNAVAILABLE, 1.0f, 1.0f, false, false)
        }
    }
}

data class CalleeInformation(val id: UUID, val name: String)
