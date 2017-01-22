//
// Created by gaoqingguang on 2017/1/12.
//

#ifndef SKIA_API_16_H__
#define SKIA_API_16_H__

#include "SkiaCommon.h"

namespace skia_api16 {

    class SkTypeface;

    class SkPathEffect;

    class SkShader;

    class SkXfermode;

    class SkMaskFilter;

    class SkColorFilter;

    class SkRasterizer;

    class SkDrawLooper;

    class SkImageFilter;

    class SkBaseMutex;

    class SkColorTable;

    class SkMetaData;

    class SkRegion;

    class SkClipStack;

    class SkCanvas;

    class SkRasterClip;

    struct SkDrawProcs;
    struct DeviceCM;

    template<typename T>
    class SkAutoTDelete : SkNoncopyable {
    public:
        SkAutoTDelete(T *obj, bool deleteWhenDone = true) : fObj(obj) {
            fDeleteWhenDone = deleteWhenDone;
        }

        ~SkAutoTDelete() { if (fDeleteWhenDone) delete fObj; }

        T *get() const { return fObj; }

        void free() {
            delete fObj;
            fObj = NULL;
        }

        T *detach() {
            T *obj = fObj;
            fObj = NULL;
            return obj;
        }

    private:
        T *fObj;
        bool fDeleteWhenDone;
    };


    class SK_API SkRefCnt : SkNoncopyable {
    public:
        SkRefCnt() : fRefCnt(1) { }

        virtual ~SkRefCnt() {
        }

        int32_t getRefCnt() const { return fRefCnt; }

        void ref() const {
            SkASSERT(fRefCnt > 0);
            sk_atomic_inc(&fRefCnt);
        }

        void unref() const {
            SkASSERT(fRefCnt > 0);
            if (sk_atomic_dec(&fRefCnt) == 1) {
                fRefCnt = 1;    // so our destructor won't complain
                SkDELETE(this);
            }
        }

        void validate() const {
            SkASSERT(fRefCnt > 0);
        }

    private:
        mutable int32_t fRefCnt;
    };


    struct SK_API Sk64 {
        int32_t fHi;   //!< the high 32 bits of the number (including sign)
        uint32_t fLo;   //!< the low 32 bits of the number
    };

    class SkString {
    public:
        SkString() { }

    private:
        struct Rec {
        public:
            size_t fLength;
            int32_t fRefCnt;
            char fBeginningOfData;

            char *data() { return &fBeginningOfData; }

            const char *data() const { return &fBeginningOfData; }
        };

        Rec *fRec;

        static const Rec gEmptyRec;
    };

    class SK_API SkPixelRef : public SkRefCnt {
    public:
        explicit SkPixelRef(void *mutex = NULL) { }

        virtual ~SkPixelRef() { }

        void *getTexture() { return NULL; }

        void notifyPixelsChanged();

        bool lockPixelsAreWritable() const;

        bool onLockPixelsAreWritable() const;

    private:
        SkBaseMutex *fMutex; // must remain in scope for the life of this object
        void *fPixels;
        SkColorTable *fColorTable;    // we do not track ownership, subclass does
        int32_t fLockCount;

        mutable uint32_t fGenerationID;

        SkString fURI;

        // can go from false to true, but never from true to false
        bool fIsImmutable;
        // only ever set in constructor, const after that
        bool fPreLocked;
    };

    class SK_API SkBitmap {
    public:
        SkBitmap() { }

        ~SkBitmap() { }

        enum Config {
            kNo_Config,         //!< bitmap has not been configured
            /**
             *  1-bit per pixel, (0 is transparent, 1 is opaque)
             *  Valid as a destination (target of a canvas), but not valid as a src.
             *  i.e. you can draw into a 1-bit bitmap, but you cannot draw from one.
             */
                    kA1_Config,
            kA8_Config,         //!< 8-bits per pixel, with only alpha specified (0 is transparent, 0xFF is opaque)
            kIndex8_Config,     //!< 8-bits per pixel, using SkColorTable to specify the colors
            kRGB_565_Config,    //!< 16-bits per pixel, (see SkColorPriv.h for packing)
            kARGB_4444_Config,  //!< 16-bits per pixel, (see SkColorPriv.h for packing)
            kARGB_8888_Config,  //!< 32-bits per pixel, (see SkColorPriv.h for packing)
            /**
             *  Custom compressed format, not supported on all platforms.
             *  Cannot be used as a destination (target of a canvas).
             *  i.e. you may be able to draw from one, but you cannot draw into one.
             */
                    kRLE_Index8_Config,

