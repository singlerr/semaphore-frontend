package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UICircle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.State
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.gui.ICON_MISS_CALL
import io.github.singlerr.semaphore.client.gui.ICON_REQUEST_CALL
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest
import java.awt.Color
import java.util.UUID
import org.apache.commons.lang3.StringUtils

class UIPlayerEntry(entry: PlayerEntry, callRequestController: CallRequestController) :
    UIBlock(Color.WHITE) {

    var onClickRequestButton: () -> Unit = {
        callRequestController.request(
            CallRequest(UMinecraft.getMinecraft().player.uniqueID, entry.id)
        )
    }

    init {
        val head =
            UIHead(entry.id).constrain {
                x = 5.pixels()
                y = CenterConstraint()

                width = 20.pixels()
                height = ImageAspectConstraint()
            } childOf this

        val userName =
            UIText(StringUtils.abbreviate(entry.name, 10)).constrain {
                x = 25.pixels() + 2.pixels()
                y = 5.pixels()

                textScale = (0.5).pixels()
            } childOf this

        val requestCallImage =
            UIResourceImage(ICON_REQUEST_CALL)
                .constrain {
                    x = 5.pixels(alignOpposite = true)
                    y = CenterConstraint()

                    width = 10.pixels()
                    height = ImageAspectConstraint()
                }
                .onMouseClick { onClickRequestButton() } childOf this

        val missCallCountIndicator =
            UIResourceImage(ICON_MISS_CALL).constrain {
                x = (15.pixels(alignOpposite = true) boundTo requestCallImage)
                y = CenterConstraint()

                width = requestCallImage.constraints.width
                height = ImageAspectConstraint()
            } childOf this
        val missCallCountBackground =
            UICircle(color = Color.RED).constrain {
                x = (9).pixels(alignOpposite = true)
                y = 9.pixels()

                radius = 3.5.pixels()
            } childOf requestCallImage

        val missCallCountText =
            UIText().bindText(entry.missCallCount.map { it.toString() }).constrain {
                color = Color.WHITE.toConstraint()

                x = 0.pixels(alignOpposite = true)
                y = (0.3).pixels(alignOpposite = true)

                width = 2.pixels()
                height = 2.pixels()
            } childOf missCallCountIndicator

        entry.missCallCount.onSetValue {
            if (it > 0) {
                missCallCountIndicator.unhide(true)
                missCallCountBackground.unhide(true)
                missCallCountText.unhide(true)
            } else {
                missCallCountBackground.hide(true)
                missCallCountIndicator.hide(true)
                missCallCountText.hide(true)
            }
        }
        entry.missCallCount.set(entry.missCallCount.get())
    }
}

data class PlayerEntry(val id: UUID, val name: String, val missCallCount: State<Int>)

data class PlayerEntryList(val entries: List<PlayerEntry>)
