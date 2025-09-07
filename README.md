<h1 align="center">Stretch</h1>

<br>

<div align="center" style="display: grid; justify-content: center;">

|                                                                  🌟                                                                   |                  Support this project                   |               
|:-------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------:|
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/bitcoin.png" alt="Bitcoin (BTC)" width="32"/>  | <code>bc1qs6qq0fkqqhp4whwq8u8zc5egprakvqxewr5pmx</code> | 
| <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/ethereum.png" alt="Ethereum (ETH)" width="32"/> | <code>0x3147bEE3179Df0f6a0852044BFe3C59086072e12</code> |
|  <img src="https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/32/tether.png" alt="USDT (TRC-20)" width="32"/>   |     <code>TKznmR65yhPt5qmYCML4tNSWFeeUkgYSEV</code>     |

</div>

<br>

<p align="center">Audio stretching library for JVM based on the C++ library <a href=https://github.com/Signalsmith-Audio/signalsmith-stretch">signalsmith-stretch</a></p>

### See also

- [Voice Activity Detection](https://github.com/numq/voice-activity-detection) *to extract speech from audio*


- [Speech recognition](https://github.com/numq/speech-recognition) *to transcribe audio to text*


- [Speech generation](https://github.com/numq/speech-generation) *to generate voice audio from text*


- [Text generation](https://github.com/numq/text-generation) *to generate text from prompt*


- [Noise reduction](https://github.com/numq/noise-reduction) *to remove noise from audio*

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
