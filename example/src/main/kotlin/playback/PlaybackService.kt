package playback

import javax.sound.sampled.AudioFormat

interface PlaybackService : AutoCloseable {
    suspend fun start(format: AudioFormat): Result<Unit>
    suspend fun play(pcmBytes: ByteArray): Result<Unit>
    suspend fun stop(): Result<Unit>

    companion object {
        fun create(): Result<PlaybackService> = runCatching { LinePlaybackService() }
    }
}