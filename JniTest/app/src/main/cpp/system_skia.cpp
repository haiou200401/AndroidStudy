//
// Created by gaoqingguang on 2016/12/13.
//

#include "system_skia.h"
#include "dynamic_library.h"

namespace {

    enum RasterConfigs {
        kUnknown_RasterConfig = 0,
        kRGB_565_RasterConfig = 1,
        kARGB_8888_RasterConfig = 2
    };
    typedef int32_t RasterConfig;

    enum CanvasBackends {
        kUnknown_CanvasBackend = 0,
        kRaster_CanvasBackend = 1,
        kGPU_CanvasBackend = 2,
        kPDF_CanvasBackend = 3
    };
    typedef int32_t CanvasBackend;

    struct ClipRect {
        int32_t left, top, right, bottom;
    };

    struct SkMCState {
        float matrix[9];
        // NOTE: this only works for non-antialiased clips
        int32_t clipRectCount;
        ClipRect *clipRects;
    };


    struct SkCanvasLayerState {
        CanvasBackend type;
        int32_t x, y;
        int32_t width;
        int32_t height;

        SkMCState mcState;

        union {
            struct {
                RasterConfig config; // pixel format: a value from RasterConfigs.
                uint64_t rowBytes;   // Number of bytes from start of one line to next.
                void *pixels;        // The pixels, all (height * rowBytes) of them.
            } raster;
            struct {
                int32_t textureID;
            } gpu;
        };
    };

    class SkCanvasState {
    public:
        SkCanvasState() {
            version = width = height = alignmentPadding = 0;
        }
        /**
         * The version this struct was built with.  This field must always appear
         * first in the struct so that when the versions don't match (and the
         * remaining contents and size are potentially different) we can still
         * compare the version numbers.
         */
        int32_t version;
        int32_t width;
        int32_t height;
        int32_t alignmentPadding;
    };

    class SkCanvasState_v1 : public SkCanvasState {
    public:
        SkCanvasState_v1() {
            layerCount = 0;
            layers = NULL;
            originalCanvas = NULL;
        }
        static const int32_t kVersion = 1;
        SkMCState mcState;

        int32_t layerCount;
        SkCanvasLayerState *layers;
//    private:
        void *originalCanvas;
        typedef SkCanvasState INHERITED;
    };


// NOTE: If you add more members, create a new subclass of SkCanvasState with a
// new CanvasState::version.
    struct SkCanvasLayerState_4_4_2 {
        CanvasBackend type;
        int32_t x, y;
        int32_t width;
        int32_t height;

        SkMCState mcState;

        union {
            struct {
                RasterConfig config; // pixel format: a value from RasterConfigs.
                uint32_t rowBytes;   // Number of bytes from start of one line to next.
                void *pixels;        // The pixels, all (height * rowBytes) of them.
            } raster;
            struct {
                int32_t textureID;
            } gpu;
        };
    };

    class SkCanvasState_4_4_2 {
    public:
        static const int32_t kVersion = 1;

        /**
         * The version this struct was built with.  This field must always appear
         * first in the struct so that when the versions don't match (and the
         * remaining contents and size are potentially different) we can still
         * compare the version numbers.
         */
        int32_t version;

        int32_t width;
        int32_t height;

        SkMCState mcState;

        int32_t layerCount;
        SkCanvasLayerState_4_4_2 *layers;
    private:
        void *originalCanvas;
    };

    SkCanvasState_v1* convertCanvasStateFor_4_4_2(SkCanvasState_4_4_2* canvasState442) {
        SkCanvasState_v1* v1 = new SkCanvasState_v1();

        v1->version = canvasState442->version;
        v1->width = canvasState442->width;
        v1->height = canvasState442->height;
        v1->alignmentPadding = 0;
        v1->mcState = canvasState442->mcState;
        v1->layerCount = canvasState442->layerCount;
        if (v1->layerCount > 0) {
            size_t sizeBytes = sizeof(SkCanvasLayerState) * v1->layerCount;
            v1->layers = (SkCanvasLayerState*)malloc(sizeBytes);
            memset(v1->layers, 0, sizeBytes);
        }

        for (int i=0; i<v1->layerCount; i++) {
            SkCanvasLayerState& layer = v1->layers[i];
            SkCanvasLayerState_4_4_2& layer442 = canvasState442->layers[i];
            layer.type = layer442.type;
            layer.x = layer442.x;
            layer.y = layer442.y;
            layer.width = layer442.width;
            layer.height = layer442.height;
            layer.raster.config = layer442.raster.config;
            layer.raster.pixels = layer442.raster.pixels;
            layer.raster.rowBytes = layer442.raster.rowBytes;
            layer.mcState = layer442.mcState;
        }

        v1->originalCanvas = (void*)canvasState442;

        return v1;
    }

} // end namespace

SystemSkia::SystemSkia() :
        library("libskia.so")
{
    m_fnCaptureCanvasState = (CaptureCanvasStateFunc)library.getFunctionPtr("_ZN18SkCanvasStateUtils18CaptureCanvasStateEP8SkCanvas");
    m_fnReleaseCanvasState = (ReleaseCanvasStateFunc)library.getFunctionPtr("_ZN18SkCanvasStateUtils18ReleaseCanvasStateEP13SkCanvasState");
}


SystemSkia::~SystemSkia() {

}

void* SystemSkia::CaptureCanvasState(void* systemCanvas) {
    SkCanvasState_4_4_2* canvasState442 = NULL;
    if (m_fnCaptureCanvasState) {
        canvasState442 = (SkCanvasState_4_4_2*)m_fnCaptureCanvasState(systemCanvas);
    }

    SkCanvasState_v1* canvasState_v1 = convertCanvasStateFor_4_4_2(canvasState442);

    return canvasState_v1;
}

void SystemSkia::ReleaseCanvasState(void* canvasState) {
    SkCanvasState_v1* canvasState_v1 = (SkCanvasState_v1*)canvasState;

    if (canvasState_v1->layers) {
        free(canvasState_v1->layers);
    }

    if (m_fnReleaseCanvasState && canvasState_v1->originalCanvas) {
        m_fnReleaseCanvasState(canvasState_v1->originalCanvas);
    }

    delete canvasState_v1;
}

