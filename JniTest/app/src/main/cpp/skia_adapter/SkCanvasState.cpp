//
// Created by gaoqingguang on 2017/1/12.
//

#include <cstdlib>
#include "SkCanvasState.h"
#include "SkiaCommon.h"
#include "../android_utils.h"
#include "SkCanvasStateApi16.h"


SkCanvasState::~SkCanvasState() {
    // loop through the layers and free the data allocated to the clipRects
    for (int i = 0; i < layerCount; ++i) {
        sk_free(layers[i].mcState.clipRects);
    }

    sk_free(mcState.clipRects);
    sk_free(layers);

    // it is now safe to free the canvas since there should be no remaining
    // references to the content that is referenced by this canvas (e.g. pixels)
    //originalCanvas->unref();
}


SkCanvasStateFactory* SkCanvasStateFactory::sInstance = NULL;

SkCanvasStateFactory* SkCanvasStateFactory::Instance() {
    if (NULL == sInstance) {
        switch (plat_support::getAndroidVersion()) {
            case 16:
            default:
                sInstance = new SkCanvasStateFactoryApi16();
                break;
        }
    }

    return sInstance;
}


SkCanvasState* SkCanvasStateFactory::CaptureCanvasState(void *canvas) {
    return NULL;
}

void SkCanvasStateFactory::ReleaseCanvasState(SkCanvasState* state) {
    delete state;
}

