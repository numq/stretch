#include <jni.h>
#include <iostream>
#include <shared_mutex>
#include <mutex>
#include <unordered_map>
#include <memory>
#include "signalsmith-stretch.h"
#include "deleter.h"

#ifndef _Included_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch
#define _Included_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_initNative
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_getInputLatencyNative
        (JNIEnv *, jclass, jlong);

JNIEXPORT jint JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_getOutputLatencyNative
        (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_configureNative
        (JNIEnv *, jclass, jlong, jint, jint, jint);

JNIEXPORT jbyteArray JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_processNative
        (JNIEnv *, jclass, jlong, jbyteArray, jint, jfloat);

JNIEXPORT void JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_flushNative
        (JNIEnv *, jclass, jlong, int channels);

JNIEXPORT void JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_resetNative
        (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch_freeNative
        (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
