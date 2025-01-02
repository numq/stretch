package interaction

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.github.numq.stretch.Stretch
import kotlinx.coroutines.*
import org.bytedeco.javacv.FFmpegFrameGrabber
import playback.PlaybackService
import playback.PlaybackState
import java.io.File
import java.net.URI
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InteractionScreen(playbackService: PlaybackService, handleThrowable: (Throwable) -> Unit) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Default }

    var playbackJob by remember { mutableStateOf<Job?>(null) }

    var state by remember { mutableStateOf<PlaybackState>(PlaybackState.Empty) }

    var playbackSpeedFactor by remember { mutableStateOf(1f) }

    suspend fun startPlaying(path: String) = runCatching {
        FFmpegFrameGrabber(path).use { grabber ->
            grabber.start()

            if (!grabber.hasAudio()) return@runCatching

            val format = AudioFormat(
                grabber.sampleRate.toFloat(),
                16,
                grabber.audioChannels,
                true,
                false
            )

            Stretch.create(
                sampleRate = format.sampleRate.toInt(),
                channels = format.channels,
                defaultPlaybackSpeedFactor = playbackSpeedFactor
            ).getOrThrow().use { stretch ->
                playbackService.start(format).mapCatching {
                    try {
                        while (currentCoroutineContext().isActive) {
                            val samples =
                                (grabber.grabSamples()?.samples?.get(0) as? ShortBuffer)?.run {
                                    ByteArray(capacity() * 2) { index ->
                                        val shortIndex = index / 2
                                        if (index % 2 == 0) {
                                            (get(shortIndex).toInt() and 0xFF).toByte()
                                        } else {
                                            (get(shortIndex).toInt()
                                                .shr(8) and 0xFF).toByte()
                                        }
                                    }
                                } ?: break

                            if (stretch.playbackSpeedFactor != playbackSpeedFactor) {
                                stretch.changePlaybackSpeedFactor(playbackSpeedFactor)
                                    .getOrThrow()
                            }

                            val stretchedData = stretch.process(samples).getOrThrow()

                            playbackService.play(stretchedData)
                        }
                    } catch (e: Exception) {
                        println("Error during playback: ${e.localizedMessage}")
                    } finally {
                        playbackService.stop()
                    }
                }.getOrThrow()
            }
        }
    }

    DisposableEffect(state) {
        playbackJob = when (state) {
            is PlaybackState.Uploaded.Playing -> coroutineScope.launch {
                runCatching {
                    playbackService.stop().getOrThrow()

                    val path = (state as PlaybackState.Uploaded.Playing).path

                    startPlaying(path).getOrThrow()

                    playbackJob?.invokeOnCompletion {
                        state = PlaybackState.Uploaded.Stopped(path)
                    }
                }.recoverCatching { t ->
                    if (t !is CancellationException) throw t
                }.onFailure(handleThrowable)
            }

            else -> null
        }

        onDispose {
            playbackJob?.cancel()
            playbackJob = null
        }
    }

    Box(modifier = Modifier.fillMaxSize().onExternalDrag(onDrop = { externalDragValue ->
        when (val data = externalDragValue.dragData) {
            is DragData.FilesList -> data.readFiles().lastOrNull()?.let { path ->
                File(URI(path).path).takeIf(File::exists)?.run {
                    state = PlaybackState.Uploaded.Playing(path = absolutePath)
                }
            }
        }
    }), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterVertically)
        ) {
            when (val currentState = state) {
                is PlaybackState.Empty -> IconButton(onClick = {

                }) {
                    Icon(Icons.Default.UploadFile, null)
                }

                is PlaybackState.Uploaded -> {
                    Text(currentState.path, modifier = Modifier.padding(8.dp))

                    when (currentState) {
                        is PlaybackState.Uploaded.Stopped -> IconButton(onClick = {
                            state = PlaybackState.Uploaded.Playing(
                                path = (state as PlaybackState.Uploaded.Stopped).path
                            )
                        }) {
                            Icon(Icons.Default.PlayCircle, null)
                        }

                        is PlaybackState.Uploaded.Playing -> IconButton(onClick = {
                            state = PlaybackState.Uploaded.Stopped(
                                path = (state as PlaybackState.Uploaded.Playing).path
                            )
                        }) {
                            Icon(Icons.Default.StopCircle, null)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                Text("Playback speed factor", modifier = Modifier.padding(8.dp))
                Text("$playbackSpeedFactor", modifier = Modifier.padding(8.dp))
                Slider(
                    value = playbackSpeedFactor,
                    onValueChange = { playbackSpeedFactor = it },
                    modifier = Modifier.fillMaxWidth(),
                    valueRange = .1f..5f
                )
            }
        }
    }
}