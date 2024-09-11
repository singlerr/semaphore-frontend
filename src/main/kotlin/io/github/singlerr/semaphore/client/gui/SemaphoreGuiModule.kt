package io.github.singlerr.semaphore.client.gui

import io.github.singlerr.access.semaphore.client.gui.NonVanillaScreenAccess
import io.github.singlerr.semaphore.client.sound.StubSoundPlayer
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.config.ConfigurationManager
import io.github.singlerr.semaphore.config.entry.ObservableConfigEntry
import io.github.singlerr.semaphore.interactors.admin.controller.CallStateController
import io.github.singlerr.semaphore.interactors.admin.controller.EntityController
import io.github.singlerr.semaphore.interactors.admin.controller.data.CallStateQuery
import io.github.singlerr.semaphore.interactors.admin.controller.data.EntityQuery
import io.github.singlerr.semaphore.interactors.callee.controller.CallResponseController
import io.github.singlerr.semaphore.interactors.callee.controller.data.CallResponse
import io.github.singlerr.semaphore.interactors.caller.controller.CallRequestController
import io.github.singlerr.semaphore.interactors.caller.controller.data.CallRequest
import java.util.function.Supplier
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

@Mod(modid = MOD_ID)
class SemaphoreGuiModule {

    var screen: Supplier<GuiPhoneScreen> =
        object : Supplier<GuiPhoneScreen> {
            override fun get(): GuiPhoneScreen {
                return GuiPhoneScreen(
                    StubRequestController(),
                    StubResponseController(),
                    StubEntityController(),
                    StubCallStateController(),
                    ObservableConfigEntry(HashMap<String, Double>())
                )
            }
        }

    val keyOpenScreen = KeyBinding("Open", Keyboard.KEY_B, "key.categories.misc")

    var volumeConfig: ObservableConfigEntry<Map<String, Double>>? = null

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ConfigurationManager.getInstance().register({ volumeConfig = it.config.volumes }, false)
    }

    @EventHandler
    fun onPostInit(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        ClientRegistry.registerKeyBinding(keyOpenScreen)

        NonVanillaScreenAccess.setFactory { params ->
            val screen =
                GuiPhoneScreen(
                    callRequestController = params.callRequestController,
                    callResponseController = params.callResponseController,
                    entityController = params.entityController,
                    callStateController = params.callStateController,
                    volumeConfig = volumeConfig!!
                )
            params.entityPresenterRegistry?.accept(screen)
            params.callRequestPresenterRegistry?.accept(screen)
            params.callResponsePresenterRegistry?.accept(screen)
            params.errorPresenterRegistry?.accept(screen)
            return@setFactory screen
        }

        if (SoundPlayerAccess.getInstance() == null) {
            SoundPlayerAccess.setInstance(StubSoundPlayer())
        }
    }

    @SubscribeEvent
    fun onKeyInput(event: TickEvent.ClientTickEvent) {
        if (keyOpenScreen.isPressed && Minecraft.getMinecraft().currentScreen !is GuiPhoneScreen) {
            screen.get().apply { Minecraft.getMinecraft().displayGuiScreen(this) }
        }
    }

    private class StubCallStateController : CallStateController {
        override fun openCall(query: CallStateQuery.OpenCall?) {}

        override fun closeCall(query: CallStateQuery.CloseCall?) {}

        override fun closeCall(query: CallStateQuery.CloseCallById?) {}
    }

    private class StubRequestController : CallRequestController {
        override fun request(request: CallRequest?) {}
    }
    private class StubResponseController : CallResponseController {
        override fun reply(response: CallResponse?) {}
    }

    private class StubEntityController : EntityController {
        override fun getEntity(query: EntityQuery.GetEntity?) {}

        override fun createEntity(query: EntityQuery.CreateEntity?) {}

        override fun deleteEntity(query: EntityQuery.DeleteEntity?) {}

        override fun getAllEntities(query: EntityQuery.GetAllEntities?) {}
    }
}

const val MOD_ID = "semaphore-elementa"
const val MOD_DEPS = "required-after:semaphore"
