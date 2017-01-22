#ifndef ANDROID_OS_UTILS_H__
#define ANDROID_OS_UTILS_H__
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <sys/resource.h>

namespace plat_support {


//#define LOG_TAG "qihoowebview_plat_support"
#define CONDITION(cond)    (__builtin_expect((cond)!=0, 0))

#define LOG_ALWAYS_FATAL_IF(cond, ...) \
	( (CONDITION(cond)) \
	? ((void)__android_log_assert(#cond, LOG_TAG, ## __VA_ARGS__)) \
	: (void)0 )

#define ALOGE(...) (__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))


void initAndroidVersion(int version);
int getAndroidVersion();


}


#endif //#ifndef ANDROID_OS_UTILS_H__

