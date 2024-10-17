package io.github.singlerr.semaphore.client.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.dsl.*
import io.github.singlerr.access.semaphore.client.gui.NonVanillaScreen
import io.github.singlerr.semaphore.client.ConfigHolder
import io.github.singlerr.semaphore.client.gui.components.UIInteractor
import io.github.singlerr.semaphore.client.gui.components.UINavigable
import io.github.singlerr.semaphore.client.gui.components.UIPhoneFrame
import io.github.singlerr.semaphore.client.gui.widgets.*
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.client.sounds.SoundResource
import io.github.singlerr.semaphore.config.entry.ObservableConfigEntry
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest
import java.util.*

class GuiPhoneScreen(
    private val callRequestController: CallRequestController,
    private val callResponseController: CallResponseController,
    private val entityController: EntityController,
    private val callStateController: CallStateController,
    private val volumeConfig: ObservableConfigEntry<Map<String, Double>>
) : WindowScreen(ElementaVersion.V6, newGuiScale = 0), UIInteractor, NonVanillaScreen {

    private val configHolder = ConfigHolder(volumeConfig)

    private val navigatorImpl: GuiNavigator = GuiNavigator { old, new ->
        old?.run {
            if (this is UINavigable) {
                this.onHide()
            }
        }
        new?.run {
            (old ?: window).addChild(this)
            if (this is UINavigable) {
                this.onShow()
            }
            entityController.getAllEntities(EntityQuery.GetAllEntities())
            return@GuiNavigator
        }
    }

    init {

        navigatorImpl.push(
            UIPhoneFrame(
                navigatorImpl,
                callRequestController,
                callResponseController,
                entityController,
                callStateController,
                configHolder
            )
        )

        //        navigatorImpl.push(
        //            AppCallReceiver(
        //                navigatorImpl,
        //                CallerInformation(UUID.randomUUID(), "Test"),
        //                callResponseController = callResponseController
        //            )
        //        )

        setupDevMode()
    }

    private fun setupDevMode() {
        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window
    }

    override fun present(entities: MutableList<PresentableEntity>?) {
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(entities) }
                .forEach { (it as UIInteractor).present(entities) }
            configHolder.present(entities)
        }
    }

    override fun present(entity: PresentableEntity?) {
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(entity) }
                .forEach { (it as UIInteractor).present(entity) }
            configHolder.present(entity)
        }
    }

    override fun present(entity: CallResponse?) {
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(entity) }
                .forEach { (it as UIInteractor).present(entity) }
        }
    }

    override fun present(request: InverseCallRequest?) {
        SoundPlayerAccess.getInstance().playSound(SoundResource.BELL, 1.0f, 1.0f, true, true)
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(request) }
                .forEach { (it as UIInteractor).present(request) }
        }
    }

    override fun presentError(error: ErrorEntity?) {
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(error) }
                .forEach { (it as UIInteractor).presentError(error) }
        }
    }

    override fun present(error: Error?) {
        Window.enqueueRenderOperation {
            navigatorImpl.pages
                .filter { it is UIInteractor && it.shouldPresent(error) }
                .forEach { (it as UIInteractor).present(error) }
        }
    }
}
