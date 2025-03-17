package com.github.numq.stretch

import com.github.numq.stretch.signalsmith.NativeSignalsmithStretch
import com.github.numq.stretch.signalsmith.SignalsmithStretch

interface Stretch : AutoCloseable {
    /**
     * The sample rate of the audio data being processed.
     */
    val sampleRate: Int

    /**
     * The number of channels (e.g., 1 for mono, 2 for stereo) in the audio data.
     */
    val channels: Int

    /**
     * The playback speed factor.
     * A value of 1.0 means normal speed, values greater than 1.0 speed up the playback, and values less than 1.0 slow it down.
     */
    val playbackSpeedFactor: Float

    /**
     * Changes the playback speed factor for the audio processing.
     *
     * @param factor The new playback speed factor.
     * A factor of 1.0 is normal speed.
     * @return A [Result] indicating success or failure of the operation.
     */
    suspend fun changePlaybackSpeedFactor(factor: Float): Result<Unit>

    /**
     * Retrieves the input latency in milliseconds.
     *
     * @return A [Result] containing the output latency in milliseconds, or failure if unable to retrieve.
     */
    suspend fun getInputLatency(): Result<Int>

    /**
     * Retrieves the output latency in milliseconds.
     *
     * @return A [Result] containing the output latency in milliseconds, or failure if unable to retrieve.
     */
    suspend fun getOutputLatency(): Result<Int>

    /**
     * Processes the PCM data.
     *
     * @param pcmBytes The raw PCM audio data to be processed.
     * @return A [Result] containing the processed audio data as a byte array.
     */
    suspend fun process(pcmBytes: ByteArray): Result<ByteArray>

    /**
     * Clears the internal buffers.
     *
     * @return A [Result] indicating success or failure of the reset operation.
     */
    suspend fun flush(): Result<Unit>

    /**
     * Resets the internal state.
     *
     * @return A [Result] indicating success or failure of the reset operation.
     */
    suspend fun reset(): Result<Unit>

    interface Signalsmith : Stretch {
        companion object {
            private var isLoaded = false

            /**
             * Loads the native library.
             *
             * This method must be called before creating a Signalsmith instance.
             *
             * @param stretchSignalsmith the path to the `stretch-signalsmith` binary.
             * @return a [Result] indicating the success or failure of the operation.
             */
            fun load(stretchSignalsmith: String) = runCatching {
                System.load(stretchSignalsmith)
            }.onSuccess {
                isLoaded = true
            }

            /**
             * Creates a new instance of [SignalsmithStretch] using the Signalsmith implementation.
             *
             * @param sampleRate the sample rate of input data.
             * @param channels the number of input data channels.
             * @param defaultPlaybackSpeedFactor the default playback speed factor.
             * @return a [Result] containing the created instance if successful.
             * @throws IllegalStateException if the native libraries are not loaded or if there is an issue with the underlying native libraries.
             */
            fun create(
                sampleRate: Int,
                channels: Int,
                defaultPlaybackSpeedFactor: Float,
            ): Result<Signalsmith> = runCatching {
                check(isLoaded) { "Native binaries were not loaded" }

                require(sampleRate > 0) { "Sample rate must be greater than 0" }

                require(channels > 0) { "Number of channels must be greater than 0" }

                SignalsmithStretch(
                    sampleRate = sampleRate,
                    channels = channels,
                    playbackSpeedFactor = defaultPlaybackSpeedFactor,
                    nativeSignalsmithStretch = NativeSignalsmithStretch()
                )
            }
        }
    }
}