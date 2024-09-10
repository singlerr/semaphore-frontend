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
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
import java.util.UUID

class AppCallReceiver(
    navigator: GuiNavigator,
    private val info: CallerInformation,
    private val callResponseController: CallResponseController,
    private val callStateController: CallStateController
) : UIApp(navigator), UIInteractor {

    override val onShow: UIComponent.() -> Unit = { parent.unhide(true) }

    init {
        defaultConstraint()
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
                navigator.pop()
            } childOf this
    }

    override fun shouldPresent(entity: Error?): Boolean = true

    override fun shouldPresent(error: ErrorEntity?): Boolean =
        error?.message()?.startsWith("error.call.closed") == true

    override fun shouldPresent(
        error: io.github.singlerr.semaphore.interactors.caller.presenter.data.Error?
    ): Boolean = true

    override fun error(
        entity: io.github.singlerr.semaphore.interactors.callee.presenter.data.Error?
    ) {
        exit()
    }

    override fun present(
        error: io.github.singlerr.semaphore.interactors.caller.presenter.data.Error?
    ) {
        exit()
    }

    override fun presentError(error: ErrorEntity?) {
        error?.let {
            if (it.message().startsWith("error.call.closed")) {
                val infoSection =
                    it.message()
                        .substring(
                            it.message().indexOf("error.call.closed") + "error.call.closed".length
                        )
                if (infoSection.isEmpty()) return@let

                val args = infoSection.split("|")
                val callerId = UUID.fromString(args[0])
                val calleeId = UUID.fromString(args[1])

                // Exit only related with me
                if (info.id == callerId && calleeId == UMinecraft.getMinecraft().player.uniqueID) {
                    exit()
                    return
                }
            }
        }
        exit()
    }
}

data class CallerInformation(val id: UUID, val name: String)
