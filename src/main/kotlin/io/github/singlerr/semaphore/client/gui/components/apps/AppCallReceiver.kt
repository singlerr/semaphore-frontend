package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.gui.BMJUA
import io.github.singlerr.semaphore.client.gui.ICON_ACCEPT_CALL
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
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error
import java.util.UUID

class AppCallReceiver(
    navigator: GuiNavigator,
    private val info: CallerInformation,
    private val callResponseController: CallResponseController,
    private val callStateController: CallStateController,
    fullConstraint: Boolean = true
) : UIApp(navigator), UIInteractor {

    override val onShow: UIComponent.() -> Unit = {
        parent.unhide(true)
        SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 1.0f, true, true)
    }
    override val onHide: UIComponent.() -> Unit = {
        super.onHide(this)
        SoundPlayerAccess.getInstance().stopSound(SoundResource.BELL)
    }

    init {
        if (fullConstraint) {
            defaultConstraint()
        } else {
            innerConstraint()
        }

        UIBlurredGradientBackground(delta = 0.0005f).constrain {
            x = 0.pixels()
            y = 0.pixels()

            width = 100.percent()
            height = 100.percent()
        } childOf this

        UIHead(info.id).constrain {
            x = CenterConstraint()
            y = 30.pixels()

            width = 30.pixels()
            height = ImageAspectConstraint()
        } childOf this

        UIText(info.name, shadow = false).constrain {
            x = CenterConstraint()
            y = 70.pixels()

            width = 45.pixels()
            height = 10.pixels()

            fontProvider = BMJUA
        } childOf this
        UIResourceImage(ICON_ACCEPT_CALL)
            .constrain {
                x = 10.pixels()
                y = 30.pixels(alignOpposite = true)

                width = 20.pixels()
                height = ImageAspectConstraint()
            }
            .onMouseClick {
                callResponseController.reply(
                    CallResponse(
                        info.id,
                        UMinecraft.getMinecraft().player.uniqueID,
                        CallResponse.Response.ACCEPT
                    )
                )
                SoundPlayerAccess.getInstance()
                    .playSound(SoundResource.CALL_ACCEPT, 1.0f, 1.0f, false, true)
                exit()
                navigator.push(
                    AppCall(
                        navigator = navigator,
                        information =
                        CallInformation(
                            opponentId = info.id,
                            callerId = info.id,
                            calleeId = UMinecraft.getMinecraft().player.uniqueID
                        ),
                        callStateController = callStateController
                    )
                )
            } childOf this
        UIResourceImage(ICON_REJECT_CALL)
            .constrain {
                x = 10.pixels(alignOpposite = true)
                y = 30.pixels(alignOpposite = true)

                width = 20.pixels()
                height = ImageAspectConstraint()
            }
            .onMouseClick {
                callResponseController.reply(
                    CallResponse(
                        info.id,
                        UMinecraft.getMinecraft().player.uniqueID,
                        CallResponse.Response.REJECT
                    )
                )
                SoundPlayerAccess.getInstance()
                    .playSound(SoundResource.CALL_REJECT, 1.0f, 1.0f, false, true)
                navigator.pop()
            } childOf this
    }

    override fun shouldPresent(entity: Error?): Boolean = true

    override fun shouldPresent(error: ErrorEntity?): Boolean = true

    override fun error(
        entity: io.github.singlerr.semaphore.interactors.callee.presenter.data.Error?
    ) {
        exit()
    }

    override fun present(error: Error?) {
        exit()
    }

    override fun presentError(error: ErrorEntity?) {

        exit()
    }
}

data class CallerInformation(val id: UUID, val name: String)
