cmake_minimum_required(VERSION 3.24)
project(stretch VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 23)

set(CMAKE_SHARED_LIBRARY_PREFIX "")

add_library(stretch-signalsmith SHARED src/Java_com_github_numq_stretch_signalsmith_NativeSignalsmithStretch.cpp)

find_package(JNI)

if (JNI_FOUND)
    message(STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message(STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
    target_include_directories(stretch-signalsmith PRIVATE ${JNI_INCLUDE_DIRS})
else ()
    message(FATAL_ERROR "JNI not found.")
endif ()

target_include_directories(stretch-signalsmith PRIVATE include)