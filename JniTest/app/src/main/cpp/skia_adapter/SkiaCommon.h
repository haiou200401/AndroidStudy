//
// Created by gaoqingguang on 2017/1/12.
//

#ifndef SKIA_COMMON_H__
#define SKIA_COMMON_H__



#include <cstdlib>

#define SK_API
#define SkASSERT(x)
#define SK_CRASH() *(int *)(uintptr_t)0 = 0
#define SK_INIT_TO_AVOID_WARNING    = 0

typedef float SkScalar;
#ifndef SkNEW
#  define SkNEW(type_name)                           (new type_name)
#  define SkNEW_ARGS(type_name, args)                (new type_name args)
#  define SkNEW_ARRAY(type_name, count)              (new type_name[(count)])
#  define SkNEW_PLACEMENT(buf, type_name)            (new (buf) type_name)
#  define SkNEW_PLACEMENT_ARGS(buf, type_name, args) (new (buf) type_name args)
#  define SkDELETE(obj)                              (delete (obj))
#  define SkDELETE_ARRAY(array)                      (delete[] (array))
#endif
#define sk_float_abs(x)         fabsf(x)
#define SkScalarAbs(x)          sk_float_abs(x)
#define SK_Scalar1              (1.0f)
#define SkScalarHalf(a)         ((a) * 0.5f)
#define SkToU8(x)   ((uint8_t)(x))
#define SK_MaxS32   0x7FFFFFFF
#define SK_MinS32   0x80000001

typedef uint32_t SkColor;
typedef uint8_t SkBool8;
#define SkToBool(cond)  ((cond) != 0)
#define SkIntToScalar(n)        ((float)(n))

union SkFloatIntUnion {
    float   fFloat;
    int32_t fSignBitInt;
};


static inline int32_t SkFloat2Bits(float x) {
    SkFloatIntUnion data;
    data.fFloat = x;
    return data.fSignBitInt;
}

static inline int32_t SkSignBitTo2sCompliment(int32_t x) {
    if (x < 0) {
        x &= 0x7FFFFFFF;
        x = -x;
    }
    return x;
}

static inline int32_t SkFloatAs2sCompliment(float x) {
    return SkSignBitTo2sCompliment(SkFloat2Bits(x));
}
#define SkScalarAs2sCompliment(x)    SkFloatAs2sCompliment(x)


static inline int32_t SkMax32(int32_t a, int32_t b) {
    if (a < b)
        a = b;
    return a;
}

static inline int32_t SkMin32(int32_t a, int32_t b) {
    if (a > b)
        a = b;
    return a;
}

int32_t sk_atomic_inc(int32_t* addr);
int32_t sk_atomic_dec(int32_t* addr);

void sk_free(void* p);
void* sk_malloc(size_t size);

static inline void sk_bzero(void* buffer, size_t size) {
    memset(buffer, 0, size);
}



class SK_API SkMatrix {
public:
    enum TypeMask {
        kIdentity_Mask      = 0,
        kTranslate_Mask     = 0x01,  //!< set if the matrix has translation
        kScale_Mask         = 0x02,  //!< set if the matrix has X or Y scale
        kAffine_Mask        = 0x04,  //!< set if the matrix skews or rotates
        kPerspective_Mask   = 0x08   //!< set if the matrix is in perspective
    };

    enum {
        kMScaleX,
        kMSkewX,
        kMTransX,
        kMSkewY,
        kMScaleY,
        kMTransY,
        kMPersp0,
        kMPersp1,
        kMPersp2
    };

    SkScalar get(int index) const {
        SkASSERT((unsigned)index < 9);
        return fMat[index];
    }
    TypeMask getPerspectiveTypeMaskOnly() const {
        if ((fTypeMask & kUnknown_Mask) &&
            !(fTypeMask & kOnlyPerspectiveValid_Mask)) {
            fTypeMask = this->computePerspectiveTypeMask();
        }
        return (TypeMask)(fTypeMask & 0xF);
    }
    bool hasPerspective() const {
        return SkToBool(this->getPerspectiveTypeMaskOnly() &
                        kPerspective_Mask);
    }
    bool isIdentity() const {
        return this->getType() == 0;
    }
    TypeMask getType() const {
        if (fTypeMask & kUnknown_Mask) {
            fTypeMask = this->computeTypeMask();
        }
        // only return the public masks
        return (TypeMask)(fTypeMask & 0xF);
    }

