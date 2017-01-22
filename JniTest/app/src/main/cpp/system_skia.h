//
// Created by gaoqingguang on 2016/12/13.
//

#ifndef STUDYJNI_SYSTEMSKIA_H
#define STUDYJNI_SYSTEMSKIA_H


#include "dynamic_library.h"

class SkCanvasState;
class SkCanvas;

class SkCanvasState_v1;

class SystemSkia {
public:
    static SystemSkia* Create(int android_version);
    static void Release(SystemSkia* skia);

    SkCanvasState* CaptureCanvasState(SkCanvas* systemCanvas);
    void ReleaseCanvasState(SkCanvasState* canvasState);


protected:
    SystemSkia();
    ~SystemSkia();

    virtual bool convertCanvasState(SkCanvasState_v1* target, SkCanvasState* systemCanvasState) {
        return false;
    }

    virtual void releaseCanvasStateBefore(SkCanvasState_v1* v1) {}

    // SK_API SkCanvasState* CaptureCanvasState(SkCanvas* canvas);
    typedef void*	(*CaptureCanvasStateFunc)	(void* canvas);
    CaptureCanvasStateFunc m_fnCaptureCanvasState;

    // SK_API void ReleaseCanvasState(SkCanvasState* state);
    typedef void (*ReleaseCanvasStateFunc) (void* state);
    ReleaseCanvasStateFunc m_fnReleaseCanvasState;

    DynamicLibrary library;
};


#endif //STUDYJNI_SYSTEMSKIA_H
