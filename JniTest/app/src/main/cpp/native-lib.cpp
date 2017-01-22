#include <jni.h>
#include <string>

#include "android_utils.h"
#include "system_skia.h"
#include "skia_adapter/SkCanvasState.h"

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

    class Canvas{
            public:
            virtual ~Canvas() { };
            virtual void* asSkCanvas() = 0;
    };

    void* getSkCanvas(JNIEnv* env, jobject jcanvas) {
        void* skcanvas = NULL;
        if (isLongOfNativeCanvas()) {
            jlong canvasHandle = env->GetLongField(jcanvas, gCanvas_nativeInstanceID);
            Canvas* canvas = reinterpret_cast<Canvas*>(canvasHandle);
            if (NULL != canvas) {
                skcanvas = canvas->asSkCanvas();
            }
        } else {
            skcanvas = (void*)env->GetIntField(jcanvas, gCanvas_nativeInstanceID);
        }
        return skcanvas;
    }

} //namespace GraphicsJNI




extern "C" {
jint JNI_OnLoad(JavaVM *vm, void *reserved);
void Java_com_yiming_jnitest_CustomView_initFromJNI(JNIEnv *env, jobject, int);
jstring Java_com_yiming_jnitest_CustomView_stringFromJNI(JNIEnv *env, jobject /* this */);
jstring Java_com_yiming_jnitest_CustomView_drawCanvasJNI(JNIEnv *env, jobject, jobject java_canvas);
void Java_com_yiming_jnitest_CustomView_nativeCallNative(JNIEnv* env, jobject /* this */, jstring path);
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
    //printf("result = %d", result);
    char buf[256];
    sprintf(buf, "result = %d;", 3);
    hello = buf;
    return env->NewStringUTF(hello.c_str());
}

static SystemSkia* g_systemSkia = NULL;
jstring
Java_com_yiming_jnitest_CustomView_drawCanvasJNI(
        JNIEnv* env,
        jobject /* this */, jobject java_canvas) {
    std::string hello = "Hello from C++";

    void* ptr_canvas = GraphicsJNI::getSkCanvas(env, java_canvas);
    SkCanvasState *ptr = NULL;
    if (plat_support::getAndroidVersion() <= 18) {
        ptr = SkCanvasStateFactory::Instance()->CaptureCanvasState(ptr_canvas);
        SkCanvasStateFactory::Instance()->ReleaseCanvasState(ptr);
    } else {
        if (!g_systemSkia)
            g_systemSkia = SystemSkia::Create(plat_support::getAndroidVersion());

        ptr = g_systemSkia->CaptureCanvasState((SkCanvas *) ptr_canvas);
        g_systemSkia->ReleaseCanvasState(ptr);
    }

    return env->NewStringUTF(hello.c_str());
}

void Java_com_yiming_jnitest_CustomView_nativeCallNative(JNIEnv* env, jobject /* this */, jstring path) {
    const char* filename = env->GetStringUTFChars(path, NULL);
    FILE* result = NULL;
    result = fopen(filename, "w+");
    if (result) {
        printf("ok");
    } else {
        printf("no");
    }

    env->ReleaseStringUTFChars(path, filename);
}

