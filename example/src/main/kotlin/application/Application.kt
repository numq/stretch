package application

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.github.numq.stretch.Stretch
import interaction.InteractionScreen
import playback.PlaybackService

fun main() {
    val pathToBinaries = Thread.currentThread().getContextClassLoader().getResource("bin")?.file

    checkNotNull(pathToBinaries) { "Binaries not found" }

    Stretch.load(libstretch = "$pathToBinaries\\libstretch.dll").getOrThrow()

    singleWindowApplication(state = WindowState(width = 512.dp, height = 512.dp)) {
        val playbackService = remember { PlaybackService.create().getOrThrow() }

        val (throwable, setThrowable) = remember { mutableStateOf<Throwable?>(null) }

        DisposableEffect(Unit) {
            onDispose {
                playbackService.close()
            }
        }

        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                InteractionScreen(playbackService = playbackService, handleThrowable = setThrowable)
                throwable?.let { t ->
                    Snackbar(
                        modifier = Modifier.padding(8.dp),
                        action = {
                            Button(onClick = { setThrowable(null) }) { Text("Dismiss") }
                        }
                    ) { Text(t.localizedMessage) }
                }
            }
        }
    }
}