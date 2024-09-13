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
import io.github.singlerr.semaphore.client.gui.widgets.getPlayerProfile
import io.github.singlerr.semaphore.client.gui.widgets.innerConstraint
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.client.sounds.SoundResource
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error
import java.util.UUID

class AppCall(
    navigator: GuiNavigator,
    private val information: CallInformation,
    callStateController: CallStateController,
    fullConstraint: Boolean = true
) : UIApp(navigator), UIInteractor {

    override val onShow: UIComponent.() -> Unit = { parent.unhide() }
    override val onHide: UIComponent.() -> Unit = {
        super.onHide(this)
        SoundPlayerAccess.getInstance().playSound(SoundResource.CALL_CLOSE, 1.0f, 1.0f, false, true)
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

        UIHead(information.opponentId).constrain {
            x = CenterConstraint()
            y = 30.pixels()

            width = 30.pixels()
            height = ImageAspectConstraint()
        } childOf this

        val name = getPlayerProfile(information.opponentId)?.name
        UIText(name!!, shadow = false).constrain {
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
                callStateController.closeCall(
                    CallStateQuery.CloseCall(information.callerId, information.calleeId)
                )
                exit()
            } childOf this
    }

    override fun shouldPresent(entity: Error?): Boolean = true
    override fun shouldPresent(error: ErrorEntity?): Boolean = true
    override fun presentError(error: ErrorEntity?) {
        error?.let {
            if (it.message.startsWith("error.call.closed")) {
                val infoSection =
                    it.message.substring(
                        it.message.indexOf("error.call.closed") + "error.call.closed".length
                    )
                if (infoSection.isEmpty()) return@let

                val args = infoSection.split("|")
                val callerId = UUID.fromString(args[0])
                val calleeId = UUID.fromString(args[1])

                // Exit only related with me
                if (information.callerId == callerId && information.calleeId == calleeId) {
                    exit()
                    return
                }
            }
        }
        // Play sound here
    }

    override fun shouldPresent(entity: CallResponse?): Boolean = true
    override fun present(entity: CallResponse?) {}
}

data class CallInformation(val opponentId: UUID, val callerId: UUID, val calleeId: UUID)
