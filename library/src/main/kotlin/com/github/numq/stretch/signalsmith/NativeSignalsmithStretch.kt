package com.github.numq.stretch.signalsmith

import java.lang.ref.Cleaner

internal class NativeSignalsmithStretch : AutoCloseable {
    private val nativeHandle = initNative().also { handle ->
        require(handle != -1L) { "Unable to initialize native library" }
    }

    private val cleanable = cleaner.register(this) { freeNative(nativeHandle) }

    private companion object {
        val cleaner: Cleaner = Cleaner.create()

        @JvmStatic
        external fun initNative(): Long

        @JvmStatic
        external fun getInputLatencyNative(handle: Long): Int

        @JvmStatic
        external fun getOutputLatencyNative(handle: Long): Int

        @JvmStatic
        external fun configureNative(handle: Long, channels: Int, blockSamples: Int, intervalSamples: Int)

        @JvmStatic
        external fun processNative(
            handle: Long,
            pcmBytes: ByteArray,
            channels: Int,
            playbackSpeedFactor: Float,
        ): ByteArray

        @JvmStatic
        external fun resetNative(handle: Long)

        @JvmStatic
        external fun freeNative(handle: Long)
    }

    fun getInputLatency() = getInputLatencyNative(handle = nativeHandle)

    fun getOutputLatency() = getOutputLatencyNative(handle = nativeHandle)

    fun configure(channels: Int, blockSamples: Int, intervalSamples: Int) = configureNative(
        handle = nativeHandle,
        channels = channels,
        blockSamples = blockSamples,
        intervalSamples = intervalSamples
    )

    fun process(pcmBytes: ByteArray, channels: Int, playbackSpeedFactor: Float) = processNative(
        handle = nativeHandle,
        pcmBytes = pcmBytes,
        channels = channels,
        playbackSpeedFactor = playbackSpeedFactor
    )

    fun reset() = resetNative(handle = nativeHandle)

    override fun close() = cleanable.clean()
}