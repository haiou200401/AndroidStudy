#include <jni.h>
#include <string>

#include "mycppfile.h"
#include "system_skia.h"

namespace plat_support {


    static int g_android_version = 16;

    void initAndroidVersion(int version) {
        g_android_version = version;
    }

    int getAndroidVersion() {
        return g_android_version;
    }
}

namespace GraphicsJNI {

    static jclass   gCanvas_class;
    static jfieldID gCanvas_nativeInstanceID;


    bool isLongOfNativeCanvas() {
        // >= android 5.0
        return plat_support::getAndroidVersion() > 20;
    }

////////////////////////////////////////////////////////////////////////////////

    const char kCanvasClassName[] = "android/graphics/Canvas";
    int register_android_graphics_Graphics(JNIEnv* env) {
        gCanvas_class = env->FindClass(kCanvasClassName);
        if (isLongOfNativeCanvas()) {
            gCanvas_nativeInstanceID = env->GetFieldID(gCanvas_class, "mNativeCanvasWrapper", "J");
        } else {
            gCanvas_nativeInstanceID = env->GetFieldID(gCanvas_class, "mNativeCanvas", "I");
        }
        return 0;
    }

    void* getNativeCanvas(JNIEnv* env, jobject canvas) {
        void* c = (void*)env->GetIntField(canvas, gCanvas_nativeInstanceID);
        return c;
    }

} //namespace GraphicsJNI




extern "C" {
jint JNI_OnLoad(JavaVM *vm, void *reserved);
void Java_com_yiming_jnitest_CustomView_initFromJNI(JNIEnv *env, jobject, int);
jstring Java_com_yiming_jnitest_CustomView_stringFromJNI(JNIEnv *env, jobject /* this */);
jstring Java_com_yiming_jnitest_CustomView_drawCanvasJNI(JNIEnv *env, jobject, jobject java_canvas);
}

int g_android_version = 0;
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    vm->GetEnv((void**)&env, JNI_VERSION_1_4);
    if (env) {
        int android_version = env->GetVersion();
        int height = android_version & 0x0000FFFF;
        int low = android_version >> 16;
        g_android_version = low;
    }
    return JNI_VERSION_1_4;
}

void
Java_com_yiming_jnitest_CustomView_initFromJNI(
        JNIEnv *env,
        jobject /* this */, int version) {
    plat_support::initAndroidVersion(version);
    GraphicsJNI::register_android_graphics_Graphics(env);
}


jstring
Java_com_yiming_jnitest_CustomView_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    c1 c1;
    int result = c1.add(3, 8);
    //printf("result = %d", result);
    char buf[256];
    sprintf(buf, "result = %d;", result);
    hello = buf;
    return env->NewStringUTF(hello.c_str());
}

jstring
Java_com_yiming_jnitest_CustomView_drawCanvasJNI(
        JNIEnv* env,
        jobject /* this */, jobject java_canvas) {
    std::string hello = "Hello from C++";

    void* ptr_canvas = GraphicsJNI::getNativeCanvas(env, java_canvas);


    SystemSkia systemSkia;
    void* ptr = systemSkia.CaptureCanvasState(ptr_canvas);
    systemSkia.ReleaseCanvasState(ptr);

    return env->NewStringUTF(hello.c_str());
}
