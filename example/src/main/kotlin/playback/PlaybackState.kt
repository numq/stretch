package playback

sealed interface PlaybackState {
    data object Empty : PlaybackState

    sealed interface Uploaded : PlaybackState {
        val path: String

        data class Stopped(override val path: String) : Uploaded

        data class Playing(override val path: String) : Uploaded
    }
}