//
// Created by gaoqingguang on 2017/1/12.
//
#include <math.h>
#include "SkiaCommon.h"



int32_t sk_atomic_inc(int32_t* addr)
{
    int32_t value = *addr;
    *addr = value + 1;
    return value;
}

int32_t sk_atomic_dec(int32_t* addr)
{
    int32_t value = *addr;
    *addr = value - 1;
    return value;
}


void sk_free(void* p) {
    free(p);
}
void* sk_malloc(size_t size) {
    return malloc(size);
}


////////////////////////////////////////////////////////
// class SkMatrix
////////////////////////////////////////////////////////
#if 1 //def SK_SCALAR_IS_FLOAT
#define kMatrix22Elem   SK_Scalar1

static const int32_t kScalar1Int = 0x3f800000;
static const int32_t kPersp1Int  = 0x3f800000;
static inline float SkDoubleToFloat(double x) {
    return static_cast<float>(x);
}

typedef double SkDetScalar;
#define SkPerspMul(a, b)            SkScalarMul(a, b)
#define SkScalarMulShift(a, b, s)   SkDoubleToFloat((a) * (b))
static inline int fixmuladdmul(float a, float b, float c, float d,
                                   float* result) {
        *result = SkDoubleToFloat((double)a * b + (double)c * d);
        return true;
}

static inline bool rowcol3(const float row[], const float col[],
                           float* result) {
    *result = row[0] * col[0] + row[1] * col[3] + row[2] * col[6];
    return true;
}

static inline int negifaddoverflows(float& result, float a, float b) {
    result = a + b;
    return 0;
}
#endif

enum {
    kTranslate_Shift,
    kScale_Shift,
    kAffine_Shift,
    kPerspective_Shift,
    kRectStaysRect_Shift
};


static void normalize_perspective(SkScalar mat[9]) {
    if (SkScalarAbs(mat[SkMatrix::kMPersp2]) > kMatrix22Elem) {
        for (int i = 0; i < 9; i++)
            mat[i] = SkScalarHalf(mat[i]);
    }
}


bool SkMatrix::setConcat(const SkMatrix& a, const SkMatrix& b) {
    TypeMask aType = a.getPerspectiveTypeMaskOnly();
    TypeMask bType = b.getPerspectiveTypeMaskOnly();

    if (a.isTriviallyIdentity()) {
        *this = b;
    } else if (b.isTriviallyIdentity()) {
        *this = a;
    } else {
        SkMatrix tmp;

        if ((aType | bType) & kPerspective_Mask) {
            if (!rowcol3(&a.fMat[0], &b.fMat[0], &tmp.fMat[kMScaleX])) {
                return false;
            }
            if (!rowcol3(&a.fMat[0], &b.fMat[1], &tmp.fMat[kMSkewX])) {
                return false;
            }
            if (!rowcol3(&a.fMat[0], &b.fMat[2], &tmp.fMat[kMTransX])) {
                return false;
            }

            if (!rowcol3(&a.fMat[3], &b.fMat[0], &tmp.fMat[kMSkewY])) {
                return false;
            }
            if (!rowcol3(&a.fMat[3], &b.fMat[1], &tmp.fMat[kMScaleY])) {
                return false;
            }
            if (!rowcol3(&a.fMat[3], &b.fMat[2], &tmp.fMat[kMTransY])) {
                return false;
            }

            if (!rowcol3(&a.fMat[6], &b.fMat[0], &tmp.fMat[kMPersp0])) {
                return false;
            }
            if (!rowcol3(&a.fMat[6], &b.fMat[1], &tmp.fMat[kMPersp1])) {
                return false;
            }
            if (!rowcol3(&a.fMat[6], &b.fMat[2], &tmp.fMat[kMPersp2])) {
                return false;
            }

            normalize_perspective(tmp.fMat);
            tmp.setTypeMask(kUnknown_Mask);
        } else {    // not perspective
            if (!fixmuladdmul(a.fMat[kMScaleX], b.fMat[kMScaleX],
                              a.fMat[kMSkewX], b.fMat[kMSkewY], &tmp.fMat[kMScaleX])) {
                return false;
            }
            if (!fixmuladdmul(a.fMat[kMScaleX], b.fMat[kMSkewX],
                              a.fMat[kMSkewX], b.fMat[kMScaleY], &tmp.fMat[kMSkewX])) {
                return false;
            }
            if (!fixmuladdmul(a.fMat[kMScaleX], b.fMat[kMTransX],
                              a.fMat[kMSkewX], b.fMat[kMTransY], &tmp.fMat[kMTransX])) {
                return false;
            }
            if (negifaddoverflows(tmp.fMat[kMTransX], tmp.fMat[kMTransX],
                                  a.fMat[kMTransX]) < 0) {
                return false;
            }

            if (!fixmuladdmul(a.fMat[kMSkewY], b.fMat[kMScaleX],
                              a.fMat[kMScaleY], b.fMat[kMSkewY], &tmp.fMat[kMSkewY])) {
                return false;
            }
            if (!fixmuladdmul(a.fMat[kMSkewY], b.fMat[kMSkewX],
                              a.fMat[kMScaleY], b.fMat[kMScaleY], &tmp.fMat[kMScaleY])) {
                return false;
            }
            if (!fixmuladdmul(a.fMat[kMSkewY], b.fMat[kMTransX],
                              a.fMat[kMScaleY], b.fMat[kMTransY], &tmp.fMat[kMTransY])) {
                return false;
            }
            if (negifaddoverflows(tmp.fMat[kMTransY], tmp.fMat[kMTransY],
                                  a.fMat[kMTransY]) < 0) {
                return false;
            }

            tmp.fMat[kMPersp0] = tmp.fMat[kMPersp1] = 0;
            tmp.fMat[kMPersp2] = kMatrix22Elem;
            //SkDebugf("Concat mat non-persp type: %d\n", tmp.getType());
            //SkASSERT(!(tmp.getType() & kPerspective_Mask));
            tmp.setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
        }
        *this = tmp;
    }
    return true;
}

