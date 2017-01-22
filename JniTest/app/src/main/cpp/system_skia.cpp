//
// Created by gaoqingguang on 2016/12/13.
//

#include "system_skia.h"
#include "dynamic_library.h"

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

    void* systemCanvasState; //
    typedef SkCanvasState INHERITED;
};


// platform implement
// android 4.4.2
class SystemSkiaApi19 : public SystemSkia{
public:
    SystemSkiaApi19() {}

private:
    struct SkCanvasLayerStateApi19 {
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

    class SkCanvasStateApi19 {
    public:
        static const int32_t kVersion = 1;

        int32_t version;
        int32_t width;
        int32_t height;

        SkMCState mcState;

        int32_t layerCount;
        SkCanvasLayerStateApi19 *layers;
    private:
        void *originalCanvas;
    };

    bool convertCanvasState(SkCanvasState_v1* v1, SkCanvasState* systemCanvasState) override {
        SkCanvasStateApi19* canvasState19 = (SkCanvasStateApi19*)systemCanvasState;

        v1->mcState = canvasState19->mcState;
        v1->layerCount = canvasState19->layerCount;
        if (v1->layerCount > 0) {
            size_t sizeBytes = sizeof(SkCanvasLayerState) * v1->layerCount;
            v1->layers = (SkCanvasLayerState*)malloc(sizeBytes);
            memset(v1->layers, 0, sizeBytes);
        }

        for (int i=0; i<v1->layerCount; i++) {
            SkCanvasLayerState& layer = v1->layers[i];
            SkCanvasLayerStateApi19& layer19 = canvasState19->layers[i];
            layer.type = layer19.type;
            layer.x = layer19.x;
            layer.y = layer19.y;
            layer.width = layer19.width;
            layer.height = layer19.height;
            layer.raster.config = layer19.raster.config;
            layer.raster.pixels = layer19.raster.pixels;
            layer.raster.rowBytes = layer19.raster.rowBytes;
            layer.mcState = layer19.mcState;
        }

        return true;
    }

    void releaseCanvasStateBefore(SkCanvasState_v1* v1) override {
        if (v1->layers) {
            free(v1->layers);
        }
    }
};

// android 6.0
class SystemSkiaApi23 : public SystemSkia{
public:
    SystemSkiaApi23() {}

private:
    bool convertCanvasState(SkCanvasState_v1* v1, SkCanvasState* systemCanvasState) override {
        memcpy(v1, systemCanvasState, sizeof(SkCanvasState_v1) - sizeof(void*));
        return true;
    }
};



SystemSkia::SystemSkia() :
        library("libskia.so")
{
    m_fnCaptureCanvasState = (CaptureCanvasStateFunc)library.getFunctionPtr("_ZN18SkCanvasStateUtils18CaptureCanvasStateEP8SkCanvas");
    m_fnReleaseCanvasState = (ReleaseCanvasStateFunc)library.getFunctionPtr("_ZN18SkCanvasStateUtils18ReleaseCanvasStateEP13SkCanvasState");
}


SystemSkia::~SystemSkia() {

}

SystemSkia* SystemSkia::Create(int android_version) {
    SystemSkia* systemSkia = NULL;
    switch(android_version) {
        case 16:
        case 17:
        case 18:
        case 19:
            systemSkia = new SystemSkiaApi19();
            break;
        case 23:
        default:
            systemSkia = new SystemSkiaApi23();
            break;
    }

    return systemSkia;
}

void SystemSkia::Release(SystemSkia* skia) {
    delete skia;
}

SkCanvasState* SystemSkia::CaptureCanvasState(SkCanvas* systemCanvas) {
    SkCanvasState* systemCanvasState = NULL;
    if (m_fnCaptureCanvasState) {
        systemCanvasState = (SkCanvasState*)m_fnCaptureCanvasState(systemCanvas);
    }

    SkCanvasState_v1* canvasState_v1 = new SkCanvasState_v1();
    canvasState_v1->version = systemCanvasState->version;
    canvasState_v1->width = systemCanvasState->width;
    canvasState_v1->height = systemCanvasState->height;
    canvasState_v1->alignmentPadding = 0;

    convertCanvasState(canvasState_v1, systemCanvasState);
    canvasState_v1->systemCanvasState = systemCanvasState;

    return canvasState_v1;
}

void SystemSkia::ReleaseCanvasState(SkCanvasState* canvasState) {
    SkCanvasState_v1* canvasState_v1 = (SkCanvasState_v1*)canvasState;

    releaseCanvasStateBefore(canvasState_v1);

    if (m_fnReleaseCanvasState && canvasState_v1->systemCanvasState) {
        m_fnReleaseCanvasState(canvasState_v1->systemCanvasState);
    }

    delete canvasState_v1;
}