            kConfigCount
        };

        int32_t width() const { return fWidth; }

        int32_t height() const { return fHeight; }

        bool empty() const { return 0 == fWidth || 0 == fHeight; }

        bool isNull() const { return NULL == fPixels && NULL == fPixelRef; }

        bool lockPixelsAreWritable() const;

        Config config() const { return (Config) fConfig; }

        int32_t rowBytes() const { return fRowBytes; }

        void *getPixels() const { return fPixels; }

        void notifyPixelsChanged() const;

    private:
        struct MipMap;
        mutable MipMap *fMipMap;

        mutable SkPixelRef *fPixelRef;
        mutable size_t fPixelRefOffset;
        mutable int32_t fPixelLockCount;
        // either user-specified (in which case it is not treated as mutable)
        // or a cache of the returned value from fPixelRef->lockPixels()
        mutable void *fPixels;
        mutable SkColorTable *fColorTable;    // only meaningful for kIndex8
        // When there is no pixel ref (setPixels was called) we still need a
        // gen id for SkDevice implementations that may cache a copy of the
        // pixels (e.g. as a gpu texture)
        mutable int32_t fRawPixelGenerationID;

        enum Flags {
            kImageIsOpaque_Flag = 0x01,
            kImageIsVolatile_Flag = 0x02,
            kImageIsImmutable_Flag = 0x04
        };

        uint32_t fRowBytes;
        uint32_t fWidth;
        uint32_t fHeight;
        uint8_t fConfig;
        uint8_t fFlags;
        uint8_t fBytesPerPixel; // based on config
    };

    class SK_API SkDevice : public SkRefCnt {
    public:
        SkDevice(const SkBitmap &bitmap) { }

        virtual ~SkDevice() { }
        void setMatrixClip(const SkMatrix&, const SkRegion&,
                                   const SkClipStack&);
        int32_t width() const { return fBitmap.width(); }

        int32_t height() const { return fBitmap.height(); }

        void gainFocus(SkCanvas *, const SkMatrix &, const SkRegion &,
                       const SkClipStack &) { }

        const SkBitmap &onAccessBitmap(SkBitmap *);

        const SkBitmap &accessBitmap(bool changePixels);

        const SkIPoint &getOrigin() const { return fOrigin; }

        const SkBitmap &getBitmap() const { return fBitmap; }

    private:
        SkBitmap fBitmap;
        SkIPoint fOrigin;
        SkMetaData *fMetaData;
    };

#define SkRegion_gEmptyRunHeadPtr   ((SkRegion::RunHead*)-1)
#define SkRegion_gRectRunHeadPtr    0

    class SK_API SkRegion {
    public:
        typedef int32_t RunType;
        enum {
            kRunTypeSentinel = 0x7FFFFFFF
        };
        enum Op {
            kDifference_Op, //!< subtract the op region from the first region
            kIntersect_Op,  //!< intersect the two regions
            kUnion_Op,      //!< union (inclusive-or) the two regions
            kXOR_Op,        //!< exclusive-or the two regions
            /** subtract the first region from the op region */
                    kReverseDifference_Op,
            kReplace_Op     //!< replace the dst region with the op region
        };

        bool isEmpty() const { return fRunHead == SkRegion_gEmptyRunHeadPtr; }

        SkRegion() { }
        explicit SkRegion(const SkIRect&);

        bool setRect(const SkIRect &);

        bool setRect(int32_t left, int32_t top, int32_t right, int32_t bottom);

        bool setEmpty();

