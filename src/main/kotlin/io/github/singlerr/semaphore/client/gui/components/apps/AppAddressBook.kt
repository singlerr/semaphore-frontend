package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.universal.UMinecraft
import io.github.singlerr.semaphore.client.gui.ICON_ADDRESS_BOOK
import io.github.singlerr.semaphore.client.gui.Memoize
import io.github.singlerr.semaphore.client.gui.components.UIAppIcon
import io.github.singlerr.semaphore.client.gui.components.UIInteractor
import io.github.singlerr.semaphore.client.gui.widgets.*
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest
import java.awt.Color

class AppAddressBook(
    navigator: GuiNavigator,
    private val entries: State<PlayerEntryList>,
    private val callRequestController: CallRequestController,
    private val callStateController: CallStateController
) : UIApp(navigator), UIInteractor {

    init {
        defaultConstraint()

        UIBlock(Color(230, 240, 240)).constrain {
            x = CenterConstraint()
            y = CenterConstraint()

            width = 100.percent()
            height = 100.percent()
        } childOf this
        val entryList =
            ScrollComponent().constrain {
                x = 0.pixels()
                y = 0.pixels()

                width = 100.percent()
                height = 100.percent()
            } childOf this
        val apply: (PlayerEntryList) -> Unit = { list ->
            entryList.clearChildren()
            for (entry in list.entries) {
                UIPlayerEntry(entry, callRequestController)
                    .constrain {
                        x = CenterConstraint()
                        y = SiblingConstraint(padding = 1f)

                        width = 100.pixels()
                        height = 22.pixels()
                    }
                    .also {
                        it.onClickRequestButton = {
                            navigator.push(
                                AppCallRequesting(
                                    navigator = navigator,
                                    callerInformation =
                                        CallerInformation(
                                            UMinecraft.getMinecraft().player.uniqueID,
                                            UMinecraft.getMinecraft().player.name
                                        ),
                                    calleeInformation = CalleeInformation(entry.id, entry.name),
                                    callRequestController = callRequestController,
                                    callStateController = callStateController,
                                    fullConstraint = false
                                )
                            )

                            callRequestController.request(
                                CallRequest(UMinecraft.getMinecraft().player.uniqueID, entry.id)
                            )
                        }
                    } childOf entryList
            }
        }
        entries.onSetValue(apply)
        //        apply(entries.getOrDefault(PlayerEntryList(emptyList())))
        //                apply(
        //                    PlayerEntryList(
        //                        (0 until 10)
        //                            .map {
        //                                PresentableEntity(UUID.randomUUID(),
        // PresentableEntity.State(0,
        //         HashMap(),
        // io.github.singlerr.semaphore.interactors.access.database.EntityType.PLAYER))
        //                            }
        //                            .map { PlayerEntry(it.id, it.state.toString(), BasicState(0))
        // }
        //                            .toList()
        //                    )
        //                )
    }

    override fun shouldPresent(entities: List<PresentableEntity>?): Boolean = true
    override fun shouldPresent(entity: PresentableEntity?): Boolean = true

    override fun present(entities: MutableList<PresentableEntity>?) {
        val countMap =
            entities
                ?.find { it.id == UMinecraft.getMinecraft().player.uniqueID }
                ?.state
                ?.missCallCount

        entries.set(
            PlayerEntryList(
                entities
                    ?.filter {
                        it.state.entityType ==
                            io.github.singlerr.semaphore.interactors.access.database.EntityType
                                .PLAYER
                    }
                    ?.filter { it.id != UMinecraft.getMinecraft().player.uniqueID }
                    ?.map {
                        PlayerEntry(
                            it.id,
                            getPlayerProfile(it.id)?.name ?: "Unknown",
                            BasicState(countMap?.get(it.id) ?: 0)
                        )
                    }
                    ?.toList()
                    ?: emptyList()
            )
        )
    }

    override fun present(entity: PresentableEntity?) {
        if (entity?.id == UMinecraft.getMinecraft().player.uniqueID) {
            val countMap = entity.state.missCallCount
            countMap.entries.forEach { (id, count) ->
                {
                    entries
                        .get()
                        .entries
                        .find { it.id == id }
                        ?.missCallCount
                        ?.also { println(it) }
                        ?.set(count)
                }
            }
        } else {
            entries
                .get()
                .entries
                .find { it.id == entity?.id }
                ?.missCallCount
                ?.set(entity?.state?.missCallCount?.get(entity.id) ?: return)
        }

        entries.set(entries.get())
    }
}

class IconAddressBook(
    navigator: GuiNavigator,
    callRequestController: CallRequestController,
    callStateController: CallStateController
) : UIAppIcon(resourceLocation = ICON_ADDRESS_BOOK, iconName = BasicState("주소록")) {
    init {
        onMouseClick {
            addressBookInstance(
                    AddressBookParams(navigator, callRequestController, callStateController)
                )
                .open()
        }
    }
}

val playerEntryList: State<PlayerEntryList> = BasicState(PlayerEntryList(mutableListOf()))

val addressBookInstance: (AddressBookParams) -> AppAddressBook by Memoize { param ->
    AppAddressBook(
        param.navigator,
        playerEntryList,
        param.requestController,
        param.callStateController
    )
}

data class AddressBookParams(
    val navigator: GuiNavigator,
    val requestController: CallRequestController,
    val callStateController: CallStateController
)