    bool setConcat(const SkMatrix& a, const SkMatrix& b);
    bool postTranslate(SkScalar dx, SkScalar dy);
    void setTranslate(SkScalar dx, SkScalar dy);
    bool postConcat(const SkMatrix& other);
    void reset();
private:
    enum {
        /** Set if the matrix will map a rectangle to another rectangle. This
            can be true if the matrix is scale-only, or rotates a multiple of
            90 degrees. This bit is not set if the matrix is identity.

            This bit will be set on identity matrices
        */
                kRectStaysRect_Mask = 0x10,

        /** Set if the perspective bit is valid even though the rest of
            the matrix is Unknown.
        */
                kOnlyPerspectiveValid_Mask = 0x40,

        kUnknown_Mask = 0x80,

        kORableMasks =  kTranslate_Mask |
                        kScale_Mask |
                        kAffine_Mask |
                        kPerspective_Mask,

        kAllMasks = kTranslate_Mask |
                    kScale_Mask |
                    kAffine_Mask |
                    kPerspective_Mask |
                    kRectStaysRect_Mask
    };
    uint8_t computePerspectiveTypeMask() const;
    uint8_t computeTypeMask() const;
    bool isTriviallyIdentity() const {
        if (fTypeMask & kUnknown_Mask) {
            return false;
        }
        return ((fTypeMask & 0xF) == 0);
    }
    void setTypeMask(int mask) {
        // allow kUnknown or a valid mask
        SkASSERT(kUnknown_Mask == mask || (mask & kAllMasks) == mask ||
                 ((kUnknown_Mask | kOnlyPerspectiveValid_Mask | kPerspective_Mask) & mask)
                 == mask);
        fTypeMask = SkToU8(mask);
    }


    SkScalar         fMat[9];
    mutable uint32_t fTypeMask;
};

struct SkIPoint {
    int32_t fX, fY;
    int32_t x() const { return fX; }
    int32_t y() const { return fY; }
};

struct SK_API SkPoint {
    SkScalar fX, fY;
};

struct SK_API SkIRect {
    int32_t fLeft, fTop, fRight, fBottom;
    static SkIRect MakeWH(int32_t w, int32_t h) {
        SkIRect r;
        r.set(0, 0, w, h);
        return r;
    }
    static SkIRect MakeXYWH(int32_t x, int32_t y, int32_t w, int32_t h) {
        SkIRect r;
        r.set(x, y, x + w, y + h);
        return r;
    }
    static bool Intersects(const SkIRect& a, const SkIRect& b) {
        return  !a.isEmpty() && !b.isEmpty() &&              // check for empties
                a.fLeft < b.fRight && b.fLeft < a.fRight &&
                a.fTop < b.fBottom && b.fTop < a.fBottom;
    }
    bool intersect(const SkIRect& a, const SkIRect& b) {
        SkASSERT(&a && &b);

        if (!a.isEmpty() && !b.isEmpty() &&
            a.fLeft < b.fRight && b.fLeft < a.fRight &&
            a.fTop < b.fBottom && b.fTop < a.fBottom) {
            fLeft   = SkMax32(a.fLeft,   b.fLeft);
            fTop    = SkMax32(a.fTop,    b.fTop);
            fRight  = SkMin32(a.fRight,  b.fRight);
            fBottom = SkMin32(a.fBottom, b.fBottom);
            return true;
        }
        return false;
    }

    bool isEmpty() const { return fLeft >= fRight || fTop >= fBottom; }

