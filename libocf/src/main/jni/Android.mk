LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CPPFLAGS += -std=c++11
LOCAL_MODULE    := test
LOCAL_SRC_FILES := test.cpp
LOCAL_LDLIBS    := -llog

LOCAL_C_INCLUDES += /home/pawwik/dev/libcoap/
LOCAL_C_INCLUDES += /home/pawwik/dev/liboic/
LOCAL_C_INCLUDES += /home/pawwik/dev/std

LOCAL_STATIC_LIBRARIES  += libcoap
LOCAL_STATIC_LIBRARIES  += liboic


include $(BUILD_SHARED_LIBRARY)



LOCAL_PATH := $(abspath /home/pawwik/dev/libcoap/)

include $(CLEAR_VARS)
LOCAL_CPPFLAGS += -std=c++11
LOCAL_MODULE := libcoap
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/include/
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/include/bits/
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi/include/

LOCAL_C_INCLUDES += /home/pawwik/dev/std
LOCAL_SRC_FILES := COAPObserver.cpp COAPOption.cpp COAPPacket.cpp COAPServer.cpp
include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(abspath /home/pawwik/dev/liboic/)

include $(CLEAR_VARS)
LOCAL_CPPFLAGS += -std=c++11
LOCAL_MODULE := liboic
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/include/
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/include/bits/
LOCAL_C_INCLUDES += /home/pawwik/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi/include/
LOCAL_C_INCLUDES += /home/pawwik/dev/libcoap/
LOCAL_C_INCLUDES += /home/pawwik/dev/std

LOCAL_STATIC_LIBRARIES  := libcoap
LOCAL_SRC_FILES := OICBase.cpp OICClient.cpp OICDevice.cpp OICDeviceResource.cpp OICResource.cpp OICServer.cpp
include $(BUILD_SHARED_LIBRARY)
