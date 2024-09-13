package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import io.github.singlerr.semaphore.client.ConfigHolder
import io.github.singlerr.semaphore.client.gui.ICON_SETTINGS
import io.github.singlerr.semaphore.client.gui.Memoize
import io.github.singlerr.semaphore.client.gui.components.UIAppIcon
import io.github.singlerr.semaphore.client.gui.widgets.GuiNavigator
import io.github.singlerr.semaphore.client.gui.widgets.defaultConstraint
import java.awt.Color

class AppSettings(navigator: GuiNavigator, config: ConfigHolder) : UIApp(navigator) {

    init {
        defaultConstraint()

        constrain { color = Color.WHITE.toConstraint() }

        val scrolls =
            ScrollComponent().constrain {
                x = 0.pixels()
                y = 0.pixels()

                width = 100.percent()
                height = 100.percent()
            } childOf this

        val update: (VolumeEntryList) -> Unit = { entries ->
            val entitiesToRemove =
                scrolls.childrenOfType<UIVolumeEntry>().filter {
                    entries.entries.find { entry -> it.entry.id == entry.id } == null
                }
            val entitiesToAdd =
                entries.entries.filter {
                    scrolls.childrenOfType<UIVolumeEntry>().find { entry ->
                        entry.entry.id == it.id
                    } == null
                }

            entitiesToRemove.forEach { scrolls.removeChild(it) }

            entitiesToAdd.forEach { entry ->
                UIVolumeEntry(entry).constrain {
                    x = CenterConstraint()
                    y = SiblingConstraint(padding = 1f)

                    width = 100.pixels()
                    height = 22.pixels()
                } childOf scrolls
            }
        }

        config.volumeEntries.onSetValue { update(it) }

        update(config.volumeEntries.getOrDefault(EMPTY))

        children.forEach { it.onMouseClick { this@AppSettings.grabWindowFocus() } }
    }
}

private val EMPTY = VolumeEntryList(emptyList())

class IconSettings(navigator: GuiNavigator, config: ConfigHolder) :
    UIAppIcon(resourceLocation = ICON_SETTINGS, iconName = BasicState("Settings")) {
    init {
        onMouseClick { settingsInstance(SettingsParams(navigator, config)).open() }
    }
}

val settingsInstance: (SettingsParams) -> AppSettings by Memoize { params: SettingsParams ->
    AppSettings(params.navigator, params.config)
}

data class SettingsParams(val navigator: GuiNavigator, val config: ConfigHolder)
