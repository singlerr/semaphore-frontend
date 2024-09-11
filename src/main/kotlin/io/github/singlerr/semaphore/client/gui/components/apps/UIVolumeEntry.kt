package io.github.singlerr.semaphore.client.gui.components.apps

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.State
import io.github.singlerr.semaphore.client.gui.widgets.UISlider
import io.github.singlerr.semaphore.client.gui.widgets.UISoundSlider
import io.github.singlerr.semaphore.client.gui.widgets.toTranslated
import io.github.singlerr.semaphore.client.sounds.SoundKey
import io.github.singlerr.semaphore.client.sounds.SoundPlayerAccess
import io.github.singlerr.semaphore.client.sounds.SoundResource
import java.awt.Color
import java.util.UUID

class UIVolumeEntry(val entry: VolumeEntry) : UIBlock(Color.DARK_GRAY) {

    init {
        val head =
            UIHead(entry.id).constrain {
                x = 5.pixels()
                y = CenterConstraint()

                width = 20.pixels()
                height = ImageAspectConstraint()
            } childOf this

        UISlider(
                defaultValue = (entry.volume.get() * 100).toInt(),
                onChange = { v -> entry.volume.set(v.toDouble() / 100) }
            )
            .constrain {
                x = 30.pixels()
                y = CenterConstraint()

                width = 100.percent() - 15.pixels()
                height = 20.pixels()
            } childOf this
    }
}

class UISystemVolumeEntry(
    val sound: SoundResource,
    val getter: (SoundResource) -> Float,
    val setter: (SoundResource, Float) -> Unit
) : UIBlock(Color.DARK_GRAY) {

    init {
        val header =
            UIText(sound.toTranslated()).constrain {
                x = 5.pixels()
                y = 3.pixels()

                height = 10.pixels()
            } childOf this

        var currentSound: SoundKey? = null
        UISoundSlider(
                defaultValue = (getter(sound) * 100).toInt(),
                onChange = { v ->
                    setter(sound, v.toFloat() / 100)
                    currentSound?.volumeSetter?.accept(v.toFloat() / 100)
                },
                onBarPressed = {
                    currentSound =
                        SoundPlayerAccess.getInstance()
                            .playSound(sound, 1.0f, getter(sound), true, true)
                },
                onBarReleased = {
                    currentSound?.let { s -> SoundPlayerAccess.getInstance().stopSound(s) }
                }
            )
            .constrain {
                x = 5.pixels()
                y = 17.pixels()

                width = 100.percent() - 15.pixels()
                height = 20.pixels()
            } childOf this
    }
}

data class VolumeEntry(val id: UUID, val volume: State<Double>)

data class VolumeEntryList(val entries: List<VolumeEntry>)