    void set(int32_t left, int32_t top, int32_t right, int32_t bottom) {
        fLeft   = left;
        fTop    = top;
        fRight  = right;
        fBottom = bottom;
    }
    void offset(int32_t dx, int32_t dy) {
        fLeft   += dx;
        fTop    += dy;
        fRight  += dx;
        fBottom += dy;
    }
    int width() const { return fRight - fLeft; }
    int height() const { return fBottom - fTop; }
    void setEmpty() { memset(this, 0, sizeof(*this)); }
    bool contains(const SkIRect& r) const {
        return  !r.isEmpty() && !this->isEmpty() &&     // check for empties
                fLeft <= r.fLeft && fTop <= r.fTop &&
                fRight >= r.fRight && fBottom >= r.fBottom;
    }

};

struct SK_API SkRect {
    SkScalar fLeft, fTop, fRight, fBottom;
};

template <typename T> struct SkTSize {
    T fWidth;
    T fHeight;

    static SkTSize Make(T w, T h) {
        SkTSize s;
        s.fWidth = w;
        s.fHeight = h;
        return s;
    }
    void set(T w, T h) {
        fWidth = w;
        fHeight = h;
    }

    T width() const { return fWidth; }
    T height() const { return fHeight; }
    bool equals(T w, T h) const {
        return fWidth == w && fHeight == h;
    }
};

template <typename T>
static inline bool operator==(const SkTSize<T>& a, const SkTSize<T>& b) {
    return a.fWidth == b.fWidth && a.fHeight == b.fHeight;
}

template <typename T>
static inline bool operator!=(const SkTSize<T>& a, const SkTSize<T>& b) {
    return !(a == b);
}

///////////////////////////////////////////////////////////////////////////////

typedef SkTSize<int32_t> SkISize;

/** Call obj->ref() and return obj. The obj must not be NULL.
 */
template <typename T> static inline T* SkRef(T* obj) {
    SkASSERT(obj);
    obj->ref();
    return obj;
}

/** Check if the argument is non-null, and if so, call obj->ref() and return obj.
 */
template <typename T> static inline T* SkSafeRef(T* obj) {
    if (obj) {
        obj->ref();
    }
    return obj;
}

/** Check if the argument is non-null, and if so, call obj->unref()
 */
template <typename T> static inline void SkSafeUnref(T* obj) {
    if (obj) {
        obj->unref();
    }
}

template <typename T> inline void SkTSwap(T& a, T& b) {
    T c(a);
    a = b;
    b = c;
}

#ifdef SK_SCALAR_SLOW_COMPARES
typedef int32_t SkScalarCompareType;
    typedef SkIRect SkRectCompareType;
    #define SkScalarToCompareType(x)    SkScalarAs2sCompliment(x)
#else
typedef SkScalar SkScalarCompareType;
typedef SkRect SkRectCompareType;
#define SkScalarToCompareType(x)    (x)
#endif

#define SkIsAlign4(x)   (0 == ((x) & 3))
#define SkDEBUGCODE(code)


class SK_API SkNoncopyable {
public:
    SkNoncopyable() {}

private:
    SkNoncopyable(const SkNoncopyable&);
    SkNoncopyable& operator=(const SkNoncopyable&);
};

template <size_t N, typename T> class SK_API SkAutoSTMalloc : SkNoncopyable {
public:
    SkAutoSTMalloc(size_t count) {
        if (count <= N) {
            fPtr = fTStorage;
        } else {
            fPtr = (T*)sk_malloc(count * sizeof(T));
        }
    }

    ~SkAutoSTMalloc() {
        if (fPtr != fTStorage) {
            sk_free(fPtr);
        }
    }

    // doesn't preserve contents
    void reset(size_t count) {
        if (fPtr != fTStorage) {
            sk_free(fPtr);
        }
        if (count <= N) {
            fPtr = fTStorage;
        } else {
            fPtr = (T*)sk_malloc(count * sizeof(T));
        }
    }

    T* get() const { return fPtr; }

    operator T*() {
        return fPtr;
    }

    operator const T*() const {
        return fPtr;
    }

    T& operator[](int index) {
        return fPtr[index];
    }

    const T& operator[](int index) const {
        return fPtr[index];
    }

private:
    T*          fPtr;
    union {
        uint32_t    fStorage32[(N*sizeof(T) + 3) >> 2];
        T           fTStorage[1];   // do NOT want to invoke T::T()
    };
};