        bool isRect() const { return fRunHead == SkRegion_gRectRunHeadPtr; }
        void translate(int dx, int dy) { this->translate(dx, dy, this); }
        void translate(int dx, int dy, SkRegion* dst) const;
        void swap(SkRegion&);
        bool op(const SkIRect& rect, Op op) { return this->op(*this, rect, op); }
        bool op(const SkRegion& rgn, const SkIRect& rect, Op);
        bool op(const SkRegion& rgna, const SkRegion& rgnb, Op op);
        bool set(const SkRegion& src) {
            SkASSERT(&src);
            *this = src;
            return !this->isEmpty();
        }
        bool setRegion(const SkRegion&);

        class SK_API Iterator {
        public:
            Iterator() : fRgn(NULL), fDone(true) { }

            Iterator(const SkRegion &);

            // if we have a region, reset to it and return true, else return false
            bool rewind();

            // reset the iterator, using the new region
            void reset(const SkRegion &);

            bool done() const { return fDone; }

            void next();

            const SkIRect &rect() const { return fRect; }

            // may return null
            const SkRegion *rgn() const { return fRgn; }

        private:
            const SkRegion *fRgn;
            const RunType *fRuns;
            SkIRect fRect;
            bool fDone;
        };

    private:
        enum {
            kRectRegionRuns = 6 // need to store a region of a rect [T B L R S S]
        };
        //friend class android::Region;    // needed for marshalling efficiently

        struct RunHead;

        void freeRuns();
        void allocateRuns(int count);
        const RunType*  getRuns(RunType tmpStorage[], int* count) const;
        bool            setRuns(RunType runs[], int count);
        static void BuildRectRuns(const SkIRect& bounds,
                                  RunType runs[kRectRegionRuns]);
        static bool ComputeRunBounds(const RunType runs[], int count,
                                     SkIRect* bounds);

        SkIRect fBounds;
        RunHead *fRunHead;

    };

    class SK_API SkPaint {
    public:
        SkPaint() { }

        ~SkPaint() { }

    private:
        SkTypeface *fTypeface;
        SkScalar fTextSize;
        SkScalar fTextScaleX;
        SkScalar fTextSkewX;

        SkPathEffect *fPathEffect;
        SkShader *fShader;
        SkXfermode *fXfermode;
        SkMaskFilter *fMaskFilter;
        SkColorFilter *fColorFilter;
        SkRasterizer *fRasterizer;
        SkDrawLooper *fLooper;
        SkImageFilter *fImageFilter;

        SkColor fColor;
        SkScalar fWidth;
        SkScalar fMiterLimit;
        unsigned fFlags : 15;
        unsigned fTextAlign : 2;
        unsigned fCapType : 2;
        unsigned fJoinType : 2;
        unsigned fStyle : 2;
        unsigned fTextEncoding : 2;  // 3 values
        unsigned fHinting : 2;
//#ifdef SK_BUILD_FOR_ANDROID
        SkString fTextLocale;
//#endif

        enum {
            kCanonicalTextSizeForPaths = 64
        };

//#ifdef SK_BUILD_FOR_ANDROID
        uint32_t fGenerationID;
//#endif
    };

    class SK_API SkDeque : SkNoncopyable {
    public:
        explicit SkDeque() { }

        ~SkDeque() { }

    private:
        struct Head;
    public:
        class F2BIter {
        public:
            /**
             * Creates an uninitialized iterator. Must be reset()
             */
            F2BIter();

            F2BIter(const SkDeque &d);

            void *next();

            void reset(const SkDeque &d);

        private:
            SkDeque::Head *fHead;
            char *fPos;
            size_t fElemSize;
        };

    private:
        Head *fFront;
        Head *fBack;
        size_t fElemSize;
        void *fInitialStorage;
        int32_t fCount;
    };


    class SK_API SkClipStack {
    public:
        SkClipStack() { }

        ~SkClipStack() { }

    private:
        struct Rec;

        SkDeque fDeque;
        int32_t fSaveCount;
    };

    class SkBounder : public SkRefCnt {
    private:
        SkBounder() { }

        ~SkBounder() { }

    public:
        void setClip(const SkRegion *clip) { fClip = clip; }

    private:
        const SkRegion *fClip;
    };


