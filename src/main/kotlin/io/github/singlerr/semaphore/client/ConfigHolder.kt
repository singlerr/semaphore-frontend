package io.github.singlerr.semaphore.client

import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import io.github.singlerr.semaphore.client.gui.components.apps.VolumeEntry
import io.github.singlerr.semaphore.client.gui.components.apps.VolumeEntryList
import io.github.singlerr.semaphore.client.sounds.NotificationType
import io.github.singlerr.semaphore.client.sounds.SoundResource
import io.github.singlerr.semaphore.config.entry.ObservableConfigEntry
import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity
import java.util.UUID

class ConfigHolder(private val config: ObservableConfigEntry<Map<String, Double>>) :
    EntityPresenter {

    val volumeEntries: State<VolumeEntryList>
    val notificationType: State<NotificationType> = BasicState(NotificationType.SOUND)
    val systemVolumes: MutableMap<SoundResource, Float> =
        mutableMapOf<SoundResource, Float>().also {
            SoundResource.values().map { it to 0.5f }.toMap(it)
        }

    init {
        val configEntries = config.get().toMutableMap()
        val update: (MutableMap<String, Double>) -> VolumeEntryList = {
            val entryList =
                it.entries.map { entry ->
                    val id = UUID.fromString(entry.key)
                    val volume = entry.value

                    val state = BasicState(volume)
                    state.onSetValue { newValue ->
                        configEntries[id.toString()] = newValue
                        this.config.set(configEntries)
                    }
                    return@map VolumeEntry(id, state)
                }

            VolumeEntryList(entryList)
        }

        volumeEntries = BasicState(update(configEntries))

        config.observe { newConfig -> volumeEntries.set(update(newConfig.toMutableMap())) }
    }

    private fun map(config: MutableMap<String, Double>): VolumeEntryList =
        VolumeEntryList(
            config.entries
                .map { entry ->
                    VolumeEntry(
                        UUID.fromString(entry.key),
                        BasicState(entry.value).also { state ->
                            state.onSetValue { value ->
                                {
                                    config[entry.key] = value
                                    this.config.set(config)
                                }
                            }
                        }
                    )
                }
                .toList()
        )
    private fun map(config: ObservableConfigEntry<Map<String, Double>>): VolumeEntryList =
        VolumeEntryList(
            config
                .get()
                ?.entries
                ?.map { entry ->
                    VolumeEntry(
                        UUID.fromString(entry.key),
                        BasicState(entry.value).also { state ->
                            state.onSetValue { value ->
                                {
                                    val temp = config.get().toMutableMap()
                                    temp[entry.key] = value
                                    this.config.set(temp)
                                }
                            }
                        }
                    )
                }!!
                .toList()
        )

    override fun present(entity: PresentableEntity?) {
        var map = config.get().toMutableMap()
        if (!map.contains(entity?.id.toString())) map[entity?.id.toString()] = 0.5

        config.set(map)
    }

    override fun present(entities: List<PresentableEntity?>?) {
        var map = config.get().toMutableMap()
        entities?.forEach { entity ->
            if (!map.contains(entity?.id.toString())) {
                map[entity?.id.toString()] = 0.5
            }
        }
        config.set(map)
    }

    override fun presentError(error: ErrorEntity?) {}
}
