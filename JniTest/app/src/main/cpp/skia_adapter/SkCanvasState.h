//
// Created by gaoqingguang on 2017/1/12.
//
#ifndef SK_CANVAS_STATE_H__
#define SK_CANVAS_STATE_H__


class SkCanvasState;

/**
 * A set of functions that are useful for copying an SkCanvas across a library
 * boundary where the Skia libraries on either side of the boundary may not be
 * version identical.  The expected usage is outline below...
 *
 *                          Lib Boundary
 * CaptureCanvasState(...)      |||
 *   SkCanvas --> SkCanvasState |||
 *                              ||| CreateFromCanvasState(...)
 *                              |||   SkCanvasState --> SkCanvas`
 *                              ||| Draw into SkCanvas`
 *                              ||| Unref SkCanvas`
 * ReleaseCanvasState(...)      |||
 *
 */
enum RasterConfigs {
    kUnknown_RasterConfig   = 0,
    kRGB_565_RasterConfig   = 1,
    kARGB_8888_RasterConfig = 2
};
typedef int32_t RasterConfig;

enum CanvasBackends {
    kUnknown_CanvasBackend = 0,
    kRaster_CanvasBackend  = 1,
    kGPU_CanvasBackend     = 2,
    kPDF_CanvasBackend     = 3
};
typedef int32_t CanvasBackend;

struct ClipRect {
    int32_t left, top, right, bottom;
};

struct SkMCState {
    float matrix[9];
    // NOTE: this only works for non-antialiased clips
    int32_t clipRectCount;
    ClipRect* clipRects;
};

// NOTE: If you add more members, create a new subclass of SkCanvasState with a
// new CanvasState::version.
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
            void* pixels;        // The pixels, all (height * rowBytes) of them.
        } raster;
        struct {
            int32_t textureID;
        } gpu;
    };
};

class SkCanvasState {
public:
    static const int32_t kVersion = 1;
    SkCanvasState() { //int32_t version, SkCanvas* canvas) {
        this->version = kVersion;
    }

    ~SkCanvasState();

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


    SkMCState mcState;
    int32_t layerCount;
    SkCanvasLayerState* layers;
protected:
    void* originalCanvas;
};


class SkCanvasStateFactory {
public:
    static SkCanvasStateFactory* Instance();

    virtual SkCanvasState* CaptureCanvasState(void* canvas);
    virtual void ReleaseCanvasState(SkCanvasState* state);

protected:
    SkCanvasStateFactory() {}

private:
    static SkCanvasStateFactory* sInstance;
}; //end namespace SkCanvasStateUtils


#endif // SK_CANVAS_STATE_H__

