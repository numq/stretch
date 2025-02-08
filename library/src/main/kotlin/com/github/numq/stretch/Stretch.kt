package com.github.numq.stretch

interface Stretch : AutoCloseable {
    val sampleRate: Int
    val channels: Int
    val playbackSpeedFactor: Float
    suspend fun changePlaybackSpeedFactor(factor: Float): Result<Unit>
    suspend fun getInputLatency(): Result<Int>
    suspend fun getOutputLatency(): Result<Int>
    suspend fun process(pcmBytes: ByteArray): Result<ByteArray>
    suspend fun reset(): Result<Unit>

    companion object {
        private var isLoaded = false

        /**
         * Loads the native library.
         *
         * @param stretch the path to the `stretch` binary.
         * @return a [Result] indicating the success or failure of the operation.
         */
        fun load(stretch: String) = runCatching {
            System.load(stretch)
        }.onSuccess {
            isLoaded = true
        }

        /**
         * Creates a new instance of [Stretch].
         *
         * @return a [Result] containing the created instance if successful.
         * @throws IllegalStateException if the native libraries are not initialized or if there is an issue with the underlying native library.
         */
        fun create(sampleRate: Int, channels: Int, defaultPlaybackSpeedFactor: Float): Result<Stretch> = runCatching {
            check(isLoaded) { "Native binaries were not loaded" }

            DefaultStretch(
                sampleRate = sampleRate,
                channels = channels,
                playbackSpeedFactor = defaultPlaybackSpeedFactor,
                nativeStretch = NativeStretch()
            )
        }
    }
}