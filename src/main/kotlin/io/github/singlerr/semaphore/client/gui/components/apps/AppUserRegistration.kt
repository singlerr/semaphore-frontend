package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.gui.*
import io.github.singlerr.semaphore.client.gui.components.UIAppIcon
import io.github.singlerr.semaphore.client.gui.components.UIInteractor
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.UIResourceImage
import io.github.singlerr.semaphore.client.gui.widgets.defaultConstraint
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityType
import java.awt.Color

class AppUserRegistration(navigator: GuiNavigator, private val entityController: EntityController) :
    UIApp(navigator), UIInteractor {

    init {
        defaultConstraint()
        UIBlock(Color(230, 240, 240)).constrain {
            x = CenterConstraint()
            y = CenterConstraint()

            width = 100.percent()
            height = 100.percent()
        } childOf this
        val bgUser =
            UIResourceImage(ICON_REGISTER_AS_USER)
                .constrain {
                    x = CenterConstraint()
                    y = 60.pixels()

                    width = ImageAspectConstraint()
                    height = 30.pixels()
                }
                .onMouseClick {
                    entityController.createEntity(
                        EntityQuery.CreateEntity(UMinecraft.getMinecraft().player.uniqueID)
                    )
                    exit()
                } childOf this
        val bgAdmin =
            UIResourceImage(ICON_REGISTER_AS_ADMIN)
                .constrain {
                    x = CenterConstraint()
                    y = 100.pixels()

                    width = ImageAspectConstraint()
                    height = 30.pixels()
                }
                .onMouseClick {
                    entityController.createEntity(
                        EntityQuery.CreateEntity(UMinecraft.getMinecraft().player.uniqueID)
                    )
                    entityController.updateEntity(
                        EntityQuery.UpdateEntity(
                            UMinecraft.getMinecraft().player.uniqueID,
                            EntityQuery.State(0, HashMap(), EntityType.ADMIN)
                        )
                    )
                    exit()
                } childOf this
    }
}

class IconUserRegistration(navigator: GuiNavigator, entityController: EntityController) :
    UIAppIcon(resourceLocation = ICON_USER_REGISTRATION, iconName = BasicState("등록")) {
    init {
        onMouseClick {
            userRegistrationInstance(UserRegistrationParams(navigator, entityController)).open()
        }
    }
}

val userRegistrationInstance: (UserRegistrationParams) -> AppUserRegistration by Memoize { params ->
    AppUserRegistration(params.navigator, params.entityController)
}

data class UserRegistrationParams(
    val navigator: GuiNavigator,
    val entityController: EntityController
)
