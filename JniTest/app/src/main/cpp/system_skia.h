//
// Created by gaoqingguang on 2016/12/13.
//

#ifndef STUDYJNI_SYSTEMSKIA_H
#define STUDYJNI_SYSTEMSKIA_H


#include "dynamic_library.h"

class SystemSkia {
public:
    SystemSkia();
    ~SystemSkia();

    void* CaptureCanvasState(void* systemCanvas);
    void ReleaseCanvasState(void* canvasState);

private:
    // SK_API SkCanvasState* CaptureCanvasState(SkCanvas* canvas);
    typedef void*	(*CaptureCanvasStateFunc)	(void* buffer);
    CaptureCanvasStateFunc m_fnCaptureCanvasState;

    // SK_API void ReleaseCanvasState(SkCanvasState* state);
    typedef void (*ReleaseCanvasStateFunc) (void* ptr);
    ReleaseCanvasStateFunc m_fnReleaseCanvasState;

    DynamicLibrary library;
};


#endif //STUDYJNI_SYSTEMSKIA_H