class SkWriter32 : SkNoncopyable {
    struct BlockHeader;
public:
    /**
     *  The caller can specify an initial block of storage, which the caller manages.
     *  SkWriter32 will not attempt to free this in its destructor. It is up to the
     *  implementation to decide if, and how much, of the storage to utilize, and it
     *  is possible that it may be ignored entirely.
     */
    SkWriter32(size_t minSize, void* initialStorage, size_t storageSize);

    SkWriter32(size_t minSize)
            : fHead(NULL)
            , fTail(NULL)
            , fMinSize(minSize)
            , fSize(0)
            , fWrittenBeforeLastBlock(0)
    {}

    ~SkWriter32();

    uint32_t* reserve(size_t size) {
        SkASSERT(SkAlign4(size) == size);

        Block* block = fTail;
        if (NULL == block || block->available() < size) {
            block = this->doReserve(size);
        }
        fSize += size;
        return block->alloc(size);
    }

    void writeIRect(const SkIRect& rect) {
        *(SkIRect*)this->reserve(sizeof(rect)) = rect;
    }

    uint32_t bytesWritten() const { return fSize; }
    uint32_t  size() const { return this->bytesWritten(); }
    void flatten(void* dst) const;
    void reset();
    void reset(void* storage, size_t size);
private:
    struct Block {
        Block*  fNext;
        char*   fBasePtr;
        size_t  fSizeOfBlock;      // total space allocated (after this)
        size_t  fAllocatedSoFar;    // space used so far

        size_t  available() const { return fSizeOfBlock - fAllocatedSoFar; }
        char*   base() { return fBasePtr; }
        const char* base() const { return fBasePtr; }

        uint32_t* alloc(size_t size) {
            SkASSERT(SkAlign4(size) == size);
            SkASSERT(this->available() >= size);
            void* ptr = this->base() + fAllocatedSoFar;
            fAllocatedSoFar += size;
            SkASSERT(fAllocatedSoFar <= fSizeOfBlock);
            return (uint32_t*)ptr;
        }

        uint32_t* peek32(size_t offset) {
            SkASSERT(offset <= fAllocatedSoFar + 4);
            void* ptr = this->base() + offset;
            return (uint32_t*)ptr;
        }

        void rewind() {
            fNext = NULL;
            fAllocatedSoFar = 0;
            // keep fSizeOfBlock as is
        }

        static Block* Create(size_t size) {
            SkASSERT(SkIsAlign4(size));
            Block* block = (Block*)sk_malloc(sizeof(Block) + size);
            block->fNext = NULL;
            block->fBasePtr = (char*)(block + 1);
            block->fSizeOfBlock = size;
            block->fAllocatedSoFar = 0;
            return block;
        }

        Block* initFromStorage(void* storage, size_t size) {
            SkASSERT(SkIsAlign4((intptr_t)storage));
            SkASSERT(SkIsAlign4(size));
            Block* block = this;
            block->fNext = NULL;
            block->fBasePtr = (char*)storage;
            block->fSizeOfBlock = size;
            block->fAllocatedSoFar = 0;
            return block;
        }
    };

    enum {
        MIN_BLOCKSIZE = sizeof(SkWriter32::Block) + sizeof(intptr_t)
    };

    Block       fExternalBlock;
    Block*      fHead;
    Block*      fTail;
    size_t      fMinSize;
    uint32_t    fSize;
    // sum of bytes written in all blocks *before* fTail
    uint32_t    fWrittenBeforeLastBlock;

    bool isHeadExternallyAllocated() const {
        return fHead == &fExternalBlock;
    }

    Block* newBlock(size_t bytes);

    // only call from reserve()
    Block* doReserve(size_t bytes);
};






#endif //SKIA_COMMON_H__