uint8_t SkMatrix::computePerspectiveTypeMask() const {
    unsigned mask = kOnlyPerspectiveValid_Mask | kUnknown_Mask;

    if (SkScalarAs2sCompliment(fMat[kMPersp0]) |
        SkScalarAs2sCompliment(fMat[kMPersp1]) |
        (SkScalarAs2sCompliment(fMat[kMPersp2]) - kPersp1Int)) {
        mask |= kPerspective_Mask;
    }

    return SkToU8(mask);
}
bool SkMatrix::postTranslate(SkScalar dx, SkScalar dy) {
    if (this->hasPerspective()) {
        SkMatrix    m;
        m.setTranslate(dx, dy);
        return this->postConcat(m);
    }

    if (SkScalarToCompareType(dx) || SkScalarToCompareType(dy)) {
        fMat[kMTransX] += dx;
        fMat[kMTransY] += dy;
        this->setTypeMask(kUnknown_Mask | kOnlyPerspectiveValid_Mask);
    }
    return true;
}

void SkMatrix::setTranslate(SkScalar dx, SkScalar dy) {
    if (SkScalarToCompareType(dx) || SkScalarToCompareType(dy)) {
        fMat[kMTransX] = dx;
        fMat[kMTransY] = dy;

        fMat[kMScaleX] = fMat[kMScaleY] = SK_Scalar1;
        fMat[kMSkewX]  = fMat[kMSkewY] =
        fMat[kMPersp0] = fMat[kMPersp1] = 0;
        fMat[kMPersp2] = kMatrix22Elem;

        this->setTypeMask(kTranslate_Mask | kRectStaysRect_Mask);
    } else {
        this->reset();
    }
}
bool SkMatrix::postConcat(const SkMatrix& mat) {
    // check for identity first, so we don't do a needless copy of ourselves
    // to ourselves inside setConcat()
    return mat.isIdentity() || this->setConcat(mat, *this);
}

void SkMatrix::reset() {
    fMat[kMScaleX] = fMat[kMScaleY] = SK_Scalar1;
    fMat[kMSkewX]  = fMat[kMSkewY] =
    fMat[kMTransX] = fMat[kMTransY] =
    fMat[kMPersp0] = fMat[kMPersp1] = 0;
    fMat[kMPersp2] = kMatrix22Elem;

    this->setTypeMask(kIdentity_Mask | kRectStaysRect_Mask);
}

