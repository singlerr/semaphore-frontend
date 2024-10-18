package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.dsl.boundTo
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import io.github.singlerr.semaphore.client.ConfigHolder
import io.github.singlerr.semaphore.client.gui.ICON_SETTINGS
import io.github.singlerr.semaphore.client.gui.Memoize
import io.github.singlerr.semaphore.client.gui.components.UIAppIcon
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.defaultConstraint
import io.github.singlerr.semaphore.client.sounds.NotificationType
import io.github.singlerr.semaphore.client.sounds.SoundResource
import java.awt.Color

class AppSystemSettings(navigator: GuiNavigator, config: ConfigHolder) : UIApp(navigator) {

    init {
        defaultConstraint()
        constrain { color = Color.WHITE.toConstraint() }

        val btnSound =
            UIBlock()
                .constrain {
                    x = 3.pixels()
                    y = 5.pixels()

                    width = 50.percent() - 3.pixels()
                    height = 20.pixels()

                    color =
                        config.notificationType
                            .map { if (it == NotificationType.SOUND) Color.GREEN else Color.GRAY }
                            .toConstraint()
                }
                .onMouseClick { config.notificationType.set(NotificationType.SOUND) } childOf this

        UIText("Sound").constrain {
            x = CenterConstraint() boundTo btnSound
            y = CenterConstraint() boundTo btnSound

            width = 25.pixels()
            height = 15.pixels()
        } childOf btnSound

        val btnVibration =
            UIBlock()
                .constrain {
                    x = 3.pixels(alignOpposite = true)
                    y = 5.pixels()

                    width = 50.percent() - 3.pixels()
                    height = 20.pixels()

                    color =
                        config.notificationType
                            .map {
                                if (it == NotificationType.VIBRATION) Color.GREEN else Color.GRAY
                            }
                            .toConstraint()
                }
                .onMouseClick { config.notificationType.set(NotificationType.VIBRATION) } childOf
                    this
        UIText("Vibration").constrain {
            x = CenterConstraint() boundTo btnVibration
            y = CenterConstraint() boundTo btnVibration

            width = 25.pixels()
            height = 15.pixels()
        } childOf btnVibration

        val scrolls =
            ScrollComponent().constrain {
                x = 0.pixels()
                y = 25.pixels()

                width = 100.percent()
                height = 100.percent() - 25.pixels()
            } childOf this

        val getter: (SoundResource) -> Float = { s -> config.systemVolumes[s]!! }
        val setter: (SoundResource, Float) -> Unit = { s, f -> config.systemVolumes[s] = f }
        config.systemVolumes.entries.forEach { entry ->
            UISystemVolumeEntry(entry.key, getter, setter, repeatingDelay = entry.key.repeatDelay)
                .constrain {
                    x = CenterConstraint()
                    y = SiblingConstraint(padding = 1f)

                    width = 100.pixels()
                    height = 50.pixels()
                } childOf scrolls
        }

        children.forEach { it.onMouseClick { this@AppSystemSettings.grabWindowFocus() } }
    }
}

class IconSystemSettings(navigator: GuiNavigator, config: ConfigHolder) :
    UIAppIcon(resourceLocation = ICON_SETTINGS, iconName = BasicState("System Settings")) {
    init {
        onMouseClick { systemSettingsInstance(SystemSettingsParams(navigator, config)).open() }
    }
}

val systemSettingsInstance: (SystemSettingsParams) -> AppSystemSettings by Memoize { params: SystemSettingsParams ->
    AppSystemSettings(params.navigator, params.config)
}

data class SystemSettingsParams(val navigator: GuiNavigator, val config: ConfigHolder)
