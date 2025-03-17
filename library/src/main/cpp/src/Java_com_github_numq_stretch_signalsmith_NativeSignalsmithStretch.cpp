#include "Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch.h"

static jclass exceptionClass;
static std::shared_mutex mutex;
static std::unordered_map<jlong, stretch_ptr> pointers;

void handleException(JNIEnv *env, const std::string &errorMessage) {
    env->ThrowNew(exceptionClass, errorMessage.c_str());
}

signalsmith::stretch::SignalsmithStretch<float> *getPointer(jlong handle) {
    std::shared_lock<std::shared_mutex> lock(mutex);

    auto it = pointers.find(handle);
    if (it == pointers.end()) {
        throw std::runtime_error("Invalid handle");
    }
    return it->second.get();
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;

    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_8) != JNI_OK) {
        return JNI_ERR;
    }

    exceptionClass = reinterpret_cast<jclass>(env->NewGlobalRef(env->FindClass("java/lang/RuntimeException")));
    if (exceptionClass == nullptr) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;

    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_8) != JNI_OK) return;

    if (exceptionClass) env->DeleteGlobalRef(exceptionClass);

    pointers.clear();
}

JNIEXPORT jlong JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_initNative(JNIEnv *env, jclass thisClass) {
    std::unique_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = new signalsmith::stretch::SignalsmithStretch<float>();

        stretch_ptr ptr(stretch);

        auto handle = reinterpret_cast<jlong>(ptr.get());

        pointers[handle] = std::move(ptr);

        return handle;
    } catch (const std::exception &e) {
        handleException(env, e.what());
        return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_getInputLatencyNative(JNIEnv *env, jclass thisClass,
                                                                                        jlong handle) {
    std::shared_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        return stretch->inputLatency();
    } catch (const std::exception &e) {
        handleException(env, e.what());
        return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_getOutputLatencyNative(JNIEnv *env, jclass thisClass,
                                                                                         jlong handle) {
    std::shared_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        return stretch->outputLatency();
    } catch (const std::exception &e) {
        handleException(env, e.what());
        return -1;
    }
}

JNIEXPORT void JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_configureNative(JNIEnv *env, jclass thisClass,
                                                                                  jlong handle,
                                                                                  jint channels,
                                                                                  jint blockSamples,
                                                                                  jint intervalSamples) {
    std::shared_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        stretch->configure(channels, blockSamples, intervalSamples);
    } catch (const std::exception &e) {
        handleException(env, e.what());
    }
}

JNIEXPORT jbyteArray JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_processNative(JNIEnv *env, jclass thisClass,
                                                                                jlong handle,
                                                                                jbyteArray pcmBytes, jint channels,
                                                                                jfloat playbackSpeedFactor) {
    std::shared_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        auto length = env->GetArrayLength(pcmBytes);
        if (length == 0) {
            throw std::runtime_error("Array is empty");
        }

        jbyte *byteArray = env->GetByteArrayElements(pcmBytes, nullptr);
        if (!byteArray) {
            throw std::runtime_error("Failed to get byte array elements");
        }

        if (length % (sizeof(int16_t) * channels) != 0) {
            env->ReleaseByteArrayElements(pcmBytes, byteArray, JNI_ABORT);
            throw std::runtime_error("Input array size is not a valid multiple of int16_t samples per channel");
        }

        int inputSamples = static_cast<int>(length / (sizeof(int16_t) * channels));

        std::vector<std::vector<float>> inputBuffers(channels, std::vector<float>(inputSamples));
        const auto *inputInterleaved = reinterpret_cast<const int16_t *>(byteArray);

        for (int i = 0; i < inputSamples; ++i) {
            for (int ch = 0; ch < channels; ++ch) {
                inputBuffers[ch][i] = static_cast<float>(inputInterleaved[i * channels + ch]) / 32768.0f;
            }
        }

        env->ReleaseByteArrayElements(pcmBytes, byteArray, JNI_ABORT);

        int outputSamples = static_cast<int>(static_cast<float>(inputSamples) / playbackSpeedFactor);
        std::vector<std::vector<float>> outputBuffers(channels, std::vector<float>(outputSamples));

        std::vector<float *> inputPtrs(channels);
        std::vector<float *> outputPtrs(channels);
        for (int ch = 0; ch < channels; ++ch) {
            inputPtrs[ch] = inputBuffers[ch].data();
            outputPtrs[ch] = outputBuffers[ch].data();
        }

        stretch->process(inputPtrs, inputSamples, outputPtrs, outputSamples);

        std::vector<int16_t> outputInterleaved(outputSamples * channels);
        for (int i = 0; i < outputSamples; ++i) {
            for (int ch = 0; ch < channels; ++ch) {
                float sample = outputBuffers[ch][i] * 32768.0f;
                sample = std::max(-32768.0f, std::min(32767.0f, sample));
                outputInterleaved[i * channels + ch] = static_cast<int16_t>(sample);
            }
        }

        jbyteArray result = env->NewByteArray(static_cast<jsize>(outputSamples * channels * sizeof(int16_t)));
        if (!result) {
            throw std::runtime_error("Failed to create output byte array");
        }

        env->SetByteArrayRegion(result, 0, static_cast<jsize>(outputSamples * channels * sizeof(int16_t)),
                                reinterpret_cast<const jbyte *>(outputInterleaved.data()));

        return result;
    } catch (const std::exception &e) {
        handleException(env, e.what());
        return nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_flushNative(JNIEnv *env, jclass thisClass,
                                                                              jlong handle, int channels) {
    std::unique_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        int outputSamples = stretch->outputLatency();

        std::vector<std::vector<float>> outputBuffers(channels, std::vector<float>(outputSamples, 0.0f));

        stretch->flush(outputBuffers, outputSamples);
    } catch (const std::exception &e) {
        handleException(env, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_resetNative(JNIEnv *env, jclass thisClass,
                                                                              jlong handle) {
    std::unique_lock<std::shared_mutex> lock(mutex);

    try {
        auto stretch = getPointer(handle);

        stretch->reset();
    } catch (const std::exception &e) {
        handleException(env, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_freeNative(JNIEnv *env, jclass thisClass,
                                                                             jlong handle) {
    std::unique_lock<std::shared_mutex> lock(mutex);

    try {
        if (pointers.erase(handle) == 0) {
            handleException(env, "Unable to free native pointer");
        }
    } catch (const std::exception &e) {
        handleException(env, e.what());
    }
}