uint8_t SkMatrix::computeTypeMask() const {
    unsigned mask = 0;

#ifdef SK_SCALAR_SLOW_COMPARES
    if (SkScalarAs2sCompliment(fMat[kMPersp0]) |
            SkScalarAs2sCompliment(fMat[kMPersp1]) |
            (SkScalarAs2sCompliment(fMat[kMPersp2]) - kPersp1Int)) {
        mask |= kPerspective_Mask;
    }

    if (SkScalarAs2sCompliment(fMat[kMTransX]) |
            SkScalarAs2sCompliment(fMat[kMTransY])) {
        mask |= kTranslate_Mask;
    }
#else
    // Benchmarking suggests that replacing this set of SkScalarAs2sCompliment
    // is a win, but replacing those below is not. We don't yet understand
    // that result.
    if (fMat[kMPersp0] != 0 || fMat[kMPersp1] != 0 ||
        fMat[kMPersp2] != kMatrix22Elem) {
        mask |= kPerspective_Mask;
    }

    if (fMat[kMTransX] != 0 || fMat[kMTransY] != 0) {
        mask |= kTranslate_Mask;
    }
#endif

    int m00 = SkScalarAs2sCompliment(fMat[SkMatrix::kMScaleX]);
    int m01 = SkScalarAs2sCompliment(fMat[SkMatrix::kMSkewX]);
    int m10 = SkScalarAs2sCompliment(fMat[SkMatrix::kMSkewY]);
    int m11 = SkScalarAs2sCompliment(fMat[SkMatrix::kMScaleY]);

    if (m01 | m10) {
        mask |= kAffine_Mask;
    }

    if ((m00 - kScalar1Int) | (m11 - kScalar1Int)) {
        mask |= kScale_Mask;
    }

    if ((mask & kPerspective_Mask) == 0) {
        // map non-zero to 1
        m00 = m00 != 0;
        m01 = m01 != 0;
        m10 = m10 != 0;
        m11 = m11 != 0;

        // record if the (p)rimary and (s)econdary diagonals are all 0 or
        // all non-zero (answer is 0 or 1)
        int dp0 = (m00 | m11) ^ 1;  // true if both are 0
        int dp1 = m00 & m11;        // true if both are 1
        int ds0 = (m01 | m10) ^ 1;  // true if both are 0
        int ds1 = m01 & m10;        // true if both are 1

        // return 1 if primary is 1 and secondary is 0 or
        // primary is 0 and secondary is 1
        mask |= ((dp0 & ds1) | (dp1 & ds0)) << kRectStaysRect_Shift;
    }

    return SkToU8(mask);
}

////////////////////////////////////////////////////////
// class SkWriter32
////////////////////////////////////////////////////////
SkWriter32::SkWriter32(size_t minSize, void* storage, size_t storageSize) {
    fMinSize = minSize;
    fSize = 0;
    fWrittenBeforeLastBlock = 0;
    fHead = fTail = NULL;

    if (storageSize) {
        this->reset(storage, storageSize);
    }
}

SkWriter32::~SkWriter32() {
    this->reset();
}


void SkWriter32::reset() {
    Block* block = fHead;

    if (this->isHeadExternallyAllocated()) {
        SkASSERT(block);
        // don't 'free' the first block, since it is owned by the caller
        block = block->fNext;
    }
    while (block) {
        Block* next = block->fNext;
        sk_free(block);
        block = next;
    }

    fSize = 0;
    fWrittenBeforeLastBlock = 0;
    fHead = fTail = NULL;
}

void SkWriter32::reset(void* storage, size_t storageSize) {
    this->reset();

    storageSize &= ~3;  // trunc down to multiple of 4
    if (storageSize > 0 && SkIsAlign4((intptr_t)storage)) {
        fHead = fTail = fExternalBlock.initFromStorage(storage, storageSize);
    }
}

void SkWriter32::flatten(void* dst) const {
    const Block* block = fHead;
    //SkDEBUGCODE(size_t total = 0;)

    while (block) {
        size_t allocated = block->fAllocatedSoFar;
        memcpy(dst, block->base(), allocated);
        dst = (char*)dst + allocated;
        block = block->fNext;

        SkDEBUGCODE(total += allocated;)
        SkASSERT(total <= fSize);
    }
    SkASSERT(total == fSize);
}

SkWriter32::Block* SkWriter32::doReserve(size_t size) {
    SkASSERT(SkAlign4(size) == size);

    Block* block = fTail;
    SkASSERT(NULL == block || block->available() < size);

    if (NULL == block) {
        SkASSERT(NULL == fHead);
        fHead = fTail = block = Block::Create(SkMax32(size, fMinSize));
        SkASSERT(0 == fWrittenBeforeLastBlock);
    } else {
        SkASSERT(fSize > 0);
        fWrittenBeforeLastBlock = fSize;

        fTail = Block::Create(SkMax32(size, fMinSize));
        block->fNext = fTail;
        block = fTail;
    }
    return block;
}
