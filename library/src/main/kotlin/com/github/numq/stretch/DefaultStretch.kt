package com.github.numq.stretch

internal class DefaultStretch(
    override val sampleRate: Int,
    override val channels: Int,
    override var playbackSpeedFactor: Float = 1f,
    private val nativeStretch: NativeStretch,
) : Stretch {
    init {
        nativeStretch.configure(
            channels = channels,
            blockSamples = (sampleRate * 0.12).toInt(),
            intervalSamples = (sampleRate * 0.03).toInt()
        )
    }

    override suspend fun changePlaybackSpeedFactor(factor: Float) = runCatching { playbackSpeedFactor = factor }

    override suspend fun getInputLatency() = runCatching { nativeStretch.getInputLatency() }

    override suspend fun getOutputLatency() = runCatching { nativeStretch.getOutputLatency() }

    override suspend fun process(pcmBytes: ByteArray) = runCatching {
        nativeStretch.process(
            pcmBytes = pcmBytes, channels = channels, playbackSpeedFactor = playbackSpeedFactor
        )
    }

    override suspend fun reset() = runCatching { nativeStretch.reset() }

    override fun close() = runCatching { nativeStretch.close() }.getOrDefault(Unit)
}