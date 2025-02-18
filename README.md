# Stretch

Audio stretching library for JVM based on the C++
library [signalsmith-stretch](https://github.com/Signalsmith-Audio/signalsmith-stretch).

## Features

- Stretches PCM audio data
- Supports any sampling rate and number of channels

## Usage

> See the [example](example) module for implementation details

1. Download latest [release](https://github.com/numq/stretch/releases)
2. Add library dependency
   ```kotlin
   dependencies {
       implementation(file("/path/to/jar"))
   }
   ```
3. Load binaries
    ```kotlin
    Stretch.Signalsmith.load(libstretch = "/path/to/stretch-signalsmith")
    ```
4. Instantiate a library
    ```kotlin
    Stretch.Signalsmith.create(sampleRate = sampleRate, channels = channels, playbackSpeedFactor = defaultPlaybackSpeedFactor)
    ```

## Requirements

- JVM version 9 or higher.

## License

This project is licensed under the [Apache License 2.0](LICENSE).

## Acknowledgments

- [signalsmith-stretch](https://github.com/Signalsmith-Audio/signalsmith-stretch)
