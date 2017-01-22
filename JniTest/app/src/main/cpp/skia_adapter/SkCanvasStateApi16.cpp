//
// Created by gaoqingguang on 2017/1/12.
//
#include <cstdint>
#include <cstdlib>
#include "SkCanvasState.h"
#include "SkiaApi16.h"
#include "SkiaCommon.h"
#include "SkCanvasStateApi16.h"

using namespace skia_api16;

class SkCanvasStateApi16 : public SkCanvasState {
public:
    static const int32_t kVersion = 1;

    SkCanvasStateApi16(skia_api16::SkCanvas *canvas) {
        originalCanvas = canvas; //SkRef(canvas);
        width = canvas->getDeviceSize().width();
        height = canvas->getDeviceSize().height();
    }

private:
};

////////////////////////////////////////////////////////////////////////////////

static void setup_MC_state(SkMCState *state, const SkMatrix &matrix, const SkRegion &clip) {
    // initialize the struct
    state->clipRectCount = 0;

    // capture the matrix
    for (int i = 0; i < 9; i++) {
        state->matrix[i] = matrix.get(i);
    }

    /*
     * capture the clip
     *
     * storage is allocated on the stack for the first 4 rects. This value was
     * chosen somewhat arbitrarily, but does allow us to represent simple clips
     * and some more common complex clips (e.g. a clipRect with a sub-rect
     * clipped out of its interior) without needing to malloc any additional memory.
     */
    const int clipBufferSize = 4 * sizeof(ClipRect);
    char clipBuffer[clipBufferSize];
    SkWriter32 clipWriter(sizeof(ClipRect), clipBuffer, clipBufferSize);

    if (!clip.isEmpty()) {
        // only returns the b/w clip so aa clips fail
        SkRegion::Iterator clip_iterator(clip);
        for (; !clip_iterator.done(); clip_iterator.next()) {
            // this assumes the SkIRect is stored in l,t,r,b ordering which
            // matches the ordering of our ClipRect struct
            clipWriter.writeIRect(clip_iterator.rect());
            state->clipRectCount++;
        }
    }

    // allocate memory for the clip then and copy them to the struct
    state->clipRects = (ClipRect *) sk_malloc(clipWriter.size());
    clipWriter.flatten(state->clipRects);
}



SkCanvasState *SkCanvasStateFactoryApi16::CaptureCanvasState(void *p) {
    SkCanvas* canvas = (SkCanvas*)p;

    SkCanvasStateApi16 *canvasState(SkNEW_ARGS(SkCanvasStateApi16, (canvas)));

    // decompose the total matrix and clip
    setup_MC_state(&canvasState->mcState, canvas->getTotalMatrix(), canvas->getTotalClip());

    /*
     * decompose the layers
     *
     * storage is allocated on the stack for the first 3 layers. It is common in
     * some view systems (e.g. Android) that a few non-clipped layers are present
     * and we will not need to malloc any additional memory in those cases.
     */
    const int layerBufferSize = 3 * sizeof(SkCanvasLayerState);
    char layerBuffer[layerBufferSize];
    SkWriter32 layerWriter(sizeof(SkCanvasLayerState), layerBuffer, layerBufferSize);
    int layerCount = 0;
    for (SkCanvas::LayerIter layer(canvas,
                                   true/*skipEmptyClips*/); !layer.done(); layer.next()) {

        // we currently only work for bitmap backed devices
        const SkBitmap &bitmap = layer.device()->accessBitmap(true/*changePixels*/);
        if (bitmap.empty() || bitmap.isNull() || !bitmap.lockPixelsAreWritable()) {
            return NULL;
        }

        SkCanvasLayerState *layerState =
                (SkCanvasLayerState *) layerWriter.reserve(sizeof(SkCanvasLayerState));
        layerState->type = kRaster_CanvasBackend;
        layerState->x = layer.x();
        layerState->y = layer.y();
        layerState->width = bitmap.width();
        layerState->height = bitmap.height();

        switch (bitmap.config()) {
            case SkBitmap::kARGB_8888_Config:
                layerState->raster.config = kARGB_8888_RasterConfig;
                break;
            case SkBitmap::kRGB_565_Config:
                layerState->raster.config = kRGB_565_RasterConfig;
                break;
            default:
                return NULL;
        }
        layerState->raster.rowBytes = bitmap.rowBytes();
        layerState->raster.pixels = bitmap.getPixels();

        setup_MC_state(&layerState->mcState, layer.matrix(), layer.clip());
        layerCount++;
    }

    // allocate memory for the layers and then and copy them to the struct
    SkASSERT(layerWriter.size() == layerCount * sizeof(SkCanvasLayerState));
    canvasState->layerCount = layerCount;
    canvasState->layers = (SkCanvasLayerState *) sk_malloc(layerWriter.size());
    layerWriter.flatten(canvasState->layers);

    //return canvasState.detach();
    return (SkCanvasState *) canvasState;
}


void SkCanvasStateFactoryApi16::ReleaseCanvasState(SkCanvasState* state) {

};


