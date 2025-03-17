package com.github.numq.stretch.signalsmith

import com.github.numq.stretch.Stretch

internal class SignalsmithStretch(
    override val sampleRate: Int,
    override val channels: Int,
    override var playbackSpeedFactor: Float = 1f,
    private val nativeSignalsmithStretch: NativeSignalsmithStretch,
) : Stretch.Signalsmith {
    init {
        nativeSignalsmithStretch.configure(
            channels = channels,
            blockSamples = (sampleRate * 0.12).toInt(),
            intervalSamples = (sampleRate * 0.03).toInt()
        )
    }

    override suspend fun changePlaybackSpeedFactor(factor: Float) = runCatching { playbackSpeedFactor = factor }

    override suspend fun getInputLatency() = runCatching { nativeSignalsmithStretch.getInputLatency() }

    override suspend fun getOutputLatency() = runCatching { nativeSignalsmithStretch.getOutputLatency() }

    override suspend fun process(pcmBytes: ByteArray) = runCatching {
        nativeSignalsmithStretch.process(
            pcmBytes = pcmBytes, channels = channels, playbackSpeedFactor = playbackSpeedFactor
        )
    }

    override suspend fun flush() = runCatching { nativeSignalsmithStretch.flush(channels = channels) }

    override suspend fun reset() = runCatching { nativeSignalsmithStretch.reset() }

    override fun close() = runCatching { nativeSignalsmithStretch.close() }.getOrDefault(Unit)
}