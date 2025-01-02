package playback

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

class LinePlaybackService : PlaybackService {
    private val mutex = Mutex()

    private var sourceDataLine: SourceDataLine? = null

    override suspend fun start(format: AudioFormat) = mutex.withLock {
        runCatching {
            sourceDataLine?.close()
            sourceDataLine = AudioSystem.getSourceDataLine(format).apply {
                val bufferSize = format.sampleRate.toInt() * format.frameSize / 10

                open(format, bufferSize)

                start()
            }
        }
    }

    override suspend fun play(pcmBytes: ByteArray) = mutex.withLock {
        runCatching {
            checkNotNull(sourceDataLine?.write(pcmBytes, 0, pcmBytes.size)) { "Playback line is not initialized" }

            Unit
        }
    }

    override suspend fun stop() = mutex.withLock {
        runCatching {
            sourceDataLine?.stop()

            sourceDataLine?.drain()

            Unit
        }
    }

    override fun close() = runBlocking {
        mutex.withLock {
            sourceDataLine?.close()
            sourceDataLine = null
        }
    }
}