    class SkAAClip {
    public:
        SkAAClip() {}

        const SkIRect &getBounds() const { return fBounds; }

        bool isEmpty() const { return NULL == fRunHead; }
        bool setEmpty() {
            this->freeRuns();
            fBounds.setEmpty();
            fRunHead = NULL;
            return false;
        }
        void freeRuns();
        bool translate(int dx, int dy, SkAAClip* dst) const;
        bool set(const SkAAClip& src) {
            *this = src;
            return !this->isEmpty();
        }
        bool op(const SkAAClip& clipAOrig, const SkAAClip& clipBOrig,
                SkRegion::Op op);

        struct RunHead;
        struct YOffset;
    private:
        SkIRect fBounds;
        RunHead *fRunHead;

        friend class Builder;

        class BuilderBlitter;

        friend class BuilderBlitter;
    };

    class SK_API SkCanvas : public SkRefCnt {
    private:
        SkCanvas() { }

        virtual ~SkCanvas() { }

    public:
        SkDevice *getDevice() const;

        SkISize getDeviceSize() const;

        const SkMatrix &getTotalMatrix() const;

        const SkRegion &getTotalClip() const;

        SkBounder *getBounder() const { return fBounder; }

        //SkDrawFilter* getDrawFilter() const;
        SkCanvas *canvasForDrawIter() {
            return this;
        }

        const SkClipStack &getTotalClipStack() const {
            return fClipStack;
        }

        class SK_API LayerIter /*: SkNoncopyable*/ {
        public:
            /** Initialize iterator with canvas, and set values for 1st device */
            LayerIter(SkCanvas *, bool skipEmptyClips);

            ~LayerIter();

            /** Return true if the iterator is done */
            bool done() const { return fDone; }

            /** Cycle to the next device */
            void next();

            // These reflect the current device in the iterator

            SkDevice *device() const;

            const SkMatrix &matrix() const;

            const SkRegion &clip() const;

            const SkPaint &paint() const;

            int32_t x() const;

            int32_t y() const;

        private:
            intptr_t fStorage[32];

            class SkDrawIter *fImpl;    // this points at fStorage
            SkPaint fDefaultPaint;
            bool fDone;
        };

    private:
        class MCRec;

        SkClipStack fClipStack;
        SkDeque fMCStack;
        // points to top of stack
        MCRec *fMCRec;
        // the first N recs that can fit here mean we won't call malloc
        uint32_t fMCRecStorage[32];

        SkBounder *fBounder;
        SkDevice *fLastDeviceToGainFocus;
        int32_t fLayerCount;    // number of successful saveLayer calls
        bool fDeviceCMDirty;            // cleared by updateDeviceCMCache()
        void updateDeviceCMCache();

        void prepareForDeviceDraw(SkDevice *, const SkMatrix &, const SkRegion &,
                                  const SkClipStack &clipStack);

        friend class SkDrawIter;    // needs setupDrawForLayerDevice()

        /*  These maintain a cache of the clip bounds in local coordinates,
            (converted to 2s-compliment if floats are slow).
         */
        mutable SkRectCompareType fLocalBoundsCompareType;
        mutable bool fLocalBoundsCompareTypeDirty;

        mutable SkRectCompareType fLocalBoundsCompareTypeBW;
        mutable bool fLocalBoundsCompareTypeDirtyBW;

        SkMatrix fExternalMatrix, fExternalInverse;
        bool fUseExternalMatrix;

    };

    class SkDraw {
    public:
        SkDraw() { }

    public:
        const SkBitmap *fBitmap;        // required
        const SkMatrix *fMatrix;        // required
        const SkRegion *fClip;          // DEPRECATED
        const SkRasterClip *fRC;        // required

        const SkClipStack *fClipStack;  // optional
        SkDevice *fDevice;        // optional
        SkBounder *fBounder;       // optional
        SkDrawProcs *fProcs;         // optional

        const SkMatrix *fMVMatrix;      // optional
        const SkMatrix *fExtMatrix;     // optional
    };

} // namespace skia_api16

#endif  //end SKIA_API_16_H__