package io.github.singlerr.semaphore.client.sound

import io.github.singlerr.semaphore.client.sounds.SoundKey
import io.github.singlerr.semaphore.client.sounds.SoundPlayer
import io.github.singlerr.semaphore.client.sounds.SoundResource
import java.util.UUID
import org.apache.logging.log4j.LogManager

class StubSoundPlayer : SoundPlayer {

    private val logger = LogManager.getLogger(javaClass)

    override fun playSound(
        sound: SoundResource?,
        pitch: Float,
        volume: Float,
        repeat: Boolean,
        stopPrevious: Boolean
    ): SoundKey? {
        logger.info(
            "Requested to play sound {} with repeat {} with stop {}",
            sound,
            repeat,
            stopPrevious
        )
        return SoundKey(UUID.randomUUID(), sound) {
            logger.info("Set volume of {} to {}", sound, it)
        }
    }

    override fun playSound(
        sound: SoundResource?,
        pitch: Float,
        volume: Float,
        repeatDelay: Int,
        repeat: Boolean,
        stopPrevious: Boolean
    ): SoundKey? {
        return SoundKey(UUID.randomUUID(), sound) {
            logger.info("Set volume of {} to {}", sound, it)
        }
    }

    override fun stopSound(soundKey: SoundKey?) {
        logger.info("Requested to stop sound {}", soundKey)
    }

    override fun stopSound(sound: SoundResource?) {
        logger.info("Requested to stop sound by type {}", sound)
    }
}
