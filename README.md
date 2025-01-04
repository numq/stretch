# Stretch

Audio stretching library for JVM based on the C++
library [signalsmith-stretch](https://github.com/Signalsmith-Audio/signalsmith-stretch).

## Features

- Stretches PCM audio data
- Supports any sampling rate and number of channels

## Installation

```kotlin
dependencies {
    implementation(file("/path/to/jar"))
}
```

## Usage

> See the [example](example) module for implementation details

1. Load binaries
    ```kotlin
    Stretch.load(libstretch = "/path/to/libstretch")
    ```
2. Instantiate a library
    ```kotlin
    Stretch.create(sampleRate, channels, defaultPlaybackSpeedFactor)
    ```

## Requirements

- JVM version 9 or higher.

## License

This project is licensed under the [Apache License 2.0](LICENSE).

## Acknowledgments

- [signalsmith-stretch](https://github.com/Signalsmith-Audio/signalsmith-stretch)
